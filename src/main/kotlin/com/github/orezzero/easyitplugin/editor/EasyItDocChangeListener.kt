package com.github.orezzero.easyitplugin.editor

import com.github.orezzero.easyitplugin.index.file.IndexManager
import com.github.orezzero.easyitplugin.index.file.entry.SimpleLocation
import com.github.orezzero.easyitplugin.md.MarkdownLanguageUtils.isMarkdownType
import com.github.orezzero.easyitplugin.runUndoTransparentWriteAction
import com.github.orezzero.easyitplugin.ui.gutter.EasyItGutterManager
import com.github.orezzero.easyitplugin.ui.gutter.EasyItGutterManagerImpl
import com.github.orezzero.easyitplugin.util.FileUtils
import com.github.orezzero.easyitplugin.util.LocationUtils
import com.github.orezzero.easyitplugin.util.MarkdownElementUtils
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.BulkAwareDocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.testFramework.LightVirtualFile

class EasyItDocChangeListener(private val project: Project) : BulkAwareDocumentListener.Simple {

    val manager = EasyItGutterManager.getInstance(project)
    val indexManager = IndexManager.getInstance(project)

    override fun afterDocumentChange(document: Document) {
        val file = FileDocumentManager.getInstance().getFile(document) ?: return
        if (file is LightVirtualFile) return

        if (file.fileType.isMarkdownType()) {
            indexManager.index(file)
            return
        }

        val map = sortedMapOf<SimpleLocation, Int>(compareBy { it.line })
        val set = mutableSetOf<Int>()
        val location2render = manager.getLocation2Render() // todo 用 indexManager 中的方法
        val relativePath = FileUtils.getRelativePath(project, file)
        for (entry in location2render.entries) {
            if (entry.key.path == relativePath) {
                val rangeMarker = entry.value.myRenderer.highlighter
                val targetLine =
                    rangeMarker?.let { if (it.isValid) it.document.getLineNumber(it.startOffset) + 1 else null } ?: -1
                when (entry.key.line) {
                    targetLine -> set.add(targetLine)
                    else -> map[entry.key] = targetLine
                }
            }
        }
        if (map.isEmpty()) return

        // collect what to change
        val elementToChange = mutableMapOf<PsiElement, String>()
        map.forEach { (location, line) ->
            when {
                line < 0 || set.contains(line) -> null
                else -> {
                    set.add(line)
                    location2render[location]?.let {
                        collectChangeItem(it, line, elementToChange)
                    }
                }
            }
        }


        val changeMdFiles = mutableListOf<VirtualFile>()
        // do change
        runUndoTransparentWriteAction {
            elementToChange.forEach { (ele, str) ->
                println("change ${ele.text} to $str")
                val doc = PsiDocumentManager.getInstance(project).getDocument(ele.containingFile)
                val virtualFile = ele.containingFile.virtualFile
                ele.replace(MarkdownElementUtils.createMarkdownText(project, str))
                PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(doc!!)
                changeMdFiles.add(virtualFile)
            }
        }
        indexManager.index(changeMdFiles)

    }

    private fun collectChangeItem(
        render: EasyItGutterManagerImpl.Render,
        line: Int,
        resultMap: MutableMap<PsiElement, String>
    ) {
        render.linkLocations.forEach { linkLocation ->
            val mdFile = FileUtils.findFileByRelativePath(project, linkLocation.location)!!
            val newPathString = manager.getCodeLocation(linkLocation)!!.let { codeLocation ->
                LocationUtils.toDest(project, codeLocation).copy(line = line)
                    .toString(mdFile)
            }
            resultMap[linkLocation.linkDest!!.firstChild] = newPathString
        }
    }
}
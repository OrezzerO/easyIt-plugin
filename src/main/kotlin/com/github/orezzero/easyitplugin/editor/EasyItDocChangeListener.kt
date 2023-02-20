package com.github.orezzero.easyitplugin.editor

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.SimpleLocation
import com.github.orezzero.easyitplugin.runUndoTransparentWriteAction
import com.github.orezzero.easyitplugin.ui.gutter.EasyItManager
import com.github.orezzero.easyitplugin.ui.gutter.EasyItManagerImpl
import com.github.orezzero.easyitplugin.util.FileUtils
import com.github.orezzero.easyitplugin.util.LocationUtils
import com.github.orezzero.easyitplugin.util.MarkdownElementUtils
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.BulkAwareDocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.testFramework.LightVirtualFile

class EasyItDocChangeListener(private val project: Project) : BulkAwareDocumentListener.Simple {

    val manager = EasyItManager.getInstance(project)!!

    override fun afterDocumentChange(document: Document) {
        val file = FileDocumentManager.getInstance().getFile(document) ?: return
        if (file is LightVirtualFile) return

        val map = sortedMapOf<SimpleLocation, Int>(compareBy { it.line })
        val set = mutableSetOf<Int>()
        val location2render = manager.getLocation2Render()
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
        val toModify = mutableMapOf<PsiElement, IndexEntry>()
        map.forEach { (location, line) ->
            when {
                line < 0 || set.contains(line) -> null
                else -> {
                    set.add(line)
                    location2render[location]?.let {
                        collectChangeItem(it, line, elementToChange, toModify)
                    }
                }
            }
        }


        // do change
        runUndoTransparentWriteAction {
            elementToChange.forEach { (ele, str) ->
                toModify[ele]?.let {
                    val originParent = ele.parent
                    println("change ${ele.text} to $str, origin offset: ${it.startOffset}- ${it.endOffset}")

                    val doc = PsiDocumentManager.getInstance(project).getDocument(ele.containingFile)
                    ele.replace(MarkdownElementUtils.createMarkdownText(project, str))
                    var oldKey = it.copy()
                    val newKey = it.copy(
                        startOffset = originParent.firstChild.startOffset,
                        endOffset = originParent.firstChild.endOffset
                    )
                    manager.refreshCache(oldKey, newKey)
                    it.startOffset = newKey.startOffset
                    it.endOffset = newKey.endOffset
                    PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(doc!!)
                    println("after change: ${it.startOffset}- ${it.endOffset}")


                }
            }
        }
    }

    private fun collectChangeItem(
        render: EasyItManagerImpl.Render,
        line: Int,
        resultMap: MutableMap<PsiElement, String>,
        modifyMap: MutableMap<PsiElement, IndexEntry>
    ) {
        render.linkLocations.forEach { linkLocation ->
            linkLocation.let {
                val startOffset = it.startOffset
                var newPathString = ""
                FileUtils.findFileByRelativePath(project, it.location)?.let { mdFile ->
                    // todo : ensure doc not change since now
                    val psiFile = PsiManager.getInstance(project).findFile(mdFile)
                    manager.getCodeLocation(it)?.let { codeLocation ->
                        LocationUtils.toDest(project, codeLocation).copy(line = line)
                            .toString(mdFile) // TODO: use cache
                    }?.let { str ->
                        newPathString = str
                    }
                    psiFile?.findElementAt(startOffset)
                }?.let { ele ->
                    resultMap[ele] = newPathString
                    modifyMap[ele] = it
                }

            }
        }
    }
}
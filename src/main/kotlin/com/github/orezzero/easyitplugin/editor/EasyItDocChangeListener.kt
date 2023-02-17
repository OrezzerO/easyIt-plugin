package com.github.orezzero.easyitplugin.editor

import com.github.orezzero.easyitplugin.index.file.entry.SimpleLocation
import com.github.orezzero.easyitplugin.ui.gutter.EasyItManager
import com.github.orezzero.easyitplugin.ui.gutter.EasyItManagerImpl
import com.github.orezzero.easyitplugin.util.FileUtils
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.BulkAwareDocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightVirtualFile

class EasyItDocChangeListener(private val project: Project) : BulkAwareDocumentListener.Simple {

    override fun afterDocumentChange(document: Document) {
        val file = FileDocumentManager.getInstance().getFile(document) ?: return
        if (file is LightVirtualFile) return


        val manager = EasyItManager.getInstance(project) ?: return
        val map = sortedMapOf<SimpleLocation, Int>(compareBy { it.line })
        val set = mutableSetOf<Int>()
        val location2render = manager.getLocation2Render()
        val relativePath = FileUtils.getRelativePath(project, file)
        for (entry in location2render.entries) {
            if (entry.key.path == relativePath) {
                val rangeMarker = entry.value.myRenderer.highlighter
                val line =
                    rangeMarker?.let { if (it.isValid) it.document.getLineNumber(it.startOffset) + 1 else null } ?: -1
                when (entry.key.line) {
                    line -> set.add(line)
                    else -> map[entry.key] = line
                }
            }
        }
        if (map.isEmpty()) return
        WriteCommandAction.runWriteCommandAction(project) {
            map.forEach { (location, line) ->
                when {
                    line < 0 || set.contains(line) -> null
                    else -> {
                        set.add(line)
                        update(location2render[location], location, line)
                    }
                }
            }
        }
    }

    private fun update(render: EasyItManagerImpl.Render?, codeLocation: SimpleLocation, line: Int) {
        render?.linkLocations?.sortedBy { it.startOffset }?.reversed()?.forEach { loca ->
            loca.let {
                val startOffset = it.startOffset
                val endOffset = it.endOffset
                FileUtils.findFileByRelativePath(project, it.location)?.let { mdFile ->
                    val doc = FileDocumentManager.getInstance().getCachedDocument(mdFile)
                    // fixme:   这里有个问题, 连续变动之后, startOffset 和 endoffset 没有变, 导致了 replace 有问题
                    // 也不能简单的修改 loca 的 offset 来修改, 因为一处replace 之后, 会影响之后的,要想办法统一修改(通过逆序)

                    var newPathString = codeLocation.copy(line = line).toString(project, mdFile)
                    doc?.replaceString(
                        startOffset,
                        endOffset,
                        newPathString
                    )
                    it.endOffset = it.startOffset + newPathString.length
                }

            }
        }
    }
}
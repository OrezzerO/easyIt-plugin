package com.github.orezzero.easyitplugin.ui.project.view


import com.intellij.ide.util.gotoByName.GotoFileCellRenderer.getRelativePath
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

data class Destination(
    val project: Project,
    val file: VirtualFile,
    val line: Int,
    val anchorAttributes: MutableMap<String, String>
) {
    val descriptor: OpenFileDescriptor = OpenFileDescriptor(project, file, line, 0)


    override fun toString(): String {
        val relativePath = getRelativePath( file,project)
        val anchorStr = getAnchorString()
        return "file://$relativePath?$anchorStr"
    }

    private fun getAnchorString(): String {
        val list = mutableListOf<String>()
        anchorAttributes.forEach { (key, value) ->
            if (key.isNotEmpty()) {
                when (key) {
                    "l" -> list.add("L$line")
                    value -> list.add(key)
                    else -> list.add("$key=$value")
                }
            }
        }
        return list.joinToString("&")
    }
}
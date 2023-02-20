package com.github.orezzero.easyitplugin.index.file.entry


import com.github.orezzero.easyitplugin.util.FileUtils
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

data class Dest(
    val name: String,
    val project: Project,
    val file: VirtualFile,
    val line: Int,
    val anchorAttributes: Map<String, String>,
    val descriptor: OpenFileDescriptor
) {
    fun toString(base: VirtualFile): String {
        val relativePath = FileUtils.getRelativePath(base, file)
        val anchorStr = getAnchorString()
        return "$relativePath#$anchorStr"
    }

    private fun getAnchorString(): String {
        val list = mutableListOf<String>()
        anchorAttributes.forEach { (key, value) ->
            if (key.isNotEmpty()) {
                when (key) {
                    "l" -> list.add("L$line")
                    "L" -> list.add("L$line")
                    value -> list.add(key)
                    else -> list.add("$key=$value")
                }
            }
        }
        return list.joinToString("&")
    }
}


package com.github.orezzero.easyitplugin.ui.project.view


import com.github.orezzero.easyitplugin.index.file.KeyValue
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
        return "$relativePath?$anchorStr"
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

    companion object {
        fun of(project: Project, base: VirtualFile, kv: KeyValue): Dest? {
            val file = FileUtils.findFileByRelativePath(base, kv.dest.substringBefore("#"))
            return file?.let {
                val anchorAttributes = parseAnchor(kv.dest.substringAfter("#"))
                val line = anchorAttributes["L"]?.let { Integer.valueOf(it) - 1 } ?: 0
                val descriptor = OpenFileDescriptor(project, file, line, 0)
                Dest(kv.name, project, file, line, anchorAttributes, descriptor)
            }
        }


        private fun parseAnchor(anchor: String): Map<String, String> {
            val anchorAttributes = mutableMapOf<String, String>()
            val split = anchor.split("&")
            for (s in split) {
                val equalPairs = s.split("=")
                when (equalPairs.size) {
                    1 -> parseSingleAnchor(s, anchorAttributes)
                    2 -> anchorAttributes[equalPairs[0]] = equalPairs[1]
                    else -> {}
                }
            }
            return anchorAttributes
        }

        fun parseSingleAnchor(s: String, anchorAttributes: MutableMap<String, String>) {
            var lastLetter = 0
            for ((index, c) in s.withIndex()) {
                if (c.isLetter()) {
                    lastLetter = index
                }
            }
            if (lastLetter == s.length - 1) {
                anchorAttributes[s] = s
            } else {
                anchorAttributes[s.substring(0, lastLetter + 1)] = s.substring(lastLetter + 1)
            }
        }

    }

}


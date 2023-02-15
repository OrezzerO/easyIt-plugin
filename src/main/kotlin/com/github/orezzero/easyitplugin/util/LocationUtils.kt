package com.github.orezzero.easyitplugin.util

import com.github.orezzero.easyitplugin.index.file.entry.Dest
import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.SimpleLocation
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

class LocationUtils {

    companion object {

        val SHARP = "#"
        fun toSimpleLocation(indexEntry: IndexEntry): SimpleLocation {
            val path = indexEntry.location.substringBefore(SHARP)
            val anchorAttributes = parseAnchor(indexEntry.location.substringAfter(SHARP))
            val line = getLineNum(anchorAttributes)
            return SimpleLocation(path, line)
        }


        fun getLineNum(anchorAttributes: Map<String, String>): Int {
            return anchorAttributes["L"]?.let { Integer.valueOf(it) - 1 } ?: 0
        }

        fun toDest(project: Project, base: VirtualFile, kv: IndexEntry): Dest? {
            val file = FileUtils.findFileByRelativePath(base, kv.location.substringBefore("#"))
            return file?.let {
                val anchorAttributes = parseAnchor(kv.location.substringAfter("#"))
                val line = getLineNum(anchorAttributes)
                val descriptor = OpenFileDescriptor(project, file, line, 0)
                Dest(kv.name, project, file, line, anchorAttributes, descriptor)
            }
        }

        fun toDest(project: Project, kv: IndexEntry): Dest? {
            return project.guessProjectDir()?.findFileByRelativePath(kv.location)?.let {
                toDest(project, it, kv)
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

        fun getFile(project: Project, relativePath: String): VirtualFile? {
            return project.guessProjectDir()?.findFileByRelativePath(relativePath)
        }


    }

}
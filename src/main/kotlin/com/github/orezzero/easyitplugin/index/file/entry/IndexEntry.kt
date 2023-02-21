package com.github.orezzero.easyitplugin.index.file.entry

import com.github.orezzero.easyitplugin.util.FileUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDestination

data class IndexEntry(val name: String, val location: String, val linkDest: MarkdownLinkDestination?) {
    companion object {
        fun of(
            name: String,
            relativePath: String,
            lineNum: Int,
            linkDest: MarkdownLinkDestination
        ): IndexEntry {
            val lineStr = "L$lineNum"
            val location = "$relativePath#$lineStr"
            return IndexEntry(name, location, linkDest)
        }
    }
}
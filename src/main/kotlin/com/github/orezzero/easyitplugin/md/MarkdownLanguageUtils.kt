package com.github.orezzero.easyitplugin.md

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.vfs.VirtualFile
import org.intellij.plugins.markdown.lang.MarkdownFileType
import org.intellij.plugins.markdown.lang.MarkdownLanguage

// todo latest code have made this class public in markdown plugin. Delete it when updating markdown plugin version
object MarkdownLanguageUtils {
    fun Language.isMarkdownLanguage(): Boolean {
        return this == MarkdownLanguage.INSTANCE
    }

    fun FileType.isMarkdownType(): Boolean {
        return this == MarkdownFileType.INSTANCE
    }

    fun VirtualFile.hasMarkdownType(): Boolean {
        return FileTypeRegistry.getInstance().isFileOfType(this, MarkdownFileType.INSTANCE)
    }
}

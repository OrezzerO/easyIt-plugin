package com.github.orezzero.easyitplugin.stub

import com.github.orezzero.easyitplugin.stub.StubUtils.additionalPlatformType
import org.intellij.markdown.MarkdownElementTypes


interface CustomMarkdownElementTypes {
    companion object {
        val INLINE_LINK = additionalPlatformType(MarkdownElementTypes.INLINE_LINK)
    }
}
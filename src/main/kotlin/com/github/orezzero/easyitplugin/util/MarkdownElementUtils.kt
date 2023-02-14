package com.github.orezzero.easyitplugin.util

import com.intellij.psi.tree.TokenSet
import org.intellij.plugins.markdown.lang.MarkdownTokenTypes
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDestination
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkText

object MarkdownElementUtils {
    fun getLinkDestinationText(markdownLinkDestination: MarkdownLinkDestination): String {
        return markdownLinkDestination.text
    }

    fun getLinkTextString(markdownLinkText: MarkdownLinkText?): String {
        val linkName = markdownLinkText?.let {
            it.node.getChildren(TokenSet.create(MarkdownTokenTypes.TEXT)).let { list ->
                if (list.isNotEmpty()) {
                    list[0].text
                } else {
                    "EMPTY"
                }
            }
        }
        return linkName ?: "EMPTY"
    }
}
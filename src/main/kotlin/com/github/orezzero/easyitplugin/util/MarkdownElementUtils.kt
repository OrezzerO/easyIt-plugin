package com.github.orezzero.easyitplugin.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.TokenSet
import org.intellij.plugins.markdown.lang.MarkdownFileType
import org.intellij.plugins.markdown.lang.MarkdownTokenTypes
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownInlineLink
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


    fun createInlineLink(project: Project, name: String): MarkdownInlineLink {
        val file = createFile(project, name)
        return file.firstChild.firstChild.firstChild as MarkdownInlineLink
    }

    fun createFile(project: Project, text: String): MarkdownFile {
        val name = "dummy.md"
        return PsiFileFactory.getInstance(project)
            .createFileFromText(name, MarkdownFileType.INSTANCE, text) as MarkdownFile
    }

    fun createInlineLink(project: Project, name: String, link: String): MarkdownInlineLink {
        return createInlineLink(project, "[$name]($link)")
    }

    fun createLineDestination(project: Project, dest: String): MarkdownLinkDestination {
        return createInlineLink(project, "[TEST]($dest)").linkDestination!!
    }

    fun createMarkdownText(project: Project, text: String): PsiElement {
        var file = createFile(project, text.trim())
        return file.firstChild.firstChild.firstChild
    }


}
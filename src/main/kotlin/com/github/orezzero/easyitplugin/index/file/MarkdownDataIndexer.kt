package com.github.orezzero.easyitplugin.index.file

import com.github.orezzero.easyitplugin.util.FileUtils.getRelativePathBaseOnProject
import com.github.orezzero.easyitplugin.util.MarkdownElementUtils
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileContent
import org.intellij.plugins.markdown.lang.psi.MarkdownRecursiveElementVisitor
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownInlineLink
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDestination

class MarkdownDataIndexer : DataIndexer<String, List<KeyValue>, FileContent> {
    override fun map(inputData: FileContent): Map<String, List<KeyValue>> {
        val file = inputData.file
        val psiFile = inputData.psiFile
        val project = psiFile.project
        val key = getRelativePathBaseOnProject(project, file)
        val value = mutableListOf<KeyValue>()
        psiFile.accept(object : MarkdownRecursiveElementVisitor() {
            override fun visitLinkDestination(linkDestination: MarkdownLinkDestination) {
                val linkDest = linkDestination.text
                val parent = linkDestination.parent
                if (parent is MarkdownInlineLink) {
                    val name = parent.linkText?.let {
                        MarkdownElementUtils.getLinkTextString(it)
                    } ?: ""
                    value.add(KeyValue(name, linkDest))
                }
            }
        })
        var result = mutableMapOf<String, List<KeyValue>>()
        result[key] = value
        return result
    }
}
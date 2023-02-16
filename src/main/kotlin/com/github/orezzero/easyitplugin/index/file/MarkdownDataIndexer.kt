package com.github.orezzero.easyitplugin.index.file

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.util.FileUtils
import com.github.orezzero.easyitplugin.util.MarkdownElementUtils
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileContent
import org.intellij.plugins.markdown.lang.psi.MarkdownRecursiveElementVisitor
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownInlineLink
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDestination

class MarkdownDataIndexer : DataIndexer<IndexEntry, IndexEntry, FileContent> {
    override fun map(inputData: FileContent): Map<IndexEntry, IndexEntry> {
        val mdFile = inputData.file
        val psiFile = inputData.psiFile
        val project = psiFile.project
        val result = mutableMapOf<IndexEntry, IndexEntry>()

        psiFile.accept(object : MarkdownRecursiveElementVisitor() {
            override fun visitLinkDestination(linkDestination: MarkdownLinkDestination) {
                val linkDest = linkDestination.text
                val parent = linkDestination.parent
                if (parent is MarkdownInlineLink) {
                    // 统一一下, IndexEntry 中的 path ,均为基于 project 的相对路径
                    val name = parent.linkText?.let {
                        MarkdownElementUtils.getLinkTextString(it)
                    } ?: ""
                    FileUtils.findFileByRelativePath(mdFile, linkDest.substringBefore("#"))?.let {
                        // calculate dest (code file)
                        val codeLocation = IndexEntry(
                            name,
                            FileUtils.getRelativePath(project, it) + "#" + linkDest.substringAfter("#")
                        )

                        // calculate src (md file)
                        val lineNum =
                            FileDocumentManager.getInstance().getDocument(mdFile)?.getLineNumber(parent.startOffset)
                                ?: 0
                        val linkLocation = IndexEntry.of(name, project, mdFile, lineNum)
                        result[linkLocation] = codeLocation
                        IndexListenerDispatcher.getInstance(project)?.indexChanged()
                    }


                }
            }
        })

        return result
    }
}
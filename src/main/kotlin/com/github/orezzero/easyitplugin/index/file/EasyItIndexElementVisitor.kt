package com.github.orezzero.easyitplugin.index.file

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.TreeNode
import com.github.orezzero.easyitplugin.index.file.entry.layer.Header
import com.github.orezzero.easyitplugin.index.file.entry.layer.ListItem
import com.github.orezzero.easyitplugin.md.MarkdownLanguageUtils.isMarkdownType
import com.github.orezzero.easyitplugin.util.FileUtils
import com.github.orezzero.easyitplugin.util.MarkdownElementUtils
import com.github.orezzero.easyitplugin.util.PriorityStack
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.startOffset
import org.intellij.plugins.markdown.lang.psi.MarkdownRecursiveElementVisitor
import org.intellij.plugins.markdown.lang.psi.impl.*

class EasyItIndexElementVisitor(
    private val project: Project,
    private val mdFile: VirtualFile,
) : MarkdownRecursiveElementVisitor() {

    val result = mutableMapOf<IndexEntry, IndexEntry>()
    val mdReferences = mutableListOf<VirtualFile>()
    val root = TreeNode(mdFile, null);
    private val markdownRelativePath = FileUtils.getRelativePath(project, mdFile)
    private val markdownLayer = MarkdownLayer()
    private var headerStack = PriorityStack<TreeNode<Header>> { node -> node.value.level }
    private var listStack = PriorityStack<TreeNode<ListItem>> { node -> node.value.level }


    override fun visitLinkDestination(linkDestination: MarkdownLinkDestination) {
        val linkDest = linkDestination.text
        val parent = linkDestination.parent
        if (parent is MarkdownInlineLink) {
            // 统一一下, IndexEntry 中的 path ,均为基于 project 的相对路径
            val name = parent.linkText.let {
                MarkdownElementUtils.getLinkTextString(it)
            }
            val traceId = markdownLayer.toString()
            println("$name:$traceId ")
            FileUtils.findFileByRelativePath(mdFile, linkDest)?.let {
                if (it.fileType.isMarkdownType()) {
                    mdReferences.add(it)
                }
                // calculate dest (code file)
                val codeLocation = IndexEntry(
                    name, FileUtils.getRelativePath(project, it) + "#" + linkDest.substringAfter("#"), null
                )
                // calculate src (md file)
                val lineNum =
                    FileDocumentManager.getInstance().getDocument(mdFile)?.getLineNumber(parent.startOffset) ?: 0
                val linkLocation = IndexEntry.of(
                    name, markdownRelativePath, lineNum + 1, linkDestination
                )
                result[linkLocation] = codeLocation
                val parent: TreeNode<*> = if (!listStack.isEmpty()) {
                    listStack.peek()
                } else if (!headerStack.isEmpty()) {
                    headerStack.peek()
                } else {
                    root
                }
                val link =
                    MarkdownTreeNodeFactory.buildLink(linkLocation, codeLocation, markdownLayer.getCurrentLayerId())
                val treeNode = TreeNode(link, parent)
                parent.addChild(treeNode)
            }
        }
    }


    override fun visitElement(element: PsiElement) {
        when (element) {
            is MarkdownListItem -> visitListItem(element)
        }
        super.visitElement(element)
    }

    private fun visitListItem(element: MarkdownListItem) {
        markdownLayer.acceptListItem(element)
        val listItem = MarkdownTreeNodeFactory.buildListItem(
            element, markdownLayer.listLayer.size, markdownLayer.getCurrentLayerId()
        )


        val parent: TreeNode<*> =
            listStack.popLowerThan(listItem.level) ?: if (headerStack.isEmpty()) root else headerStack.peek()

        val treeNode = TreeNode(listItem, parent)
        listStack.push(treeNode)
        parent.addChild(treeNode)
    }

    override fun visitList(list: MarkdownList) {
        markdownLayer.listStart(list)
        super.visitList(list)
        markdownLayer.listEnd(list)
        listStack.popLowerThan(markdownLayer.listLayer.size)
    }

    /**
     * Header
     */
    override fun visitHeader(markdownHeader: MarkdownHeader) {
        markdownLayer.acceptHeader(markdownHeader)
        val header = MarkdownTreeNodeFactory.buildHeader(
            markdownHeader, markdownLayer.getHeaderLayer(markdownHeader), markdownLayer.getCurrentLayerId()
        )

        val parent: TreeNode<*> = headerStack.popLowerThan(header.level) ?: root
        val treeNode = TreeNode(header, parent)
        headerStack.push(treeNode)
        parent.addChild(treeNode)

        super.visitHeader(markdownHeader)
    }

}
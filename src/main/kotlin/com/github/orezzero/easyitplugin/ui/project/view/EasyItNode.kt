@file:Suppress("UNCHECKED_CAST")

package com.github.orezzero.easyitplugin.ui.project.view

import com.github.orezzero.easyitplugin.index.file.entry.LayerId
import com.github.orezzero.easyitplugin.index.file.entry.TreeNode
import com.github.orezzero.easyitplugin.index.file.entry.layer.Header
import com.github.orezzero.easyitplugin.index.file.entry.layer.Link
import com.github.orezzero.easyitplugin.index.file.entry.layer.ListItem
import com.github.orezzero.easyitplugin.md.MarkdownLanguageUtils.isMarkdownType
import com.github.orezzero.easyitplugin.util.FileUtils
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile


abstract class EasyItNode<T> protected constructor(project: Project?, value: T, val layerId: LayerId) :
    ProjectViewNode<T>(project, value!!, ProjectViewSettings.Immutable.DEFAULT) {

    override fun contains(file: VirtualFile): Boolean {
        return false
    }

    override fun getSortKey(): Comparable<*> {
        return layerId
    }

    fun generateTreeNode(node: TreeNode<*>, nodes: MutableList<EasyItNode<*>>) {
        when (node.value) {
            is Link -> nodes.add(handleLink(node as TreeNode<Link>))
            is ListItem -> nodes.addAll(handleListItem(node as TreeNode<ListItem>))
            is Header -> nodes.add(handleHeader(node as TreeNode<Header>))
            else -> throw IllegalStateException("Unexpected treeNode value type: ${node.value?.javaClass?.name} ")
        }
    }

    private fun handleHeader(treeNode: TreeNode<Header>): EasyItNode<*> {
        return CommonEasyItNode(project, treeNode, treeNode.value.id, treeNode.value.navigatable)
    }


    private fun handleLink(node: TreeNode<Link>): EasyItNode<*> {
        val codeFile = findVirtualFile(node.value.code.location)
        return if (codeFile != null && codeFile != virtualFile && codeFile.fileType.isMarkdownType()) {
            EasyItFileNode(myProject, codeFile, node.value.code, node.value.id)
        } else {
            EasyItLinkNode(myProject, node.value.link, node.value.code, node.value.id)
        }
    }

    private fun handleListItem(node: TreeNode<ListItem>): List<EasyItNode<*>> {
        return if (node.getChildren().isNotEmpty()) {
            var parent: EasyItLinkNode? = null
            val result: MutableList<EasyItNode<*>> = mutableListOf()
            for (child in node.getChildren()) {
                if (child.value is Link) {
                    val easyItNode = handleLink(child as TreeNode<Link>)
                    result.add(easyItNode)
                    if (easyItNode is EasyItLinkNode) {
                        parent = easyItNode
                    }
                } else if (child.value is ListItem) {
                    val subResult = handleListItem(child as TreeNode<ListItem>)
                    if (parent != null) {
                        parent.addChildren(subResult)
                    } else {
                        result.addAll(subResult)
                    }
                } else {
                    throw IllegalStateException("Unresolved TreeNode type: ${child.value?.javaClass?.name} ")
                }
            }
            return result
        } else {
            listOf()
        }
    }

    private fun haveList(children: List<TreeNode<*>>): Boolean {
        for (child in children) {
            if (child.value is ListItem) {
                return true
            }
        }
        return false
    }

    fun findVirtualFile(location: String): VirtualFile? {
        return FileUtils.findFileByRelativePath(project, location.substringBefore("#"))
    }
}
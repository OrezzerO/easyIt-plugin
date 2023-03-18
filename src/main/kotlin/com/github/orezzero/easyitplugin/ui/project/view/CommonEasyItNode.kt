package com.github.orezzero.easyitplugin.ui.project.view

import com.github.orezzero.easyitplugin.index.file.entry.LayerId
import com.github.orezzero.easyitplugin.index.file.entry.TreeNode
import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.pom.Navigatable

class CommonEasyItNode<L>(
    project: Project,
    val value: TreeNode<L>,
    layerId: LayerId,
    val navigatable: Navigatable?,
) : EasyItNode<TreeNode<L>>(project, value, layerId) {
    override fun update(presentation: PresentationData) {
        presentation.setIcon(AllIcons.Nodes.BookmarkGroup)
        presentation.presentableText = value.value.toString()
    }

    override fun getChildren(): MutableCollection<out AbstractTreeNode<*>> {
        val nodes = mutableListOf<EasyItNode<*>>()
        value.getChildren().forEach {
            generateTreeNode(it, nodes)
        }
        return nodes
    }

    override fun canNavigate(): Boolean {
        return navigatable != null
    }

    override fun canNavigateToSource(): Boolean {
        return navigatable?.canNavigate() ?: false
    }

    override fun navigate(requestFocus: Boolean) {
        navigatable?.navigate(requestFocus)
    }

}
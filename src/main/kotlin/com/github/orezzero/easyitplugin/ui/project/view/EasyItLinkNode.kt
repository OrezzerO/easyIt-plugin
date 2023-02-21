package com.github.orezzero.easyitplugin.ui.project.view

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project

class EasyItLinkNode(project: Project, val codeLocation: IndexEntry) : EasyItNode<IndexEntry>(project, codeLocation) {
    override fun getChildren(): Collection<AbstractTreeNode<*>> {
        return ArrayList()
    }

    override fun update(presentation: PresentationData) {
        presentation.setIcon(AllIcons.Nodes.Method)
        presentation.presentableText = codeLocation.name
    }

    override fun canNavigate(): Boolean {
        return codeLocation.linkDest?.canNavigate() ?: false
    }

    override fun canNavigateToSource(): Boolean {
        return canNavigate()
    }

    override fun navigate(requestFocus: Boolean) {
        codeLocation.linkDest?.navigate(true)
    }

    // TODO attention: 这里重载了父类的 equals 和 hashcode, 父类的方法使用 value 作为比较的对象,
    //  这里修改之后,有可能造成其他问题
    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }


}
package com.github.orezzero.easyitplugin.ui.project.view

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project

class EasyItLinkNode(project: Project?, value: Value) : EasyItNode<Value?>(project, value) {

    override fun getChildren(): Collection<AbstractTreeNode<*>> {
        return ArrayList()
    }

    override fun update(presentation: PresentationData) {
        presentation.setIcon(AllIcons.Nodes.Method)
        presentation.presentableText = value!!.name
    }

    override fun canNavigate(): Boolean {
        return true
    }

    override fun canNavigateToSource(): Boolean {
        return canNavigate()
    }

    override fun navigate(requestFocus: Boolean) {
        value?.also {
            it.descriptor.navigate(true)
        }
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
package com.github.orezzero.easyitplugin.ui.gutter

import com.github.orezzero.easyitplugin.action.EasyItNavigateAction
import com.github.orezzero.easyitplugin.ui.project.view.EasyItLinkNode
import com.github.orezzero.easyitplugin.ui.project.view.EasyItNodeManagerImpl
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GutterDynamicActionGroup(private val info: EasyItNodeManagerImpl.Info) : ActionGroup() {
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val nodes: Set<EasyItLinkNode> = info.nodes
        return nodes.stream().map { node: EasyItLinkNode -> toAction(node) }.toArray { length -> arrayOfNulls(length) }
    }

    private fun toAction(node: EasyItLinkNode): AnAction {
        return EasyItNavigateAction(node.value!!)
    }
}
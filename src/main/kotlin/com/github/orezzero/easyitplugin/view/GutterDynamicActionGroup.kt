package com.github.orezzero.easyitplugin.view

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GutterDynamicActionGroup(private val info: EasyItNodeManagerImpl.Info) : ActionGroup() {
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val nodes: Set<EasyItLinkNode> = info.nodes
        return nodes.stream().map { node: EasyItLinkNode -> toAction(node) }.toArray { length -> arrayOfNulls(length) }
    }

    private fun toAction(node: EasyItLinkNode): AnAction {
        return EasyItNativeAction(node.value!!)
    }
}
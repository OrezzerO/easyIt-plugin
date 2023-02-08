package com.github.orezzero.easyitplugin.view

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class EasyItNativeAction(private val node: Node) : AnAction(node.name) {
    override fun actionPerformed(e: AnActionEvent) {
        val text = node.text
        text.navigate(true)
    }
}
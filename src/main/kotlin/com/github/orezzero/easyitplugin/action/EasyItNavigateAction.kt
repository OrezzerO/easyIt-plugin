package com.github.orezzero.easyitplugin.action

import com.github.orezzero.easyitplugin.ui.project.view.Value
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class EasyItNavigateAction(private val value: Value) : AnAction(value.name) {
    override fun actionPerformed(e: AnActionEvent) {
        val text = value.text
        text.navigate(true)
    }
}
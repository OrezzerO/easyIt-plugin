package com.github.orezzero.easyitplugin.ui.gutter

import com.github.orezzero.easyitplugin.action.EasyItNavigateAction
import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import java.util.*

class GutterDynamicActionGroup(private val renderer: EasyItGutterManagerImpl.Render, val project: Project) :
    ActionGroup() {
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val nodes = renderer.linkLocations
        return e?.project.let {
            nodes.stream().map { node: IndexEntry -> toAction(node) }.filter(Objects::nonNull)
                .toArray { length -> arrayOfNulls(length) }
        }
    }

    private fun toAction(entry: IndexEntry): AnAction {
        return EasyItNavigateAction(project, entry)
    }
}
package com.github.orezzero.easyitplugin.action


import com.intellij.ide.actions.SplitAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.fileEditor.impl.EditorWindow.Companion.DATA_KEY
import com.intellij.openapi.fileEditor.impl.MoveEditorToOppositeTabGroupAction
import javax.swing.SwingConstants

class MoveOrSplitAction : SplitAction(SwingConstants.VERTICAL, true) {

    val moveAction: MoveEditorToOppositeTabGroupAction = MoveEditorToOppositeTabGroupAction()
    override fun actionPerformed(event: AnActionEvent) {
        val dataContext = event.dataContext
        val vFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext)
        val project = CommonDataKeys.PROJECT.getData(dataContext)
        if (vFile == null || project == null) {
            return
        }
        val window: EditorWindow = DATA_KEY.getData(dataContext) ?: return

        val siblings = window.getSiblings()
        if (siblings.size == 1) {
            moveAction.actionPerformed(event)
        } else {
            super.actionPerformed(event)
        }

    }
}
package com.github.orezzero.easyitplugin.action

import com.github.orezzero.easyitplugin.util.FileUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class EasyItCreateAction : AnAction() {

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val editor = anActionEvent.getData(CommonDataKeys.EDITOR)
        val psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE)
        val project = anActionEvent.project
        if (editor == null || psiFile == null || project == null) return

        val currentVirtualFile = psiFile.virtualFile

        val caretModel = editor.caretModel
        val doc = editor.document
        val selectedText = caretModel.primaryCaret.selectedText
        if (selectedText.isNullOrBlank()) return
        val createFile = FileUtils.createFile(project, ".easyIt/$selectedText.md") ?: return
        val relativePath = FileUtils.getRelativePath(currentVirtualFile, createFile)


        WriteCommandAction.runWriteCommandAction(
            project
        ) {
            doc.replaceString(
                caretModel.primaryCaret.selectionStart,
                caretModel.primaryCaret.selectionEnd,
                "[$selectedText]($relativePath)"
            )
        }
    }
}
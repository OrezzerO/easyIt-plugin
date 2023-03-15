// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.github.orezzero.easyitplugin.action

import com.github.orezzero.easyitplugin.util.FileUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class EasyItInsertAction : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val pop = OneTimeRecorder.peek() ?: return
        val editor = anActionEvent.getData(CommonDataKeys.EDITOR)
        val psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE)
        if (editor == null || psiFile == null) {
            return
        }

        val currentVirtualFile = psiFile.virtualFile
        val path = FileUtils.getRelativePath(currentVirtualFile, pop.file)

        val caretModel = editor.caretModel
        val doc = editor.document
        val linkText = caretModel.primaryCaret.selectedText ?: pop.str

        WriteCommandAction.runWriteCommandAction(
            anActionEvent.project
        ) {
            doc.replaceString(
                caretModel.primaryCaret.selectionStart,
                caretModel.primaryCaret.selectionEnd,
                "[" + linkText + "](" + path + "#L" + pop.line + ")"
            )
        }
    }
}
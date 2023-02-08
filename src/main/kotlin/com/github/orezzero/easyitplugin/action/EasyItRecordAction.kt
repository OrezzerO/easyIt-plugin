// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.github.orezzero.easyitplugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class EasyItRecordAction : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val editor = anActionEvent.getData(CommonDataKeys.EDITOR)
        val psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE)
        if (editor == null || psiFile == null) {
            return
        }
        val caretModel = editor.caretModel
        val record = OneTimeRecorder.Record()
        record.offset = caretModel.offset
        record.text = caretModel.primaryCaret.selectedText
        record.selectedStart = caretModel.primaryCaret.selectionStart
        record.selectedEnd = caretModel.primaryCaret.selectionEnd
        record.doc = editor.document
        record.virtualFile = psiFile.virtualFile
        // todo record something
        OneTimeRecorder.push(record)
    }
}
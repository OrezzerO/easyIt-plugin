// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.github.orezzero.easyitplugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.vfs.VfsUtilCore

class EasyItInsertAction : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val pop = OneTimeRecorder.pop() ?: return
        val editor = anActionEvent.getData(CommonDataKeys.EDITOR)
        val psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE)
        val currentVirtualFile = psiFile!!.virtualFile
        val path = VfsUtilCore.findRelativePath(pop.virtualFile!!, currentVirtualFile, VfsUtilCore.VFS_SEPARATOR_CHAR)
        val lineNumber = editor!!.caretModel.logicalPosition.line + 1
        val doc = pop.doc
        WriteCommandAction.runWriteCommandAction(
            anActionEvent.project
        ) {
            doc?.replaceString(
                pop.selectedStart,
                pop.selectedEnd,
                "[" + pop.text + "](" + path + "#L" + lineNumber + ")"
            )
        }
    }
}
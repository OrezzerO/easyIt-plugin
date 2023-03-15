package com.github.orezzero.easyitplugin.action

import com.github.orezzero.easyitplugin.runUndoTransparentWriteAction
import com.github.orezzero.easyitplugin.util.MarkdownElementUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownInlineLink

class EasyItCancelLinkAction : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val editor = anActionEvent.getData(CommonDataKeys.EDITOR)
        val psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE)
        val project = anActionEvent.project
        if (editor == null
            || psiFile == null
            || project == null
        ) return

        val caretModel = editor.caretModel
        val offset = caretModel.offset
        val leaf = psiFile.findElementAt(offset)
        var link: MarkdownInlineLink? = null
        var parent = leaf?.parent
        while (parent != null) {
            if (parent is MarkdownInlineLink) {
                link = parent
                break
            }
            parent = parent.parent
        }
        if (link == null) {
            return
        }
        runUndoTransparentWriteAction {
            link.linkText?.let {
                link.replace(MarkdownElementUtils.createMarkdownText(project, MarkdownElementUtils.getLinkTextString(it)))
            }
        }
    }
}
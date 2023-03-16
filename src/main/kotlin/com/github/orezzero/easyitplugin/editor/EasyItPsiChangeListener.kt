package com.github.orezzero.easyitplugin.editor

import com.github.orezzero.easyitplugin.index.file.IndexManager
import com.github.orezzero.easyitplugin.md.MarkdownLanguageUtils.isMarkdownType
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.PsiDocumentTransactionListener
import com.intellij.testFramework.LightVirtualFile

class EasyItPsiChangeListener(private val project: Project) : PsiDocumentTransactionListener {

    val indexManager = IndexManager.getInstance(project)

    override fun transactionStarted(document: Document, file: PsiFile) {}

    override fun transactionCompleted(document: Document, file: PsiFile) {
        // TODO: this method is invoked twice
        val file = FileDocumentManager.getInstance().getFile(document) ?: return
        if (file is LightVirtualFile) return

        if (file.fileType.isMarkdownType()) {
            indexManager.index(file)
            return
        }
    }

}
package com.github.orezzero.easyitplugin.editor

import com.github.orezzero.easyitplugin.index.file.IndexManager
import com.github.orezzero.easyitplugin.index.file.entry.SimpleLocation
import com.github.orezzero.easyitplugin.md.MarkdownLanguageUtils.isMarkdownType
import com.github.orezzero.easyitplugin.runUndoTransparentWriteAction
import com.github.orezzero.easyitplugin.ui.gutter.EasyItGutterManager
import com.github.orezzero.easyitplugin.ui.gutter.EasyItGutterManagerImpl
import com.github.orezzero.easyitplugin.util.FileUtils
import com.github.orezzero.easyitplugin.util.LocationUtils
import com.github.orezzero.easyitplugin.util.MarkdownElementUtils
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.PsiDocumentTransactionListener
import com.intellij.testFramework.LightVirtualFile

class EasyItPsiChangeListener(private val project: Project) : PsiDocumentTransactionListener {

    val indexManager = IndexManager.getInstance(project)

    override fun transactionStarted(document: Document, file: PsiFile) {}

    override fun transactionCompleted(document: Document, file: PsiFile) {
        val file = FileDocumentManager.getInstance().getFile(document) ?: return
        if (file is LightVirtualFile) return

        if (file.fileType.isMarkdownType()) {
            indexManager.index(file)
            return
        }
    }

}
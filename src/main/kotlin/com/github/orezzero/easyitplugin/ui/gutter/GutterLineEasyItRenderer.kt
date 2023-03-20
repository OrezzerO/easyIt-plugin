package com.github.orezzero.easyitplugin.ui.gutter

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.AppUIUtil
import java.lang.ref.WeakReference
import javax.swing.Icon

data class GutterLineEasyItRenderer(
    val wrapper: EasyItGutterManagerImpl.Render,
    val project: Project,
    val file: VirtualFile
) :
    GutterIconRenderer() {


    private var reference: WeakReference<RangeHighlighter>? = null

    private val layer
        get() = HighlighterLayer.ERROR + 1

    private val document
        get() = file.let { FileDocumentManager.getInstance().getCachedDocument(it) }

    private val markup
        get() = document?.let { DocumentMarkupModel.forDocument(it, project, true) as? MarkupModelEx }

    internal val highlighter
        get() = reference?.get() ?: markup?.allHighlighters?.find { it.gutterIconRenderer == this }


    override fun getIcon(): Icon {
        return AllIcons.Gutter.Colors
    }

    fun refreshHighlighter() = AppUIUtil.invokeLaterIfProjectAlive(project) {
        highlighter?.also {
            it.gutterIconRenderer = null
            it.gutterIconRenderer = this
        } ?: createHighlighter()
    }

    fun removeHighlighter() = AppUIUtil.invokeLaterIfProjectAlive(project) {
        highlighter?.dispose()
        reference = null
    }

    private fun createHighlighter() {
        reference =
            markup?.addPersistentLineHighlighter(
                CodeInsightColors.BOOKMARKS_ATTRIBUTES,
                wrapper.location.line - 1,
                layer
            )
                ?.let {
                    it.gutterIconRenderer = this
                    it.errorStripeTooltip = tooltipText
                    WeakReference(it)
                }
    }


    override fun getPopupMenuActions(): ActionGroup {
        return GutterDynamicActionGroup(wrapper,project)
    }


}
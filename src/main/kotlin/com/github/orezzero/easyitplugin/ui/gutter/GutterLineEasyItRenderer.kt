package com.github.orezzero.easyitplugin.ui.gutter

import com.github.orezzero.easyitplugin.ui.project.view.Destination
import com.github.orezzero.easyitplugin.ui.project.view.EasyItNodeManagerImpl
import com.github.orezzero.easyitplugin.ui.project.view.Value
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.ui.AppUIUtil
import java.lang.ref.WeakReference
import javax.swing.Icon

data class GutterLineEasyItRenderer(val info: EasyItNodeManagerImpl.Info, var destination: Destination) : GutterIconRenderer() {


    private var reference: WeakReference<RangeHighlighter>? = null

    private val layer
        get() = HighlighterLayer.ERROR + 1

    private val document
        get() = destination.file.let { FileDocumentManager.getInstance().getCachedDocument(it) }

    private val markup
        get() = document?.let { DocumentMarkupModel.forDocument(it, destination.project, true) as? MarkupModelEx }

    internal val highlighter
        get() = reference?.get() ?: markup?.allHighlighters?.find { it.gutterIconRenderer == this }


    override fun getIcon(): Icon {
        return AllIcons.Gutter.Colors
    }

    fun refreshHighlighter() = AppUIUtil.invokeLaterIfProjectAlive(destination.project) {
        highlighter?.also {
            it.gutterIconRenderer = null
            it.gutterIconRenderer = this
        } ?: createHighlighter()
    }

    fun removeHighlighter() = AppUIUtil.invokeLaterIfProjectAlive(destination.project) {
        highlighter?.dispose()
        reference = null
    }

    private fun createHighlighter() {
        reference = markup?.addPersistentLineHighlighter(CodeInsightColors.BOOKMARKS_ATTRIBUTES, destination.line, layer)?.let {
            it.gutterIconRenderer = this
            it.errorStripeTooltip = tooltipText
            WeakReference(it)
        }
    }


    override fun getPopupMenuActions(): ActionGroup {
        return GutterDynamicActionGroup(info)
    }


}
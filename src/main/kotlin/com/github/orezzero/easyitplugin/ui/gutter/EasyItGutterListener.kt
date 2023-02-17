package com.github.orezzero.easyitplugin.ui.gutter

import com.github.orezzero.easyitplugin.editor.EasyItDocChangeListener
import com.github.orezzero.easyitplugin.index.file.IndexListenerDispatcher
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project

class EasyItGutterListener : com.intellij.openapi.startup.StartupActivity {
    override fun runActivity(project: Project) {
        EditorFactory.getInstance().eventMulticaster.addDocumentListener(EasyItDocChangeListener(project), project)
        EasyItManager.getInstance(project)!!
        IndexListenerDispatcher.getInstance(project)?.indexChanged()
    }
}
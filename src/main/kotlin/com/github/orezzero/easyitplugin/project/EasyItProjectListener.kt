package com.github.orezzero.easyitplugin.project

import com.github.orezzero.easyitplugin.editor.EasyItDocChangeListener
import com.github.orezzero.easyitplugin.index.file.IndexListenerDispatcher
import com.github.orezzero.easyitplugin.index.file.IndexManager
import com.github.orezzero.easyitplugin.index.file.IndexManager.Companion.ROOT_PATH
import com.github.orezzero.easyitplugin.ui.gutter.EasyItGutterManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project

class EasyItProjectListener : com.intellij.openapi.startup.StartupActivity {
    override fun runActivity(project: Project) {
        EditorFactory.getInstance().eventMulticaster.addDocumentListener(EasyItDocChangeListener(project), project)
        EasyItGutterManager.getInstance(project)!!
        IndexManager.getInstance(project)!!.index(ROOT_PATH)
    }
}
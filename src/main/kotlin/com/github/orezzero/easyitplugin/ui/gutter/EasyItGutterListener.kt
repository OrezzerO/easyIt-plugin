package com.github.orezzero.easyitplugin.ui.gutter

import com.github.orezzero.easyitplugin.index.file.IndexListenerDispatcher
import com.intellij.openapi.project.Project

class EasyItGutterListener : com.intellij.openapi.startup.StartupActivity {
    override fun runActivity(project: Project) {
        EasyItManager.getInstance(project)!!
        IndexListenerDispatcher.getInstance(project)?.indexChanged()
    }
}
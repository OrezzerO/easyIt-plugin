package com.github.orezzero.easyitplugin.ui.gutter

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.SimpleLocation
import com.intellij.openapi.project.Project

interface EasyItGutterManager {
    fun getLocation2Render(): Map<SimpleLocation, EasyItGutterManagerImpl.Render>
    fun getCodeLocation(linkLocation: IndexEntry): IndexEntry?

    companion object {
        fun getInstance(project: Project): EasyItGutterManager {
            return if (project.isDisposed) throw IllegalStateException("Project is disposed: $project") else project.getService(
                EasyItGutterManager::class.java
            )
        }
    }
}
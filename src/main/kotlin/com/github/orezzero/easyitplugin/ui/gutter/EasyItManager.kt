package com.github.orezzero.easyitplugin.ui.gutter

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.intellij.openapi.project.Project

interface EasyItManager {
    fun onIndexAdd(linkLocation: IndexEntry, codeLocation: IndexEntry)
    fun onIndexUpdate(md: IndexEntry, code: IndexEntry)

    companion object {
        fun getInstance(project: Project?): EasyItManager? {
            return if (project == null || project.isDisposed) null else project.getService(
                EasyItManager::class.java
            )
        }
    }
}
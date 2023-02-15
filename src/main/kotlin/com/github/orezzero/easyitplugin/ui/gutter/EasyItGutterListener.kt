package com.github.orezzero.easyitplugin.ui.gutter

import com.github.orezzero.easyitplugin.index.file.EasyItManager
import com.github.orezzero.easyitplugin.index.file.MarkdownLinkIndex
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex

class EasyItGutterListener : com.intellij.openapi.startup.StartupActivity {
    override fun runActivity(project: Project) {
        val instance = FileBasedIndex.getInstance()
        val allKeys = instance.getAllKeys(MarkdownLinkIndex.NAME, project)
        for (key in allKeys) {
            val values = instance.getValues(MarkdownLinkIndex.NAME, key, GlobalSearchScope.allScope(project))
            for (value in values) {
                EasyItManager.getInstance(project)?.onIndexAdd(key, value)
            }
        }

    }
}
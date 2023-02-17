package com.github.orezzero.easyitplugin.action

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.util.LocationUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.guessProjectDir

class EasyItNavigateAction(val entry: IndexEntry) : AnAction(entry.name) {
    override fun actionPerformed(e: AnActionEvent) {
        val toSimpleLocation = LocationUtils.toSimpleLocation(entry)
        e.project?.let {
            it.guessProjectDir()?.findFileByRelativePath(toSimpleLocation.path)?.let { f ->
                val openFileDescriptor = OpenFileDescriptor(it, f, toSimpleLocation.line, 0)
                openFileDescriptor.navigate(true)
            }
        }
    }
}
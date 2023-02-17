package com.github.orezzero.easyitplugin.index.file.entry

import com.github.orezzero.easyitplugin.util.FileUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

data class SimpleLocation(val path: String, val line: Int) {

    fun toString(project: Project, base: VirtualFile): CharSequence {


        val file = FileUtils.findFileByRelativePath(project, path)
        val relativePath = file?.let {
            FileUtils.getRelativePath(base, file)
        } ?: path

        return "${relativePath}#L$line"

    }
}
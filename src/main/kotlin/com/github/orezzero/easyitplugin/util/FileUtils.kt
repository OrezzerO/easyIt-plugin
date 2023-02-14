package com.github.orezzero.easyitplugin.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcsUtil.VcsFileUtil

object FileUtils {
    @JvmStatic
    fun getRelativePath(base: VirtualFile, dest: VirtualFile): String {
        return VcsFileUtil.relativePath(base, dest)
    }

    @JvmStatic
    fun getRelativePathBaseOnProject(project: Project, dest: VirtualFile): String {
        val projectDir = project.guessProjectDir()!!
        return getRelativePath(projectDir, dest)
    }

    fun findFileByRelativePath(base: VirtualFile, relativePath: String): VirtualFile? {
        return if (base.isDirectory) {
            base.findFileByRelativePath(relativePath)
        } else {
            base.parent?.findFileByRelativePath(relativePath)
        }

    }
}
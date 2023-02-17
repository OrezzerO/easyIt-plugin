package com.github.orezzero.easyitplugin.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcsUtil.VcsFileUtil
import org.jetbrains.annotations.NonNls

object FileUtils {
    @JvmStatic
    fun getRelativePath(base: VirtualFile, dest: VirtualFile): String {
        return if (base.isDirectory) {
            VcsFileUtil.relativePath(base, dest)
        } else {
            VcsFileUtil.relativePath(base.parent, dest)
        }
    }

    @JvmStatic
    fun getRelativePath(project: Project, dest: VirtualFile): String {
        val projectDir = project.guessProjectDir()!!
        return getRelativePath(projectDir, dest)
    }

    fun findFileByRelativePath(base: VirtualFile, relativePath: String): VirtualFile? {
        return if (base.isDirectory) {
            base.findFileByRelativePath(parse(relativePath))
        } else {
            base.parent?.findFileByRelativePath(parse(relativePath))
        }
    }

    private fun parse(relativePath: String): @NonNls String {
        if (relativePath.contains("#")) {
            return relativePath.substringBefore("#")
        }
        return relativePath
    }

    fun findFileByRelativePath(project: Project, relativePath: String): VirtualFile? {
        return findFileByRelativePath(project.guessProjectDir()!!, relativePath)
    }

}
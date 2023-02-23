package com.github.orezzero.easyitplugin.util

import com.github.orezzero.easyitplugin.runWriteAction
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

    fun createFile(project: Project, pathRelatedToProject: String): VirtualFile? {
        return runWriteAction {
            project.guessProjectDir()?.let {
                val dirs = pathRelatedToProject.split("/")
                var parent = it
                for (i in 0 until dirs.size - 1) {
                    var dir = parent.findChild(dirs[i])
                    if (dir == null) {
                        dir = parent.createChildDirectory(FileUtils, dirs[i])
                    }
                    parent = dir
                }

                // Create the file in the final directory
                parent.createChildData(FileUtils, dirs[dirs.size - 1])
            }
        }
    }
}


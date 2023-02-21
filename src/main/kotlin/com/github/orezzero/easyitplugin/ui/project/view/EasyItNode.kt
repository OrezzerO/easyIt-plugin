package com.github.orezzero.easyitplugin.ui.project.view

import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

abstract class EasyItNode<T> protected constructor(project: Project?, value: T) :
    ProjectViewNode<T>(project, value!!, ProjectViewSettings.Immutable.DEFAULT) {
    abstract fun order(): Int

    override fun contains(file: VirtualFile): Boolean {
        return false
    }

    override fun getSortKey(): Comparable<Int> {
        return order()
    }
}
package com.github.orezzero.easyitplugin.ui.project.view

import com.intellij.openapi.project.Project

interface EasyItNodeManager {
    fun onNodeAdded(node: EasyItNode<*>)
    fun onNodeRemoved(node: EasyItNode<*>)

    companion object {
        fun getInstance(project: Project?): EasyItNodeManager? {
            return if (project == null || project.isDisposed) null else project.getService(
                EasyItNodeManager::class.java
            )
        }
    }
}
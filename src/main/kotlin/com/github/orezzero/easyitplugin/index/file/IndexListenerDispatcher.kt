package com.github.orezzero.easyitplugin.index.file

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.EventDispatcher


class IndexListenerDispatcher(val project: Project) {
    companion object {
        fun getInstance(project: Project): IndexListenerDispatcher {
            return if (project.isDisposed) throw IllegalStateException("project is disposed:$project") else project.getService(
                IndexListenerDispatcher::class.java
            )
        }
    }

    private val dispatcher = EventDispatcher.create(LinkIndexListener::class.java)

    fun addListener(listener: LinkIndexListener) {
        dispatcher.addListener(listener)
    }


    fun indexChanged(virtualFile: VirtualFile) {
        dispatcher.multicaster.indexChanged(virtualFile)
    }

    fun removeListener(listener: LinkIndexListener) {
        dispatcher.removeListener(listener)
    }
}
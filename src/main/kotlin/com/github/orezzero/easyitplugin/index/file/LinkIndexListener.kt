package com.github.orezzero.easyitplugin.index.file

import com.intellij.openapi.vfs.VirtualFile
import java.util.*

interface LinkIndexListener : EventListener {
    fun indexChanged(virtualFile: VirtualFile) {}
}
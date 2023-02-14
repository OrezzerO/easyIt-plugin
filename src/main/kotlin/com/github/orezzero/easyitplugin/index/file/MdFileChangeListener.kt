package com.github.orezzero.easyitplugin.index.file

import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class MdFileChangeListener(private val runnable: Runnable) : BulkFileListener {
    override fun after(events: List<VFileEvent>) {
        var mdFileModified = false
        for (event in events) {
            event.file?.extension.equals("md")
            event.file?.let {
                if ("md" == it.extension) {
                    mdFileModified = true
                }
            }
        }
        if (mdFileModified) {
            runnable.run()
        }
    }
}
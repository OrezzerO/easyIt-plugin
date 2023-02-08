package com.github.orezzero.easyitplugin.action

import com.intellij.openapi.editor.Document
import com.intellij.openapi.vfs.VirtualFile
import java.util.concurrent.atomic.AtomicReference

object OneTimeRecorder {
    private val RECORD = AtomicReference<Record?>()
    fun push(record: Record?) {
        RECORD.set(record)
    }

    fun pop(): Record? {
        val record = RECORD.get()
        RECORD.set(null)
        return record
    }

    class Record {
        var offset = 0
        var text: String? = null
        var doc: Document? = null
        var selectedStart = 0
        var selectedEnd = 0
        var virtualFile: VirtualFile? = null
    }
}
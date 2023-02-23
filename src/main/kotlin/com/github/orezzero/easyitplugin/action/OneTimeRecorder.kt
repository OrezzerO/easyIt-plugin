package com.github.orezzero.easyitplugin.action

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

    class Record(val file: VirtualFile, val line: Int)
}
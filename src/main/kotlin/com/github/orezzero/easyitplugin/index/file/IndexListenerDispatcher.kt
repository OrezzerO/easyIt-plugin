package com.github.orezzero.easyitplugin.index.file

import com.intellij.openapi.project.Project
import com.intellij.util.EventDispatcher
import com.intellij.util.concurrency.AppExecutorUtil
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


class IndexListenerDispatcher(val project: Project) {
    companion object {
        fun getInstance(project: Project): IndexListenerDispatcher? {
            return if (project.isDisposed) null else project.getService(
                IndexListenerDispatcher::class.java
            )
        }
    }

    init {
        AppExecutorUtil.getAppScheduledExecutorService()
            .scheduleWithFixedDelay(this::triggerListener, 1, 1, TimeUnit.SECONDS);
    }

    private val dispatcher = EventDispatcher.create(LinkIndexListener::class.java)
    private val indexChanged = AtomicBoolean(false)


    fun addListener(listener: LinkIndexListener) {
        dispatcher.addListener(listener)
    }


    fun indexChanged() {
        indexChanged.set(true)
    }

    fun triggerListener() {
        if (indexChanged.get()) {
            synchronized(this) {
                if (indexChanged.compareAndSet(true, false)) {
                    dispatcher.multicaster.indexChanged()
                }
            }
        }
    }

    fun removeListener(listener: LinkIndexListener) {
        dispatcher.removeListener(listener)
    }
}
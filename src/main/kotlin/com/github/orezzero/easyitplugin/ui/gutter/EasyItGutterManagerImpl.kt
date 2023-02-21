package com.github.orezzero.easyitplugin.ui.gutter

import com.github.orezzero.easyitplugin.index.file.IndexListenerDispatcher
import com.github.orezzero.easyitplugin.index.file.IndexManager
import com.github.orezzero.easyitplugin.index.file.LinkIndexListener
import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.SimpleLocation
import com.github.orezzero.easyitplugin.util.FileUtils
import com.github.orezzero.easyitplugin.util.LocationUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.util.concurrent.ConcurrentHashMap


class EasyItGutterManagerImpl(val project: Project) : EasyItGutterManager {
    init {
        val manager = this
        IndexListenerDispatcher.getInstance(project).addListener(object : LinkIndexListener {
            override fun indexChanged(virtualFile: VirtualFile) {
                manager.refresh()
            }
        })
    }

    val indexManager = IndexManager.getInstance(project)

    private val location2Renderer: MutableMap<SimpleLocation, Render> = ConcurrentHashMap()
    private val link2codeCache: MutableMap<IndexEntry, IndexEntry> = ConcurrentHashMap()
    fun refresh() {
        indexManager.withWriteLock {
            // clear old index
            for (value in location2Renderer.values) {
                value.quickRemove()
            }
            location2Renderer.clear()
            link2codeCache.clear()

            // put new index
            val allDate = indexManager.getAllDate()
            for (maps in allDate.values) {
                maps.forEach { (link, code) -> onIndexAdd(link, code) }
            }
        }
    }

    override fun getLocation2Render(): Map<SimpleLocation, Render> {
        return indexManager.withReadLock {
            location2Renderer
        }
    }

    override fun getCodeLocation(linkLocation: IndexEntry): IndexEntry? {
        return indexManager.withReadLock {
            link2codeCache[linkLocation]
        }
    }

    private fun onIndexAdd(linkLocation: IndexEntry, codeLocation: IndexEntry) {
        link2codeCache[linkLocation] = codeLocation
        val simpleCodeLocation = LocationUtils.toSimpleLocation(codeLocation)
        val file = FileUtils.findFileByRelativePath(project, simpleCodeLocation.path)
        file?.let {
            val render = location2Renderer.computeIfAbsent(simpleCodeLocation) { l -> Render(l, project, file) }
            render.addLinkLocation(linkLocation)
            render.refreshRender()
        }
    }

    inner class Render constructor(val location: SimpleLocation, project: Project, file: VirtualFile) {
        val linkLocations = mutableSetOf<IndexEntry>()
        val myRenderer: GutterLineEasyItRenderer

        init {
            myRenderer = GutterLineEasyItRenderer(this, project, file)
        }

        fun refreshRender() {
            if (linkLocations.isNotEmpty()) {
                myRenderer.refreshHighlighter()
            }
        }

        fun removeRender(entry: IndexEntry): Boolean {
            linkLocations.remove(entry)
            if (linkLocations.isEmpty()) {
                myRenderer.removeHighlighter()
                return true
            }
            return false
        }

        fun quickRemove(): Boolean {
            linkLocations.clear()
            myRenderer.removeHighlighter()
            return true
        }


        fun addLinkLocation(linkLocation: IndexEntry) {
            linkLocations.add(linkLocation)
        }


    }

}
package com.github.orezzero.easyitplugin.ui.gutter

import com.github.orezzero.easyitplugin.index.file.IndexListenerDispatcher
import com.github.orezzero.easyitplugin.index.file.LinkIndexListener
import com.github.orezzero.easyitplugin.index.file.MarkdownLinkIndex
import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.SimpleLocation
import com.github.orezzero.easyitplugin.runReadAction
import com.github.orezzero.easyitplugin.util.FileUtils
import com.github.orezzero.easyitplugin.util.LocationUtils

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import java.util.concurrent.ConcurrentHashMap


class EasyItManagerImpl(val project: Project) : EasyItManager {
    init {
        val manager = this
        IndexListenerDispatcher.getInstance(project)?.let {
            it.addListener(object : LinkIndexListener {
                init {
                    val me: LinkIndexListener = this
                    Disposer.register(
                        project
                    ) {
                        it.removeListener(me)
                    }
                }

                override fun indexChanged() {
                    manager.refresh()
                }
            })
        }
    }

    private val location2Renderer: MutableMap<SimpleLocation, Render> = ConcurrentHashMap()
    fun refresh() {
        // clear old index
        for (value in location2Renderer.values) {
            value.quickRemove()
        }
        location2Renderer.clear()

        // put new index
        val instance = FileBasedIndex.getInstance()
        val allKeys = runReadAction { instance.getAllKeys(MarkdownLinkIndex.NAME, project) }
        for (key in allKeys) {
            val values =
                runReadAction { instance.getValues(MarkdownLinkIndex.NAME, key, GlobalSearchScope.allScope(project)) }
            for (value in values) {
                EasyItManager.getInstance(project)?.onIndexAdd(key, value)
            }
        }
    }

    override fun getLocation2Render(): Map<SimpleLocation, Render> {
        return location2Renderer
    }

    override fun onIndexAdd(linkLocation: IndexEntry, codeLocation: IndexEntry) {
        val simpleCodeLocation = LocationUtils.toSimpleLocation(codeLocation)

        val file = FileUtils.findFileByRelativePath(project, simpleCodeLocation.path)
        file?.let {
            val render = location2Renderer.computeIfAbsent(simpleCodeLocation) { l -> Render(l, project, file) }
            render.addLinkLocation(linkLocation)
            render.refreshRender()
        }
    }

    override fun onIndexUpdate(md: IndexEntry, code: IndexEntry) {
        TODO("Not yet implemented")
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
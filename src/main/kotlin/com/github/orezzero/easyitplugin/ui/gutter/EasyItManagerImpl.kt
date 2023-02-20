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
    private val link2codeCache: MutableMap<IndexEntry, IndexEntry> = ConcurrentHashMap()
    fun refresh() {
        // clear old index
        for (value in location2Renderer.values) {
            value.quickRemove()
        }
        location2Renderer.clear()
        link2codeCache.clear()

        // put new index
        val instance = FileBasedIndex.getInstance()
        val allLinkLocations = runReadAction { instance.getAllKeys(MarkdownLinkIndex.NAME, project) }
        for (linkLocation in allLinkLocations) {
            val codeLocations =
                runReadAction {
                    instance.getValues(
                        MarkdownLinkIndex.NAME,
                        linkLocation,
                        GlobalSearchScope.allScope(project)
                    )
                }
            when (codeLocations.size) {
                1 -> EasyItManager.getInstance(project)?.onIndexAdd(linkLocation, codeLocations[0])
                else -> throw IllegalStateException("Code location size error:${codeLocations.size}")
            }
        }
    }

    override fun getLocation2Render(): Map<SimpleLocation, Render> {
        return location2Renderer
    }

    override fun getCodeLocation(linkLocation: IndexEntry): IndexEntry? {
        return link2codeCache[linkLocation]
    }

    override fun refreshCache(oldKey: IndexEntry, newKey: IndexEntry) {
        link2codeCache.remove(oldKey)?.let {
            link2codeCache[newKey] = it
        }

    }

    override fun onIndexAdd(linkLocation: IndexEntry, codeLocation: IndexEntry) {
        link2codeCache[linkLocation] = codeLocation
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
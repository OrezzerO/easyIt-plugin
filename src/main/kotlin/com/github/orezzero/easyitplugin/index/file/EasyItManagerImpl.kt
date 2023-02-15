package com.github.orezzero.easyitplugin.index.file

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.SimpleLocation
import com.github.orezzero.easyitplugin.ui.gutter.GutterLineEasyItRenderer
import com.github.orezzero.easyitplugin.util.LocationUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.util.concurrent.ConcurrentHashMap


class EasyItManagerImpl(val project: Project) : EasyItManager {

    private val location2Renderer: MutableMap<SimpleLocation, Render> = ConcurrentHashMap()

    override fun onIndexAdd(linkLocation: IndexEntry, codeLocation: IndexEntry) {
        val simpleCodeLocation = LocationUtils.toSimpleLocation(codeLocation)

        val file = LocationUtils.getFile(project, simpleCodeLocation.path)
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


        fun addLinkLocation(linkLocation: IndexEntry) {
            linkLocations.add(linkLocation)
        }


    }

}
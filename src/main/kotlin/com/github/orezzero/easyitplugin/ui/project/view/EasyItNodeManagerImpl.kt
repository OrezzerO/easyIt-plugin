package com.github.orezzero.easyitplugin.ui.project.view

import com.github.orezzero.easyitplugin.editor.EasyItDocChangeListener
import com.github.orezzero.easyitplugin.ui.gutter.GutterLineEasyItRenderer
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import java.util.*


class EasyItNodeManagerImpl(val project: Project) : EasyItNodeManager {

    init {
        val multicaster = EditorFactory.getInstance().eventMulticaster
//        multicaster.addDocumentListener(EasyItDocChangeListener(project), project)
    }

    private val value2info: MutableMap<Destination, Info> = mutableMapOf()
    override fun onNodeAdded(node: EasyItNode<*>) {
        when (node) {
            is EasyItLinkNode -> {
                onEasyItLinkNodeAdded(node)
                value2info[node.value?.destination]?.refreshRender()
            }
        }
    }

    override fun onNodeRemoved(node: EasyItNode<*>) {
        value2info[node.value]?.let {
            if (it.removeRender(node)) {
                value2info.remove(node.value)
            }
        }
    }

    override fun getValue2Info(): MutableMap<Destination, Info> {
        return Collections.unmodifiableMap(value2info)

    }


    private fun onEasyItLinkNodeAdded(treeNode: EasyItLinkNode) {
        treeNode.value?.destination?.let {
            val inMapInfo = value2info.computeIfAbsent(it) { i -> Info(i) }
            inMapInfo.addNode(treeNode)
        }
    }

    inner class Info constructor(destination: Destination) {
        val myRenderer: GutterLineEasyItRenderer
        val nodes = mutableSetOf<EasyItLinkNode>()

        init {
            myRenderer = GutterLineEasyItRenderer(this, destination)
        }

        fun refreshRender() {
            if (nodes.isNotEmpty()) {
                myRenderer.refreshHighlighter()
            }
        }

        fun removeRender(easyItNode: EasyItNode<*>): Boolean {
            nodes.remove(easyItNode)
            if (nodes.isEmpty()) {
                myRenderer.removeHighlighter()
                return true
            }
            return false
        }

        fun addNode(node: EasyItLinkNode) {
            nodes.add(node)
        }
    }
}
package com.github.orezzero.easyitplugin.ui.project.view

import com.github.orezzero.easyitplugin.ui.gutter.GutterLineEasyItRenderer
import com.intellij.openapi.project.Project


class EasyItNodeManagerImpl(val project: Project) : EasyItNodeManager {

    private val value2info: MutableMap<Value, Info> = mutableMapOf()
    override fun onNodeAdded(node: EasyItNode<*>) {
        when (node) {
            is EasyItLinkNode -> {
                onEasyItLinkNodeAdded(node)
                value2info[node.value]?.refreshRender()
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


    private fun onEasyItLinkNodeAdded(treeNode: EasyItLinkNode) {
        treeNode.value?.let {
            val inMapInfo = value2info.computeIfAbsent(it) { i -> Info(i) }
            inMapInfo.addNode(treeNode)
        }
    }

    inner class Info constructor(value: Value) {
        private val myRenderer: GutterLineEasyItRenderer
        val nodes = mutableSetOf<EasyItLinkNode>()

        init {
            myRenderer = GutterLineEasyItRenderer(this, value)
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
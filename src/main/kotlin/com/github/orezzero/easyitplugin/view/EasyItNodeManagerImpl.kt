package com.github.orezzero.easyitplugin.view

import com.intellij.openapi.project.Project


class EasyItNodeManagerImpl(val project: Project) : EasyItNodeManager {

    private val nodeMap: MutableMap<EasyItLinkNode, Info> = mutableMapOf()

    //private val infoMap: MutableMap<Info, MutableList<EasyItLinkNode>> = mutableMapOf()
    private val value2info: MutableMap<Node, Info> = mutableMapOf()


    override fun onNodeAdded(node: EasyItNode<*>?) {
        when (node) {
            is EasyItLinkNode -> onEasyItLinkNodeAdded(node)
        }
        nodeMap[node]?.refreshRender()
    }

    override fun onNodeRemoved(node: EasyItNode<*>?) {
        node?.let {
            nodeMap[node]?.removeRender(it)
            nodeMap.remove(node)
        }
    }


    private fun onEasyItLinkNodeAdded(treeNode: EasyItLinkNode) {
        treeNode.value?.let {
            val inMapInfo = value2info.computeIfAbsent(it) { i -> Info(i) }
            nodeMap[treeNode] = inMapInfo
            inMapInfo.addNode(treeNode)
        }
    }

    inner class Info constructor(node: Node) {
        private val myRenderer: GutterLineEasyItRenderer
        val nodes = mutableSetOf<EasyItLinkNode>()

        init {
            myRenderer = GutterLineEasyItRenderer(this, node)
        }

        fun refreshRender() {
            if (nodes.isNotEmpty()) {
                myRenderer.refreshHighlighter()
            }
        }

        fun removeRender(easyItNode: EasyItNode<*>) {
            if (nodes.isEmpty()) {
                myRenderer.removeHighlighter()
            } else {
                nodes.remove(easyItNode)
            }
        }

        fun addNode(node: EasyItLinkNode) {
            nodes.add(node)
        }
    }
}
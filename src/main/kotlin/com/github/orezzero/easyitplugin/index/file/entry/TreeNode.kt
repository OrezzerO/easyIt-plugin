package com.github.orezzero.easyitplugin.index.file.entry

class TreeNode<T>(val value: T, val parent: TreeNode<*>?) {
    var rightChild: TreeNode<*>? = null;
    private val children: MutableList<TreeNode<*>> = mutableListOf()

    fun addChild(node: TreeNode<*>) {
        children.add(node)
        rightChild = node
    }

    fun getChildren(): List<TreeNode<*>> {
        return children.toList()
    }


}
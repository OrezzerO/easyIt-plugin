package com.github.orezzero.easyitplugin.index.file


import com.github.orezzero.easyitplugin.index.file.entry.LayerId
import com.intellij.util.containers.Stack
import org.intellij.plugins.markdown.lang.MarkdownElementTypes.*
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownHeader
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownList
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownListItem
import java.util.*


class MarkdownLayer {
    val headerLayer = mutableListOf<Int>()
    val listLayer = Stack<Int>()
    var linkLayer = 0

    fun acceptHeader(header: MarkdownHeader) {
        // todo add lock
        this.linkLayer = 0
        val headerLayer = getHeaderLayer(header)
        while (this.headerLayer.size - 1 < headerLayer) this.headerLayer.add(0)
        this.headerLayer[headerLayer]++
        for (i in headerLayer + 1 until this.headerLayer.size) {
            this.headerLayer[i] = 0
        }
    }


    private fun printMajor(): String {
        return printList(headerLayer)
    }

    private fun printMinorLayer(): String {
        return printList(listLayer)
    }

    private fun printList(list: List<Int>): String {
        val stringJoiner = StringJoiner(".")
        for (i in list) {
            stringJoiner.add(i.toString())
        }
        val result = stringJoiner.toString()
        return result
    }

    override fun toString(): String {
        return printMajor() + ":" + printMinorLayer()
    }

    fun getHeaderLayer(header: MarkdownHeader): Int {
        return when (header.elementType) {
            ATX_1 -> 1
            ATX_2 -> 2
            ATX_3 -> 3
            ATX_4 -> 4
            ATX_5 -> 5
            ATX_6 -> 6
            else -> 7
        }
    }

    fun listStart(list: MarkdownList) {
        listLayer.push(0)
    }

    fun listEnd(list: MarkdownList) {
        listLayer.pop()
    }

    fun acceptListItem(element: MarkdownListItem) {
        var currentIndex = listLayer.pop()
        listLayer.push(currentIndex + 1)
    }

    fun getCurrentLayerId(): LayerId {
        return LayerId(headerLayer.toList(), listLayer.toList(), linkLayer)
    }

    fun acceptLink() {
        linkLayer++
    }
}
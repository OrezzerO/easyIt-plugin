package com.github.orezzero.easyitplugin.index.file.entry

import java.util.*
import kotlin.math.min

class LayerId(val headerId: List<Int>, val listId: List<Int>, val linkId: Int) : Comparable<LayerId> {

    companion object {
        val META = LayerId(listOf(), listOf(), 0)
    }


    override fun toString(): String {
        val stringJoiner = StringJoiner(":")
        stringJoiner.add(printMajor())
        stringJoiner.add(printMinorLayer())
        stringJoiner.add(linkId.toString())
        return stringJoiner.toString()
    }

    private fun printMajor(): String {
        return printList(headerId)
    }

    private fun printMinorLayer(): String {
        return printList(listId)
    }

    private fun printList(list: List<Int>): String {
        val stringJoiner = StringJoiner(".")
        for (i in list) {
            stringJoiner.add(i.toString())
        }
        val result = stringJoiner.toString()
        return result
    }

    override fun compareTo(other: LayerId): Int {
        var result = compareList(headerId, other.headerId)
        if (result != 0) return result;

        // listItem is not show in project view
        // and sub nodes of listItem is flattened.
        // So we do take headerId into consider.
        // todo: BTW , the way to calculate linkId is not correct
//        result = compareList(listId, other.headerId)
//        if (result != 0) return result;

        return linkId.compareTo(other.linkId)
    }

    private fun compareList(me: List<Int>, other: List<Int>): Int {
        val size = me.size
        val otherSize = other.size
        val minSize = min(size, otherSize)

        for (i in 0 until minSize) {
            val compare = me[i].compareTo(other[i])
            if (compare != 0) return compare
        }

        return size.compareTo(otherSize)
    }
}
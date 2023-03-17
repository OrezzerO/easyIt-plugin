package com.github.orezzero.easyitplugin.index.file.entry

import kotlin.math.min

class LayerId(val headerId: List<Int>, val listId: List<Int>, val linkId: Int) : Comparable<LayerId> {

    companion object {
        val META = LayerId(listOf(), listOf(), 0)
    }

    override fun compareTo(other: LayerId): Int {
        var result = compareList(headerId, other.headerId)
        if (result != 0) return result;

        result = compareList(listId, other.headerId)
        if (result != 0) return result;

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
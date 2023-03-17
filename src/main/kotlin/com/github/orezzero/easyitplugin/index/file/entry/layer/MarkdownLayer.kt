package com.github.orezzero.easyitplugin.index.file.entry.layer

import com.github.orezzero.easyitplugin.index.file.entry.LayerId

abstract class MarkdownLayer(
    val text: String,
    val id: LayerId,
) {
    override fun toString(): String {
        return text
    }
}
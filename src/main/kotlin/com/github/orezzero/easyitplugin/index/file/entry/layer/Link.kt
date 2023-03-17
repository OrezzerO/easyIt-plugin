package com.github.orezzero.easyitplugin.index.file.entry.layer

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.LayerId

class Link(
    val link: IndexEntry,
    val code: IndexEntry,
    id: LayerId,
) : MarkdownLayer(link.name, id)

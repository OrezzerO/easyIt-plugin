package com.github.orezzero.easyitplugin.index.file

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.LayerId
import com.github.orezzero.easyitplugin.index.file.entry.layer.Header
import com.github.orezzero.easyitplugin.index.file.entry.layer.Link
import com.github.orezzero.easyitplugin.index.file.entry.layer.ListItem
import com.github.orezzero.easyitplugin.util.MarkdownElementUtils
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownHeader
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownListItem


class MarkdownTreeNodeFactory {
    companion object {
        fun buildHeader(header: MarkdownHeader, level: Int, id: LayerId): Header {
            val headerText = MarkdownElementUtils.getHeaderText(header)
            return Header(headerText, level, id, header)
        }

        fun buildListItem(listItem: MarkdownListItem, level: Int, id: LayerId): ListItem {
            val text = "ListItem ${id.listId[id.listId.size - 1]}"
            return ListItem(text, level, id)
        }

        fun buildLink(linkLocation: IndexEntry, codeLocation: IndexEntry, id: LayerId): Link {
            return Link(linkLocation, codeLocation, id)
        }
    }
}
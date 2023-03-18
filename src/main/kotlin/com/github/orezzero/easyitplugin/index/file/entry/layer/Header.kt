package com.github.orezzero.easyitplugin.index.file.entry.layer

import com.github.orezzero.easyitplugin.index.file.entry.LayerId
import com.intellij.pom.Navigatable

class Header(text: String, val level: Int, id: LayerId, val navigatable: Navigatable) : MarkdownLayer(text, id)
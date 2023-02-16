package com.github.orezzero.easyitplugin.index.file

import java.util.*

interface LinkIndexListener : EventListener {
    fun indexChanged() {}
}
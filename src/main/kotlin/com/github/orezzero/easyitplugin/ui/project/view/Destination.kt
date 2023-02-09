package com.github.orezzero.easyitplugin.ui.project.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

data class Destination(val project: Project, val file: VirtualFile, val line: Int)
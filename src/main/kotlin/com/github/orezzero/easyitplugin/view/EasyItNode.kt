package com.github.orezzero.easyitplugin.view

import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project

abstract class EasyItNode<T> protected constructor(project: Project?, value: T) : AbstractTreeNode<T>(project, value!!)
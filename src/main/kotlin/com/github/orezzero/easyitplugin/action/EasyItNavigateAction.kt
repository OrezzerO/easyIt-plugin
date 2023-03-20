package com.github.orezzero.easyitplugin.action

import com.github.orezzero.easyitplugin.index.file.IndexManager
import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.layer.ListItem
import com.github.orezzero.easyitplugin.util.LocationUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.Stack

import java.util.*

class EasyItNavigateAction(project: Project, val entry: IndexEntry) : AnAction(getName(project, entry)) {
    override fun actionPerformed(e: AnActionEvent) {
        val toSimpleLocation = LocationUtils.toSimpleLocation(entry)
        e.project?.let {
            it.guessProjectDir()?.findFileByRelativePath(toSimpleLocation.path)?.let { f ->
                val openFileDescriptor = OpenFileDescriptor(it, f, toSimpleLocation.line - 1, 0)
                openFileDescriptor.navigate(true)
            }
        }
    }
}

fun getName(project: Project, entry: IndexEntry): String {
    val treeNode = IndexManager.getInstance(project).getCacheData(entry)
    val stack = Stack<String>()

    treeNode?.value?.let {
        stack.push(it.toString())
    }

    var parent = treeNode?.parent
    while (parent != null) {
        if (parent.value is VirtualFile) {
            // if TreeNode<VirtualFile> we use filename instead of full path
            stack.push((parent.value as VirtualFile).nameWithoutExtension)
        } else if (parent.value is ListItem) {
            // skip list item
        } else {
            stack.push(parent.value.toString())
        }
        parent = parent.parent
    }

    val stringJoiner = StringJoiner(" > ")
    while (stack.isNotEmpty()) {
        stringJoiner.add(stack.pop())
    }
    return stringJoiner.toString()


}

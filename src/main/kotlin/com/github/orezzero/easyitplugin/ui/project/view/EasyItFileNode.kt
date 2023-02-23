package com.github.orezzero.easyitplugin.ui.project.view


import com.github.orezzero.easyitplugin.index.file.IndexListenerDispatcher
import com.github.orezzero.easyitplugin.index.file.IndexManager
import com.github.orezzero.easyitplugin.index.file.LinkIndexListener
import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.md.MarkdownLanguageUtils.isMarkdownType
import com.github.orezzero.easyitplugin.util.FileUtils
import com.github.orezzero.easyitplugin.util.LocationUtils
import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.util.Alarm
import javax.swing.SwingUtilities

class EasyItFileNode : EasyItNode<VirtualFile> {

    var oldChildren: Collection<AbstractTreeNode<*>?> = emptyList()

    val indexManager = IndexManager.getInstance(project)

    var codeLocation: IndexEntry? = null

    var order = Integer.MAX_VALUE

    constructor(project: Project?, value: VirtualFile, codeLocation: IndexEntry) : super(project, value) {
        this.codeLocation = codeLocation
    }

    constructor(project: Project?, value: VirtualFile, root: Boolean) : super(project, value) {
        if (root) {
            subscribeToVFS(project!!)
        }
    }

    override fun getVirtualFile(): VirtualFile {
        return value!!
    }

    override fun getChildren(): Collection<AbstractTreeNode<*>?> {
        val fileNodes: MutableList<EasyItNode<*>> = mutableListOf()
        val linkNodes: MutableList<EasyItNode<*>> = mutableListOf()

        indexManager.getFileData(virtualFile)?.forEach { (linkLoc, codeLoc) ->
            val codeFile = findVirtualFile(codeLoc.location)
            if (codeFile != null && codeFile != virtualFile && codeFile.fileType.isMarkdownType()) {
                fileNodes.add(EasyItFileNode(myProject, codeFile, codeLoc))
            } else {
                val linkNode = EasyItLinkNode(myProject, linkLoc, codeLoc)
                linkNodes.add(linkNode)
            }
        }
        // sort
        fileNodes.sortedBy { node -> node.order() }
        linkNodes.sortedBy { node -> node.order() }
        fileNodes.addAll(linkNodes)

        oldChildren = fileNodes
        return fileNodes
    }

    override fun order(): Int {
        if (order == Int.MAX_VALUE) {
            codeLocation?.let { order = LocationUtils.getOrder(it) }
        }
        println(order)
        return order
    }

    private fun findVirtualFile(text: String): VirtualFile? {
        return FileUtils.findFileByRelativePath(project, text.substringBefore("#"))
    }

    override fun update(presentation: PresentationData) {
        presentation.setIcon(AllIcons.Nodes.Folder)
        presentation.presentableText = codeLocation?.name ?: value.name
    }

    override fun canNavigate(): Boolean {
        return true
    }

    override fun canNavigateToSource(): Boolean {
        return canNavigate()
    }

    override fun navigate(requestFocus: Boolean) {
        PsiManager.getInstance(myProject).findFile(value!!)?.navigate(requestFocus)
    }


    private fun subscribeToVFS(project: Project?) {
        if (project == null) {
            return
        }

        // TODO: (done) 1. 监听自身的Index 变化
        // TODO: 2. 自身那个 Index 需要监听下文件的变化, 如果文件被删除或者移动, 需要修改Index
        // todo: 3. 需要增强一下 MD 文件 rename 那个 processor , 同步修改 link 信息

        val alarm = Alarm(Alarm.ThreadToUse.POOLED_THREAD, project)
        IndexListenerDispatcher.getInstance(project).let {
            it.addListener(object : LinkIndexListener {
                override fun indexChanged(virtualFile: VirtualFile) {
                    // md file changes
                    alarm.cancelAllRequests()
                    alarm.addRequest({
                        SwingUtilities.invokeLater {
                            ProjectView.getInstance(myProject).getProjectViewPaneById(EasyItProjectView.ID)
                                .updateFromRoot(true) // todo 这里可以选择不 从 root 更新, 而是从某个节点更新, 可优化
                        }
                    }, 1000)
                }
            })

        }
    }
}
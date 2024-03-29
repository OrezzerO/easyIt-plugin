package com.github.orezzero.easyitplugin.ui.project.view


import com.github.orezzero.easyitplugin.index.file.IndexListenerDispatcher
import com.github.orezzero.easyitplugin.index.file.IndexManager
import com.github.orezzero.easyitplugin.index.file.LinkIndexListener
import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.index.file.entry.LayerId
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.util.Alarm
import org.intellij.plugins.markdown.MarkdownIcons
import javax.swing.SwingUtilities

class EasyItFileNode : EasyItNode<VirtualFile> {

    val indexManager = IndexManager.getInstance(project)

    var codeLocation: IndexEntry? = null


    constructor(project: Project?, value: VirtualFile, codeLocation: IndexEntry, layerId: LayerId) : super(
        project,
        value,
        layerId
    ) {
        this.codeLocation = codeLocation
    }

    constructor(project: Project?, value: VirtualFile, root: Boolean, layerId: LayerId) : super(
        project,
        value,
        layerId
    ) {
        if (root) {
            subscribeToVFS(project!!)
        }
    }

    override fun getVirtualFile(): VirtualFile {
        return value!!
    }

    override fun getChildren(): Collection<AbstractTreeNode<*>?> {
        val nodes: MutableList<EasyItNode<*>> = mutableListOf()
        indexManager.getFileRoot(virtualFile)?.getChildren()?.forEach { generateTreeNode(it, nodes) }
        return nodes
    }

    override fun update(presentation: PresentationData) {
        presentation.setIcon(MarkdownIcons.MarkdownPlugin)
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
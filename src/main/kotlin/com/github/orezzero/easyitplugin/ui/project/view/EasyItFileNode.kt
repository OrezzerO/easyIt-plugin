package com.github.orezzero.easyitplugin.ui.project.view


import com.github.orezzero.easyitplugin.index.file.MarkdownLinkIndex
import com.github.orezzero.easyitplugin.stub.InlineLinkTextIndex
import com.github.orezzero.easyitplugin.stub.LinkIndexListener
import com.github.orezzero.easyitplugin.util.FileUtils
import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Alarm
import com.intellij.util.indexing.FileBasedIndex
import java.util.*
import javax.swing.SwingUtilities
import kotlin.streams.toList

class EasyItFileNode : EasyItNode<VirtualFile> {

    var oldChildren: Collection<AbstractTreeNode<*>?> = emptyList()

    constructor(project: Project?, value: VirtualFile) : super(project, value)
    constructor(project: Project?, value: VirtualFile, root: Boolean) : super(project, value) {
        if (root) {
            subscribeToVFS(project!!)
        }
    }

    override fun getVirtualFile(): VirtualFile {
        return value!!
    }

    override fun getChildren(): Collection<AbstractTreeNode<*>?> {
//        val list: List<MarkdownInlineLink> = ArrayList()
        val children: MutableList<AbstractTreeNode<*>?> = mutableListOf()

        var values = FileBasedIndex.getInstance().getValues(
            MarkdownLinkIndex.NAME, FileUtils.getRelativePathBaseOnProject(project, value!!),
            GlobalSearchScope.fileScope(project, virtualFile)
        )
        var flatValues = values.stream().flatMap { value -> value.stream() }.toList()



        for (value in flatValues) {
            val file = findVirtualFile(value.dest)
            if (isDestMdFile(file)) {
                children.add(EasyItFileNode(myProject, file!!))
            } else if (file != null) {
                Dest.of(project, virtualFile, value)?.let {
                    val linkNode = EasyItLinkNode(myProject, it)
                    children.add(linkNode)
                    EasyItNodeManager.getInstance(myProject)?.onNodeAdded(linkNode)
                }
            }
        }
        for (ele in oldChildren) {
            val easyItNode = ele as? EasyItLinkNode
            easyItNode?.let { EasyItNodeManager.getInstance(myProject)?.onNodeRemoved(it) }
        }
        oldChildren = children
        return children
    }

    private fun findVirtualFile(text: String): VirtualFile? {
        return FileUtils.findFileByRelativePath(virtualFile, text.substringBefore("#"))
    }

    private fun isDestMdFile(file: VirtualFile?): Boolean {
        if (file == null) {
            return false
        }
        return file.extension == "md" || file.extension == "markdown"
    }

    override fun update(presentation: PresentationData) {
        presentation.setIcon(AllIcons.Nodes.Folder)
        var text = "DEFAULT"
        text = Optional.ofNullable(
            value
        ).map { obj: VirtualFile -> obj.name }.orElse(text)
        presentation.presentableText = text
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

        // todo 还需要鉴定 MD 文件的变化
        val alarm = Alarm(Alarm.ThreadToUse.POOLED_THREAD, project)
        LocalFileSystem.getInstance().addVirtualFileListener(object : VirtualFileListener {
            init {
                val me: VirtualFileListener = this
                Disposer.register(
                    project
                ) {
                    LocalFileSystem.getInstance().removeVirtualFileListener(
                        me
                    )
                }
            }

            override fun fileCreated(event: VirtualFileEvent) {
                handle(event)
            }

            override fun fileDeleted(event: VirtualFileEvent) {
                handle(event)
            }

            fun handle(event: VirtualFileEvent) {
                val filename = event.fileName.lowercase(Locale.getDefault())
                if (filename.endsWith(".md")) {
                    // md file changes
                    alarm.cancelAllRequests()
                    alarm.addRequest({
                        SwingUtilities.invokeLater {
                            ProjectView.getInstance(myProject)
                                .getProjectViewPaneById(EasyItProjectView.ID)
                                .updateFromRoot(true) // todo 这里可以选择不 从 root 更新, 而是从某个节点更新, 可优化
                        }
                    }, 1000)
                }
            }
        })

        InlineLinkTextIndex.addLinkFileListener(object : LinkIndexListener {
            init {
                val me: LinkIndexListener = this
                Disposer.register(
                    project
                ) {
                    InlineLinkTextIndex.removeLinkIndexListener(me)
                }
            }

            override fun indexChanged(`object`: Any?) {
                // md file changes
                alarm.cancelAllRequests()
                alarm.addRequest({
                    SwingUtilities.invokeLater {
                        ProjectView.getInstance(myProject)
                            .getProjectViewPaneById(EasyItProjectView.ID)
                            .updateFromRoot(true) // todo 这里可以选择不 从 root 更新, 而是从某个节点更新, 可优化
                    }
                }, 1000)

            }
        })
    }
}
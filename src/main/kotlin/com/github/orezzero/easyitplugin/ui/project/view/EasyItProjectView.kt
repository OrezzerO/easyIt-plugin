package com.github.orezzero.easyitplugin.ui.project.view

import com.github.orezzero.easyitplugin.index.file.IndexManager
import com.github.orezzero.easyitplugin.util.FileUtils
import com.intellij.icons.AllIcons
import com.intellij.ide.SelectInTarget
import com.intellij.ide.impl.ProjectViewSelectInTarget
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.AbstractProjectViewPaneWithAsyncSupport
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase
import com.intellij.ide.projectView.impl.ProjectTreeStructure
import com.intellij.ide.projectView.impl.ProjectViewTree
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls
import javax.swing.Icon
import javax.swing.tree.DefaultTreeModel

class EasyItProjectView(project: Project) : AbstractProjectViewPaneWithAsyncSupport(project) {
    override fun getTitle(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "Easy-IT"
    }

    override fun getIcon(): Icon {
        return AllIcons.Ide.Link
    }

    override fun getId(): String {
        return ID
    }

    override fun getWeight(): Int {
        return 10
    }

    override fun createSelectInTarget(): SelectInTarget {
        return object : ProjectViewSelectInTarget(myProject) {
            override fun toString(): String {
                return ID
            }

            override fun getMinorViewId(): String {
                return ID
            }

            override fun getWeight(): Float {
                return 10F
            }
        }
    }

    override fun createStructure(): ProjectAbstractTreeStructureBase {
        return object : ProjectTreeStructure(myProject, ID) {
            override fun createRoot(project: Project, settings: ViewSettings): EasyItFileNode {
                var indexFile = FileUtils.findFileByRelativePath(myProject, IndexManager.ROOT_PATH)
                // get index file
                if (indexFile == null) {
                    // todo 弹窗提示

                    // 创建
                    indexFile = FileUtils.createFile(myProject, IndexManager.ROOT_PATH)
                }
                if (indexFile == null) {
                    indexFile = FileUtils.findFileByRelativePath(project, IndexManager.ROOT_PATH)
                }

                return EasyItFileNode(project, indexFile!!, true)
            }

            // Children will be searched in async mode
            override fun isToBuildChildrenInBackground(element: Any): Boolean {
                return true
            }
        }
    }

    override fun createTree(treeModel: DefaultTreeModel): ProjectViewTree {
        return object : ProjectViewTree(treeModel) {
            override fun isRootVisible(): Boolean {
                return true
            }
        }
    }

    companion object {
        const val ID = "EasyIT"
    }
}
package com.github.orezzero.easyitplugin.index.file

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.github.orezzero.easyitplugin.md.MarkdownLanguageUtils.isMarkdownType
import com.github.orezzero.easyitplugin.util.FileUtils
import com.github.orezzero.easyitplugin.util.MarkdownElementUtils
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.refactoring.suggested.startOffset
import org.intellij.plugins.markdown.lang.psi.MarkdownRecursiveElementVisitor
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownInlineLink
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDestination
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock


/**
 * This class is thread safe.
 */
@Service(Service.Level.PROJECT)
class IndexManager(val project: Project) {
    companion object {

        const val ROOT_PATH = "README.md"
        fun getInstance(project: Project): IndexManager {
            return if (project.isDisposed) throw IllegalStateException("project is disposed: $project") else project.getService(
                IndexManager::class.java
            )
        }
    }

    private val rwLock: ReadWriteLock = ReentrantReadWriteLock()
    private val readLock = rwLock.readLock()
    private val writeLock = rwLock.writeLock()

    /**
     * This map store each md file's index result.
     * Key of outer map is virtual file of the markdown file.
     * Key of inner map is link location , value of inner map is code location.
     * Inner map is a immutableMap
     *
     *
     */
    private val store: MutableMap<VirtualFile, Map<IndexEntry, IndexEntry>> = mutableMapOf()

    /**
     * Index md file and new file references in it. Indexed file reference will not be indexed.
     */
    fun index(rootPath: String) {
        FileUtils.findFileByRelativePath(project, rootPath)?.let {
            withWriteLock {
                index(it)
            }
        }
    }

    fun index(virtualFiles: List<VirtualFile>) {
        withWriteLock {
            for (virtualFile in virtualFiles) {
                index(virtualFile)
            }
        }
    }


    fun index(mdFile: VirtualFile) {
        val indexChangeFiles = mutableListOf<VirtualFile>()
        withWriteLock {
            PsiManager.getInstance(project).findFile(mdFile)?.let { psiFile ->

                val result = mutableMapOf<IndexEntry, IndexEntry>()
                val markdownRelativePath = FileUtils.getRelativePath(project, mdFile)
                val mdReferences = mutableListOf<VirtualFile>()

                psiFile.accept(object : MarkdownRecursiveElementVisitor() {
                    override fun visitLinkDestination(linkDestination: MarkdownLinkDestination) {
                        val linkDest = linkDestination.text
                        val parent = linkDestination.parent
                        if (parent is MarkdownInlineLink) {
                            // 统一一下, IndexEntry 中的 path ,均为基于 project 的相对路径
                            val name = parent.linkText.let {
                                MarkdownElementUtils.getLinkTextString(it)
                            }
                            FileUtils.findFileByRelativePath(mdFile, linkDest)?.let {
                                if (it.fileType.isMarkdownType()) {
                                    mdReferences.add(it)
                                }
                                // calculate dest (code file)
                                val codeLocation = IndexEntry(
                                    name,
                                    FileUtils.getRelativePath(project, it) + "#" + linkDest.substringAfter("#"),
                                    null
                                )
                                // calculate src (md file)
                                val lineNum =
                                    FileDocumentManager.getInstance().getDocument(mdFile)
                                        ?.getLineNumber(parent.startOffset) ?: 0
                                val linkLocation = IndexEntry.of(
                                    name,
                                    markdownRelativePath,
                                    lineNum + 1,
                                    linkDestination
                                )
                                result[linkLocation] = codeLocation
                                // todo trigger listener
                            }
                        }
                    }
                })
                store[mdFile] = result.toMap()
                indexChangeFiles.add(mdFile)

                for (mdReference in mdReferences) {
                    if (store[mdReference] == null) {
                        index(mdReference)
                    }
                }
            }
        }
        for (indexChangeFile in indexChangeFiles) {
            IndexListenerDispatcher.getInstance(project).indexChanged(indexChangeFile)
        }
    }


    /**
     * Get data in a file.
     * @return: A map with key: link location; value: code location
     */
    fun getFileData(virtualFile: VirtualFile): Map<IndexEntry, IndexEntry>? {
        return withReadLock { store[virtualFile] }
    }

    fun getAllDate(): Map<VirtualFile, Map<IndexEntry, IndexEntry>> {
        return withReadLock { store.toMap() }
    }


    fun withWriteLock(runnable: Runnable) {
        writeLock.lock()
        try {
            runnable.run()
        } finally {
            writeLock.unlock()
        }
    }

    fun <T> withReadLock(runnable: () -> T): T {
        readLock.lock()
        try {
            return runnable()
        } finally {
            readLock.unlock()
        }
    }


}
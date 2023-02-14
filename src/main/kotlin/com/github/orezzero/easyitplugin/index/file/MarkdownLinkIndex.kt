package com.github.orezzero.easyitplugin.index.file

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.intellij.util.io.VoidDataExternalizer
import org.intellij.plugins.markdown.lang.MarkdownFileType
import org.intellij.plugins.markdown.lang.psi.MarkdownRecursiveElementVisitor
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDestination

class MarkdownLinkIndex : FileBasedIndexExtension<String, Void>() {


    init {
        ApplicationManager.getApplication().messageBus.connect()
            .subscribe(VirtualFileManager.VFS_CHANGES, MdFileChangeListener {
                FileBasedIndex.getInstance().requestRebuild(NAME)
            })
    }

    override fun getName(): ID<String, Void> {
        return NAME
    }

    override fun getIndexer(): DataIndexer<String, Void, FileContent> {
        return DataIndexer<String, Void, FileContent> { inputData ->
            val psiFile = inputData.psiFile
            val map: Map<String, Void> = HashMap()
            println(inputData.psiFile.name)
            psiFile.accept(object : MarkdownRecursiveElementVisitor() {
                override fun visitLinkDestination(linkDestination: MarkdownLinkDestination) {
                    linkDestination.parent
                    println(linkDestination.text)
                }
            })
            map
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<Void> {
        // todo add generic type
        return VoidDataExternalizer.INSTANCE
    }

    override fun getVersion(): Int {
        return 1
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return DefaultFileTypeSpecificInputFilter(MarkdownFileType.INSTANCE)
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    companion object {
        val NAME = ID.create<String?, Void?>("MarkdownLink")
    }
}
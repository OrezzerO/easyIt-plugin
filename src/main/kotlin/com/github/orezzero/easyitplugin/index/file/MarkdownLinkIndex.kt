package com.github.orezzero.easyitplugin.index.file

import com.github.orezzero.easyitplugin.index.file.entry.IndexEntry
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.KeyDescriptor
import org.intellij.plugins.markdown.lang.MarkdownFileType

class MarkdownLinkIndex : FileBasedIndexExtension<IndexEntry, IndexEntry>() {

    init {
        ApplicationManager.getApplication().messageBus.connect()
            .subscribe(VirtualFileManager.VFS_CHANGES, MdFileChangeListener {
                FileBasedIndex.getInstance().requestRebuild(NAME)
            })
    }

    override fun getName(): ID<IndexEntry, IndexEntry> {
        return NAME
    }

    override fun getIndexer(): DataIndexer<IndexEntry, IndexEntry, FileContent> {
        return MarkdownDataIndexer()
    }

    override fun getKeyDescriptor(): KeyDescriptor<IndexEntry> {
        return IndexEntry.KEY_DESCRIPTOR_INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<IndexEntry> {
        return IndexEntry.DATA_EXTERNALIZER_INSTANCE
    }

    override fun getVersion(): Int {
        return 3
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return DefaultFileTypeSpecificInputFilter(MarkdownFileType.INSTANCE)
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    companion object {
        val NAME = ID.create<IndexEntry, IndexEntry>("MarkdownLink")
    }
}
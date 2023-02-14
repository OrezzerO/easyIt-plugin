package com.github.orezzero.easyitplugin.index.file

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.IOUtil
import com.intellij.util.io.KeyDescriptor
import org.intellij.plugins.markdown.lang.MarkdownFileType
import java.io.DataInput
import java.io.DataOutput

class MarkdownLinkIndex : FileBasedIndexExtension<String, List<KeyValue>>() {

    init {
        ApplicationManager.getApplication().messageBus.connect()
            .subscribe(VirtualFileManager.VFS_CHANGES, MdFileChangeListener {
                FileBasedIndex.getInstance().requestRebuild(NAME)
            })
    }

    override fun getName(): ID<String, List<KeyValue>> {
        return NAME
    }

    override fun getIndexer(): DataIndexer<String, List<KeyValue>, FileContent> {
        return MarkdownDataIndexer()
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<List<KeyValue>> {
        return object : DataExternalizer<List<KeyValue>> {
            override fun save(out: DataOutput, values: List<KeyValue>?) {
                out.writeInt(values!!.size)
                for (value in values) {
                    IOUtil.writeUTF(out, value.name)
                    IOUtil.writeUTF(out, value.dest)
                }
            }

            override fun read(input: DataInput): List<KeyValue> {
                val size = input.readInt()
                val infos = ArrayList<KeyValue>(size)
                for (i in 0 until size) {
                    infos.add(KeyValue(IOUtil.readUTF(input), IOUtil.readUTF(input)))
                }
                return infos
            }

        }
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
        val NAME = ID.create<String?, List<KeyValue>?>("MarkdownLink")
    }
}
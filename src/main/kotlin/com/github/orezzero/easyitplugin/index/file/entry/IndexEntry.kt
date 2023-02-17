package com.github.orezzero.easyitplugin.index.file.entry

import com.github.orezzero.easyitplugin.util.FileUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.KeyDescriptor
import java.io.DataInput
import java.io.DataOutput

data class IndexEntry(val name: String, val location: String, var startOffset: Int, var endOffset: Int) {

    companion object {
        val DATA_EXTERNALIZER_INSTANCE = LocationDataExternalizer()
        val KEY_DESCRIPTOR_INSTANCE = LocationKeyDescriptor()
        fun of(
            name: String,
            project: Project,
            virtualFile: VirtualFile,
            lineNum: Int,
            startOffset: Int,
            endOffset: Int
        ): IndexEntry {
            val lineStr = "L$lineNum"
            val relativePath = FileUtils.getRelativePath(project, virtualFile)
            val location = "$relativePath#$lineStr"
            return IndexEntry(name, location, startOffset, endOffset)
        }
    }

    class LocationDataExternalizer : DataExternalizer<IndexEntry> {
        override fun save(out: DataOutput, value: IndexEntry) {
            out.writeUTF(value.name)
            out.writeUTF(value.location)
            out.writeInt(value.startOffset)
            out.writeInt(value.endOffset)
        }

        override fun read(input: DataInput): IndexEntry {
            return IndexEntry(input.readUTF(), input.readUTF(), input.readInt(), input.readInt())
        }

    }

    class LocationKeyDescriptor : KeyDescriptor<IndexEntry> {
        override fun getHashCode(value: IndexEntry?): Int {
            return value.hashCode()
        }

        override fun isEqual(val1: IndexEntry?, val2: IndexEntry?): Boolean {
            return val1 == val2
        }

        override fun save(out: DataOutput, value: IndexEntry) {
            DATA_EXTERNALIZER_INSTANCE.save(out, value)
        }

        override fun read(input: DataInput): IndexEntry {
            return DATA_EXTERNALIZER_INSTANCE.read(input)
        }

    }
}
package com.github.orezzero.easyitplugin.stub

import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.intellij.plugins.markdown.lang.stubs.MarkdownStubElementType
import java.io.IOException

class MarkdownInlineLinkStubElementType(debugName: String) :
    MarkdownStubElementType<MarkdownInlineLinkStubElement, MarkdownInlineLink>(debugName) {
    override fun createElement(node: ASTNode): PsiElement {
        return MarkdownInlineLink(node)
    }

    override fun createPsi(stub: MarkdownInlineLinkStubElement): MarkdownInlineLink {
        return MarkdownInlineLink(stub, this)
    }

    override fun createStub(psi: MarkdownInlineLink, parentStub: StubElement<*>?): MarkdownInlineLinkStubElement {
        return MarkdownInlineLinkStubElement(parentStub!!, this, psi.text)
    }

    @Throws(IOException::class)
    override fun serialize(stub: MarkdownInlineLinkStubElement, dataStream: StubOutputStream) {
        writeUTFFast(dataStream, stub.indexedName)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): MarkdownInlineLinkStubElement {
        var indexedName: String? = null
        try {
            indexedName = dataStream.readUTFFast()
        } catch (e: IOException) {
            LOG.error("Cannot read data stream; ", e.message)
        }
        val actualName = if (StringUtil.isEmpty(indexedName)) null else indexedName
        return MarkdownInlineLinkStubElement(
            parentStub,
            this,
            actualName
        )
    }

    override fun indexStub(stub: MarkdownInlineLinkStubElement, sink: IndexSink) {
        val indexedName = stub.indexedName
        if (indexedName != null) {
            sink.occurrence(InlineLinkTextIndex.KEY, indexedName)
//            InlineLinkTextIndex.Companion.indexChanged(stub)
        }
    }

    companion object {
        private val LOG = Logger.getInstance(
            MarkdownInlineLinkStubElementType::class.java
        )

        @Throws(IOException::class)
        private fun writeUTFFast(dataStream: StubOutputStream, text: String?) {
            dataStream.writeUTFFast(text ?: "")
        }
    }
}
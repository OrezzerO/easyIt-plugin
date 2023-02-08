package com.github.orezzero.easyitplugin.stub

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.util.EventDispatcher

/**
 * Index of InlineLink actual full text.
 */
class InlineLinkTextIndex : StringStubIndexExtension<MarkdownInlineLink>() {
    override fun getKey(): StubIndexKey<String, MarkdownInlineLink> = KEY

    companion object {
        @JvmField
        val KEY: StubIndexKey<String, MarkdownInlineLink> = StubIndexKey.createIndexKey("markdown.inlineLink")

    private val dispatcher = EventDispatcher.create<LinkIndexListener?>(LinkIndexListener::class.java)

    fun addLinkFileListener(listener: LinkIndexListener) {
      dispatcher.addListener(listener)
    }

    fun indexChanged(ele : MarkdownInlineLinkStubElement){
      dispatcher.multicaster.indexChanged(ele)
    }

    fun removeLinkIndexListener(listener: LinkIndexListener) {
      dispatcher.removeListener(listener)
    }
    }
}

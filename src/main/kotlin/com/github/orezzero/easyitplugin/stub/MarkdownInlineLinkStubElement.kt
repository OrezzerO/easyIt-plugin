package com.github.orezzero.easyitplugin.stub

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.intellij.plugins.markdown.lang.stubs.MarkdownStubElementBase

class MarkdownInlineLinkStubElement(
  parent: StubElement<*>,
  elementType: IStubElementType<*, *>,
  val indexedName: String?
) : MarkdownStubElementBase<MarkdownInlineLink?>(parent, elementType)

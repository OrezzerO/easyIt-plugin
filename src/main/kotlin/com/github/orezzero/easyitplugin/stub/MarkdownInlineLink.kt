package com.github.orezzero.easyitplugin.stub

import com.intellij.lang.ASTNode
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLink
import org.intellij.plugins.markdown.lang.stubs.MarkdownStubBasedPsiElementBase
import org.intellij.plugins.markdown.lang.stubs.MarkdownStubElement

// todo Markdown header 实现了 PsiExternalReferenceHost 接口, 需要看下 InlineLink 是否需要实现这个接口
class MarkdownInlineLink : MarkdownStubBasedPsiElementBase<MarkdownStubElement<*>>, MarkdownLink {
    constructor(node: ASTNode) : super(node)
    constructor(stub: MarkdownInlineLinkStubElement, type: MarkdownInlineLinkStubElementType) : super(stub, type)

}

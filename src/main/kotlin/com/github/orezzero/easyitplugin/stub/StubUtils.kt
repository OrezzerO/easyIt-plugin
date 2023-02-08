package com.github.orezzero.easyitplugin.stub

import com.intellij.psi.PsiElement
import com.intellij.psi.util.siblings
import org.intellij.markdown.IElementType
import org.intellij.plugins.markdown.lang.MarkdownElementType
import java.util.*

object StubUtils {
    private var markdownToPlatformTypeMap: MutableMap<IElementType, com.intellij.psi.tree.IElementType>? = null
    private var platformToMarkdownTypeMap: MutableMap<com.intellij.psi.tree.IElementType, IElementType>? = null

    @JvmStatic
    fun additionalPlatformType(markdownType: IElementType): com.intellij.psi.tree.IElementType {
        synchronized(MarkdownElementType::class.java) {
            // TODO: now only INLINE_LINK needs to be resolved
            val platformType: com.intellij.psi.tree.IElementType =
                MarkdownInlineLinkStubElementType(markdownType.toString())
            addToMap(markdownType, platformType)
            return platformType
        }
    }

    private fun addToMap(markdownType: IElementType, platformType: com.intellij.psi.tree.IElementType) {
        getMarkdownToPlatformTypeMap()!![markdownType] = platformType
        getPlatformToMarkdownTypeMap()!![platformType] = markdownType
    }

    fun getMarkdownToPlatformTypeMap(): MutableMap<IElementType, com.intellij.psi.tree.IElementType>? {
        return if (markdownToPlatformTypeMap != null) {
            markdownToPlatformTypeMap
        } else {
            initByOriginClass()
            Objects.requireNonNull(
                markdownToPlatformTypeMap
            )
        }
    }

    fun getPlatformToMarkdownTypeMap(): MutableMap<com.intellij.psi.tree.IElementType, IElementType>? {
        return if (platformToMarkdownTypeMap != null) {
            platformToMarkdownTypeMap
        } else {
            initByOriginClass()
            Objects.requireNonNull(
                platformToMarkdownTypeMap
            )
        }
    }

    private fun initByOriginClass() {
        try {
            val markdownToPlatformTypeMapField =
                MarkdownElementType::class.java.getDeclaredField("markdownToPlatformTypeMap")
            markdownToPlatformTypeMapField.isAccessible = true
            var o = markdownToPlatformTypeMapField.get(null)
            markdownToPlatformTypeMap =
                Objects.requireNonNull(o) as MutableMap<IElementType, com.intellij.psi.tree.IElementType>
            val platformToMarkdownTypeMapField =
                MarkdownElementType::class.java.getDeclaredField("platformToMarkdownTypeMap")
            platformToMarkdownTypeMapField.isAccessible = true
            o = platformToMarkdownTypeMapField.get(null)
            platformToMarkdownTypeMap =
                Objects.requireNonNull(o) as MutableMap<com.intellij.psi.tree.IElementType, IElementType>
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }


}

fun PsiElement.children(): Sequence<PsiElement> {
    return childrenOrNull().orEmpty()
}

fun PsiElement.childrenOrNull(): Sequence<PsiElement>? {
    return firstChild?.siblings(forward = true, withSelf = true)
}
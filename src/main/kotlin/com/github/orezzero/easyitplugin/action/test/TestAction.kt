package com.github.orezzero.easyitplugin.action.test

import com.github.orezzero.easyitplugin.runUndoTransparentWriteAction
import com.github.orezzero.easyitplugin.util.FileUtils.findFileByRelativePath
import com.github.orezzero.easyitplugin.util.MarkdownElementUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiRecursiveElementWalkingVisitor

class TestAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val virtualFile = findFileByRelativePath(project!!, "README.md")
        val file = PsiManager.getInstance(project).findFile(virtualFile!!)
        val elementMap: MutableMap<String, MutableList<PsiElement>> = HashMap()
        file!!.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                val name = element.javaClass.name
                println(name)
                val psiElements = elementMap.computeIfAbsent(name) { ArrayList() }
                psiElements.add(element)
                super.visitElement(element)
            }
        }
        )
        println(elementMap.size)
        var list = elementMap.get("org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDestination")!!
        runUndoTransparentWriteAction {
            for (l in list) {
                var inlineLink = MarkdownElementUtils.createMarkdownText(project, "src/a/b/c/A.kt#L13")
                l.firstChild.replace(inlineLink)
            }
        }


    }
}
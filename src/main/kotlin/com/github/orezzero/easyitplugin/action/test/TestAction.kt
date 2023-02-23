package com.github.orezzero.easyitplugin.action.test


import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.impl.IdeFrameImpl


class TestAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        var project = e.project!!


//        // Get the WindowManager instance
//        // Get the WindowManager instance
//        val windowManager = WindowManager.getInstance()
//
//        // Get the FileEditorManager for the current project
//
//        // Get the FileEditorManager for the current project
//        val fileEditorManager = FileEditorManager.getInstance(project)
//
//        // Get the currently selected Editor instance
//
//        // Get the currently selected Editor instance
//        val editor: Editor? = fileEditorManager.selectedTextEditor
//
//        // Get the IdeFrameImpl associated with the Editor
//
//        // Get the IdeFrameImpl associated with the Editor
//        val ideFrame = windowManager.getFrame(project) as IdeFrameImpl?
//
//        // Get the EditorWindow associated with the Editor
//
//        // Get the EditorWindow associated with the Editor
//        val editorWindow: EditorWindow = fileEditorManager.getOrCreateEditorWindow(editor).getWindow()
//
//        // Do something with the EditorWindow
//        // For example, get the title of the frame:
//
//        // Do something with the EditorWindow
//        // For example, get the title of the frame:
//        val frameTitle: String = editorWindow.getFrameTitle()

    }
}
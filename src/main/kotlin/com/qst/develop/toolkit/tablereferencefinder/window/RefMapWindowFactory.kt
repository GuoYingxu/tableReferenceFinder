package com.qst.develop.toolkit.tablereferencefinder.window

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class RefMapWindowFactory: ToolWindowFactory {
override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = TableReferenceFinderPanelFactory.createPanel(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
package com.qst.develop.toolkit.tablereferencefinder.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.qst.develop.toolkit.tablereferencefinder.window.TableReferenceFinderPanel
import com.qst.develop.toolkit.tablereferencefinder.window.TableReferenceFinderPanelFactory

class FindRefMapAction: DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        //打开插件面板
        val panel = e.project?.getUserData(TableReferenceFinderPanel.TABLE_REFERENCE_FINDER_PANEL)

        if (panel == null) {
            val tableReferenceFinderPanel =  TableReferenceFinderPanelFactory.createPanel(e.project!!)
            e.project?.putUserData(TableReferenceFinderPanel.TABLE_REFERENCE_FINDER_PANEL, tableReferenceFinderPanel)
            tableReferenceFinderPanel.show()
        } else {
            panel.show()
        }
    }
}
package com.qst.develop.toolkit.tablereferencefinder.window

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel

class TableReferenceFinderPanel(project: Project) : JPanel(){
    companion object {
        val TABLE_REFERENCE_FINDER_PANEL: Key<TableReferenceFinderPanel> = Key.create("TableReferenceFinderPanel")
    }
    init {
        val label = JLabel("这里进行搜索和展示")
        val scrollPanel = JBScrollPane(label);

        add(scrollPanel)
    }


}
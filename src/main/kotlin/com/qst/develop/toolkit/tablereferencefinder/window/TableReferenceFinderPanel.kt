package com.qst.develop.toolkit.tablereferencefinder.window

import com.intellij.openapi.util.Key
import com.intellij.ui.components.JBScrollPane
import javax.swing.JLabel
import javax.swing.JPanel

class TableReferenceFinderPanel: JPanel(){
    companion object {
        val TABLE_REFERENCE_FINDER_PANEL: Key<TableReferenceFinderPanel> = Key.create("TableReferenceFinderPanel")
    }

    init {
        val label = JLabel("Table Reference Finder")
        val scrollPanel = JBScrollPane(label);
        add(scrollPanel)

    }

}
package com.qst.develop.toolkit.tablereferencefinder.window

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.ui.treeStructure.Tree
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.actionButton
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.treeStructure.SimpleTree
import com.qst.develop.toolkit.tablereferencefinder.service.FinderService
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.tree.TreeSelectionModel

class TableReferenceFinderPanel(project: Project) : JPanel(){
    companion object {
        val TABLE_REFERENCE_FINDER_PANEL: Key<TableReferenceFinderPanel> = Key.create("TableReferenceFinderPanel")
    }
    init {
        val jTree = project.service<FinderService>().treeCom
        jTree.emptyText.text = "Nothing to display"
        jTree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        val scrollPanel = JBScrollPane(jTree);
        scrollPanel.setViewportView(jTree)
        scrollPanel.setHorizontalScrollBarPolicy(JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        scrollPanel.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED)
        scrollPanel.setWheelScrollingEnabled(true)
        layout = BorderLayout()
        add(createInput(project), BorderLayout.NORTH)
        add(scrollPanel, BorderLayout.CENTER)
    }
    var searchKeys ="";

//    private fun createSimpleTree():SimpleTree {
//        println("init treecom")
//        return object:SimpleTree() {
//            override fun paintComponents(g: Graphics?) {
//                super.paintComponents(g)
//                val myLabel = JLabel("Nothing to display")
//                myLabel.font = font
//                myLabel.background = background
//                myLabel.foreground = foreground
//                val bounds = bounds
//                val size: Dimension = myLabel.preferredSize
//                myLabel.setBounds(0, 0, size.width, size.height)
//                val x = (bounds.width - size.width) / 2
//                val g2 = g?.create(bounds.x + x, bounds.y + 20, bounds.width, bounds.height)
//                try {
//                    myLabel.paint(g2)
//                } finally {
//                    g2?.dispose()
//                }
//            }
//        }
//    }
    private fun createInput(project: Project) =
         panel {
             row("Find:") {
                 val action = object : DumbAwareAction("Action text", "Action description", AllIcons.Actions.Find) {
                     override fun actionPerformed(e: AnActionEvent) {
                         project.getService(FinderService::class.java).startSearchXML(searchKeys)
                     }
                 }
                 textField().applyToComponent {
                     document.addDocumentListener(object : DocumentListener {
                         override fun insertUpdate(e: DocumentEvent?) {
                             searchKeys = text
                         }

                         override fun removeUpdate(e: DocumentEvent?) {
                             searchKeys = text
                         }

                         override fun changedUpdate(e: DocumentEvent?) {
                             searchKeys = text
                         }
                     })
                 }.bindText(::searchKeys)
                 actionButton(action)
             }
        }

}
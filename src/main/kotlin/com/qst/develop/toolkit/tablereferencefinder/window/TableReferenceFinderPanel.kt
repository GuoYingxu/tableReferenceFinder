package com.qst.develop.toolkit.tablereferencefinder.window

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.actionButton
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.qst.develop.toolkit.tablereferencefinder.service.FinderService
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class TableReferenceFinderPanel(project: Project) : JPanel(){
    companion object {
        val TABLE_REFERENCE_FINDER_PANEL: Key<TableReferenceFinderPanel> = Key.create("TableReferenceFinderPanel")
    }
    init {
        val label = JLabel("这里进行搜索和展示")
        val scrollPanel = JBScrollPane(label);
        scrollPanel.setViewportView(label)
        scrollPanel.setHorizontalScrollBarPolicy(JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        scrollPanel.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED)
        scrollPanel.setWheelScrollingEnabled(true)
        layout = BorderLayout()
        add(createInput(project), BorderLayout.NORTH)
        add(scrollPanel, BorderLayout.CENTER)
    }
    var searchKeys ="";
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
package com.qst.develop.toolkit.tablereferencefinder.window

import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull

class TableReferenceFinderPanelFactory {
    companion object{
        fun createPanel(@NotNull project:Project): TableReferenceFinderPanel {
            return TableReferenceFinderPanel()
        }
    }
}
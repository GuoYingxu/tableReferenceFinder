package com.qst.develop.toolkit.tablereferencefinder.service

import com.intellij.ide.DefaultTreeExpander
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.tree.AsyncTreeModel
import com.intellij.ui.tree.StructureTreeModel
import com.intellij.ui.treeStructure.SimpleNode
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.ui.treeStructure.SimpleTreeStructure

class RefTreeStructure(private val project: Project, tree: SimpleTree):SimpleTreeStructure() {
    private val structureTreeModel = StructureTreeModel(this, project)
    private val asyncTreeModel = AsyncTreeModel(structureTreeModel, project)
    private val rootNode = RootNode(project)
    override fun getRootElement():RootNode = rootNode

    init {
        tree.showsRootHandles = true
        tree.isRootVisible = true
        tree.model = asyncTreeModel
        DefaultTreeExpander(tree).expandAll()

        println(project.service<FinderService>().rootChainModels.size)
    }

    fun update(){
        structureTreeModel.invalidateAsync()
    }
    inner class RootNode(val holder:Project):SimpleNode(){
        override fun getName(): String  = "引用查询结果： "

        override fun getChildren(): Array<SimpleNode> =
            holder.service<FinderService>().rootChainModels.map{
                ChainNode(it)
            }.toTypedArray()

    }
}
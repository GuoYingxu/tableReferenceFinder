package com.qst.develop.toolkit.tablereferencefinder.service

import com.intellij.lang.java.JavaLanguage
import com.intellij.ui.treeStructure.SimpleNode
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.util.OpenSourceUtil
import com.qst.develop.toolkit.tablereferencefinder.ToolKitIcon
import com.qst.develop.toolkit.tablereferencefinder.model.RefChainModel
import java.awt.event.InputEvent
import javax.swing.Icon

class ChainNode(private val chainModel:RefChainModel) : SimpleNode(){

    init {
        if(chainModel.isAccessor) {
            templatePresentation.setIcon(ToolKitIcon.getIcon("accessor"))
        }else
        if(chainModel.isMapper) {
            templatePresentation.setIcon(ToolKitIcon.getIcon("mapper"))
        }else
        if(chainModel.isService) {
            templatePresentation.setIcon(ToolKitIcon.getIcon("service"))
        }else
        if(chainModel.isController) {
            templatePresentation.setIcon(ToolKitIcon.getIcon("controller"))
        }else {
            templatePresentation.setIcon(ToolKitIcon.getIcon("unkown"))
        }
    }
    override fun getChildren(): Array<SimpleNode> {
        return chainModel.refs.map {
            ChainNode(it)
        }.toTypedArray()
    }
    override fun handleDoubleClickOrEnter(tree: SimpleTree?, inputEvent: InputEvent?) {
        try {
            tree?.let {
                val currentNode = it.selectedNode as ChainNode
                val psiMethodItem = currentNode.chainModel.method
                if(psiMethodItem.isValid && psiMethodItem.language == JavaLanguage.INSTANCE) {
                    OpenSourceUtil.navigate(psiMethodItem)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun getName(): String {
        return chainModel.method.name
    }

    override fun setIcon(closedIcon: Icon?) {
        super.setIcon(closedIcon)
    }
}
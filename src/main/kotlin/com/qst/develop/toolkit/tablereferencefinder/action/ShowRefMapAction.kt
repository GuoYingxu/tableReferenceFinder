package com.qst.develop.toolkit.tablereferencefinder.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.service
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeElement
import com.intellij.psi.util.PsiUtil
import com.qst.develop.toolkit.tablereferencefinder.service.FinderService

class ShowRefMapAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        e.project?.service<FinderService>()?.chainModelMap?.clear()
        e.project?.service<FinderService>()?.rootChainModels?.clear()
        e.project?.service<FinderService>()?.findReference(null,e.getData(PlatformDataKeys.PSI_ELEMENT) as PsiMethod)
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val presentation = e.presentation
        val psiElement = e.getData(PlatformDataKeys.PSI_ELEMENT)
        if(psiElement != null){
            // 判断是否是方法
            if(psiElement is PsiMethod){
                presentation.isEnabledAndVisible = true
                return
            }
        }
        presentation.isEnabledAndVisible = false
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return super.getActionUpdateThread()
    }
}
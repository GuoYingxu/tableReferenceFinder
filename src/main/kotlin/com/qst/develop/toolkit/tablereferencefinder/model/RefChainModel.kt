package com.qst.develop.toolkit.tablereferencefinder.model

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod

data class RefChainModel(
    var from: PsiMethod?,
    val method: PsiMethod,
    val clazz: PsiClass,
    var refs: List<RefChainModel> = emptyList(),
    var module: String = "",
    var requestMethod: String = "",
    var requestUrl: String = "",
    var comment: String = "",
    val isService:Boolean =false,
    val isController:Boolean= false,
    val isAccessor:Boolean = false,
    val isMapper:Boolean = false,
    ) {
    fun toChainList():MutableList<String> {
//        println("type:$isService,$isController,$isAccessor,$isMapper")
        val chainList = mutableListOf<String>()
        if(refs.isNotEmpty()) {
            for (ref in refs) {
                ref.toChainList().forEach {
                    chainList.add("${clazz.name}::${method.name} -> $it")
                }
            }
        }else {
            chainList.add("${clazz.name}::${method.name}")
        }
        return chainList;
    }
}

package com.qst.develop.toolkit.tablereferencefinder

import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.*

class Uitls {
    companion object {
        /**
         * 通过class 注解判断是不是controller
         */
        fun isSpringController(psiClass:PsiClass):Boolean {
            return psiClass.annotations.any {
                it.qualifiedName == "org.springframework.stereotype.Controller" ||
                        it.qualifiedName == "org.springframework.web.bind.annotation.RestController"
            }
        }
        /**
         * 通过class 注解判断是不是service
         */
        fun isSpringService(psiClass:PsiClass):Boolean {
            return psiClass.annotations.any {
                it.qualifiedName == "org.springframework.stereotype.Service"
            }
        }
        /**
         * 通过class 注解判断是不是mapper
         */
        fun isSpringMapper(psiClass:PsiClass):Boolean {
            return psiClass.annotations.any {
//                println(it.qualifiedName)
                it.qualifiedName == "org.springframework.stereotype.Component"
            }
        }

        /**
         * 通过class 注解判断是不是Accessor
         */
        fun isSpringAccessor(psiClass:PsiClass):Boolean {
            return psiClass.annotations.any {
                it.qualifiedName == "org.springframework.stereotype.Repository"
            }
        }

      fun getRequestComment(psiMethod: PsiMethod):String {
            var description = ""
           (psiMethod as? PsiDocCommentOwner)?.docComment?.descriptionElements?.map {
                if (it.text.isNotBlank() && !it.text.startsWith("@")) {
                    description = it.text.trim()
                    return@map
                }
            }
            return description
        }

        fun getModule(psiClass: PsiClass):String {
            return ModuleUtilCore.findModuleForPsiElement(psiClass)?.name ?: ""
        }

        fun getRequestMethod(psiMethod: PsiMethod):String {
            var methodName = "Get"
            for (annotation in psiMethod.annotations) {
                if(annotation.qualifiedName?.contains("Mapping") == true) {
                    if(annotation.qualifiedName?.contains("GetMapping") == true) {
                        methodName = "Get"
                    }
                    if(annotation.qualifiedName?.contains("PostMapping") == true) {
                        methodName =  "Post"
                    }
                    if(annotation.qualifiedName?.contains("DeleteMapping") == true) {
                        methodName =  "Delete"
                    }
                    if(annotation.qualifiedName?.contains("PutMapping") == true) {
                        methodName =  "Put"
                    }
                    if(annotation.qualifiedName?.contains("PatchMapping") == true) {
                        methodName =  "Patch"
                    }
                }
            }
            return methodName
        }

        fun getRequestPaths(psiMethod:PsiMethod):List<String> {
            val clazz = psiMethod.containingClass ?: return emptyList()

            val prefix = getRequestMappings(clazz.annotations.find {
                it.qualifiedName == "org.springframework.web.bind.annotation.RequestMapping"
            } ?: return emptyList()).firstOrNull() ?: ""

            val annotations = psiMethod.modifierList.annotations

            if (annotations.isEmpty()) {
                return emptyList()
            }
            val requestMappingPaths = ArrayList<String>()

            annotations.forEach {
                    annotation ->
                        SpringRequestMethodAnnotation.entries.forEach {
                            value ->
                                if(value.qualifiedName == annotation.qualifiedName || value.qualifiedName.endsWith(annotation.qualifiedName!!)) {
                                    requestMappingPaths.add(prefix +"/"+ getRequestMappings(annotation).firstOrNull())
                                }
                        }
            }
            return requestMappingPaths
        }

        /**
         * 解析 RequestMapping(GetMapping,PostMapping,PatchMapping,DeleteMapping,PutMapping) 注解的 method, url
         * 当 注解的 value 为数组时，会解析为多个 RequestPath
         * 因此返回值为 List<String>
         *
         * @param annotation RequestMapping 注解
         * @param defaultValue 默认值 当value 为空时，使用默认值 ‘’
         * @return List<RequestPath>
         */
        private fun getRequestMappings(annotation: PsiAnnotation, defaultValue:String = ""):List<String> {
//            println("getRequestMappings::${annotation.text}")
            val requestMappings:MutableList<String> = ArrayList()
            var requestMappingValues = getAnnotationAttributeValues(annotation,"value")
//            println("requestMappingValues::${requestMappingValues}")
            val requestMethodAnnotation = SpringRequestMethodAnnotation.getRequestMethodAnnotation(annotation.qualifiedName!!)
            val requestMappingMethods = ArrayList<String>()
            if(requestMethodAnnotation?.methodName != null) {
                requestMappingMethods.add(requestMethodAnnotation.methodName!!)
            }else {
                requestMappingMethods.addAll( getAnnotationAttributeValues(annotation,"method"))
            }
//            println("requestMappingMethods::${requestMappingMethods}")
            // value 为空时，使用默认值 ‘’
            if(requestMappingValues.isEmpty()) {
                requestMappingValues = listOf(defaultValue)
            }
            return requestMappingValues
        }
        private fun getAnnotationAttributeValues(annotation: PsiAnnotation, attrName: String):List<String> {
            val value = annotation.findAttributeValue(attrName) ?: return emptyList()
            // value 值是引用表达式
            if(value is PsiReferenceExpression) {
//                println(" is PsiReferenceExpression")
                return listOf(value.text.removeSurrounding("\""))
            }
            // value 值是字面量表达式
            if(value is PsiLiteralExpression){
//                println(" is PsiLiteralExpression")
                return listOf(value.value.toString().removeSurrounding("\""))
            }
            // value 值是数组表达式
            if(value is PsiArrayInitializerMemberValue) {
//                println(" is PsiArrayInitializerMemberValue")
                return value.initializers.map { it.text.removeSurrounding("\"") }
            }
            return emptyList()

        }


    }
}
enum class SpringRequestMethodAnnotation(var qualifiedName:String,var methodName:String?){
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping",null),
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping","GET"),
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping","POST"),
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping","PUT"),
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping","DELETE"),
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping","PATCH"),
    ;

    companion object {
        fun getRequestMethodAnnotation(qualifiedName:String):SpringRequestMethodAnnotation? {
//            println("qualifiedName::${qualifiedName}")
            values().forEach {
                if (it.qualifiedName == qualifiedName || it.qualifiedName.endsWith(qualifiedName)) {
                    return it
                }
            }
            return null
        }

    }

}
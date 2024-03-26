package com.qst.develop.toolkit.tablereferencefinder.service

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex.getFiles
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile


@Service(Service.Level.PROJECT)
class FinderService(private val project: Project) {
    fun startSearchXML(tableName: String) {
        println("startSearchXML:::$tableName")
        // TODO: 2021/8/17 0017  查询Mapper.xml 文件中的表名，找到对应的sql语句的ID
        val sqlIdList = mutableListOf<String?>()
        val psiFiles = getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project))
        psiFiles.stream().filter { file -> file.name.contains("Mapper") }.map { file ->
            PsiManager.getInstance(project).findFile(file) as XmlFile
        }.forEach { xmlFile ->
            //println("fileName:::${xmlFile.name}")
            val rootTag = xmlFile.rootTag
            val subTags = rootTag?.subTags
            subTags?.forEach { tag ->
                val id = tag.getAttributeValue("id")
                if (tag.text.contains(tableName)) {
                    sqlIdList.add(id)
                }
            } }

        //sqlIdList.forEach(::println)
        //sqlIdList
    }
    /**
     * 获取mapperId
     */
    fun getMapperId(tableName: String) {
        println("getMapperId:::$tableName")
    }
    /**
     * 获取Accessor方法
     */
    fun getAccessorMethod(mapperId: String) {
        println("getAccessorMethod:::$mapperId")
    }

    fun getServiceMethod(accessorMethod: String) {
        println("getServiceMethod:::$accessorMethod")
    }

    fun getControllerMethod(serviceMethod: String) {
        println("getControllerMethod:::$serviceMethod")
    }

}
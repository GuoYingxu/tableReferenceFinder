package com.qst.develop.toolkit.tablereferencefinder.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class FinderService(private val project: Project) {
    fun startSearchXML(tableName: String) {
        println("startSearchXML:::$tableName")
        // TODO: 2021/8/17 0017  查询Mapper.xml 文件中的表名，找到对应的sql语句的ID

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
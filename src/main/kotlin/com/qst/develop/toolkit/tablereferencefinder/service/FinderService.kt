package com.qst.develop.toolkit.tablereferencefinder.service

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.util.Query
import com.qst.develop.toolkit.tablereferencefinder.model.RefChainModel
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JLabel
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex.getFiles
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile


@Service(Service.Level.PROJECT)
class FinderService(private val project: Project) {

    private var treeStructure:RefTreeStructure? = null

    private var chainModelList:MutableList<RefChainModel> = mutableListOf()

    var rootChainModels:MutableList<RefChainModel> = mutableListOf()

    var treeCom:SimpleTree=  createSimpleTree()
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
        //println(sqlIdList)

    }
    private fun createSimpleTree():SimpleTree {
        println("init treecom")
        return object:SimpleTree() {
            override fun paintComponents(g: Graphics?) {
                super.paintComponents(g)
                val myLabel = JLabel("Nothing to display")
                myLabel.font = font
                myLabel.background = background
                myLabel.foreground = foreground
                val bounds = bounds
                val size: Dimension = myLabel.preferredSize
                myLabel.setBounds(0, 0, size.width, size.height)
                val x = (bounds.width - size.width) / 2
                val g2 = g?.create(bounds.x + x, bounds.y + 20, bounds.width, bounds.height)
                try {
                    myLabel.paint(g2)
                } finally {
                    g2?.dispose()
                }
            }
        }
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

    fun findReference(from:PsiMethod?,method:PsiMethod) {
        println("findReference:::${method.name}")
        val refModel =  getMethodCallChain(from,method)
        chainModelList.add(refModel)
        rootChainModels.add(refModel);
        refModel.toChainList().forEach {
            println(it)
        }

        // 更新树
        updateTree()
    }



    private fun updateTree() {
        println(project)
        if(project == null) return;
        if(treeCom == null) return;
        // 异步执行
        if(treeStructure == null) {
            treeStructure = RefTreeStructure(project,treeCom!!)
        }
        treeStructure!!.update();
    }
    /**
     * 获取方法调用链
     */
    private fun getMethodCallChain( from:PsiMethod?,  method: PsiMethod): RefChainModel {
        val refs =  getMethodCallChainRecursive(from,method)
        method.containingClass!!.annotations.forEach {
            println("annotation:${it.qualifiedName}")
        }
        return RefChainModel(from,method,method.containingClass!!,refs,
            isService = method.containingClass!!.hasAnnotation("org.springframework.stereotype.Service"),
            isController = method.containingClass!!.hasAnnotation("org.springframework.stereotype.RestController"),
            isAccessor = method.containingClass!!.hasAnnotation("org.springframework.stereotype.Repository"),
            isMapper = method.containingClass!!.hasAnnotation("org.springframework.stereotype.Comment")
        )
    }

    private fun getMethodCallChainRecursive(from:PsiMethod?,method: PsiMethod):MutableList<RefChainModel> {
        val query: Query<PsiReference> = ReferencesSearch.search(method)
        val callChain = mutableListOf<RefChainModel>()
        for (psiReference in query) {
            val element = psiReference.element
            val parent = PsiTreeUtil.getParentOfType(element, PsiMethod::class.java)
            if(parent!=null) {
                val clazz = parent.containingClass
                val refs = getMethodCallChainRecursive(method,parent)
                callChain.add(RefChainModel(method,parent,clazz!!,refs,
                    isService = clazz.hasAnnotation("org.springframework.stereotype.Service"),
                    isController = clazz.hasAnnotation("org.springframework.stereotype.RestController"),
                    isAccessor = clazz.hasAnnotation("org.springframework.stereotype.Repository"),
                    isMapper = clazz.hasAnnotation("org.springframework.stereotype.Comment")
                ))
            }
        }
        return callChain
    }}
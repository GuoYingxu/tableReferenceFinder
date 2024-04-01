package com.qst.develop.toolkit.tablereferencefinder.service

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.util.Query
import com.qst.develop.toolkit.tablereferencefinder.model.RefChainModel
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JLabel
import com.intellij.psi.search.FileTypeIndex.getFiles
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile
import com.qst.develop.toolkit.tablereferencefinder.Uitls.Companion.getModule
import com.qst.develop.toolkit.tablereferencefinder.Uitls.Companion.getRequestComment
import com.qst.develop.toolkit.tablereferencefinder.Uitls.Companion.getRequestMethod
import com.qst.develop.toolkit.tablereferencefinder.Uitls.Companion.getRequestPaths
import com.qst.develop.toolkit.tablereferencefinder.Uitls.Companion.isSpringAccessor
import com.qst.develop.toolkit.tablereferencefinder.Uitls.Companion.isSpringController
import com.qst.develop.toolkit.tablereferencefinder.Uitls.Companion.isSpringMapper
import com.qst.develop.toolkit.tablereferencefinder.Uitls.Companion.isSpringService
import java.nio.file.Files
import java.nio.file.Paths


@Service(Service.Level.PROJECT)
class FinderService(private val project: Project) {

    private var treeStructure: RefTreeStructure? = null

    var chainModelMap: MutableMap<String,RefChainModel> = mutableMapOf()
    var rootChainModels: MutableList<RefChainModel> = mutableListOf()

    var treeCom: SimpleTree = createSimpleTree()
    fun startSearchXML(tableName: String) {
        rootChainModels.clear();
        chainModelMap.clear();
        println("startSearchXML:::$tableName")
        // TODO: 2021/8/17 0017  查询Mapper.xml 文件中的表名，找到对应的sql语句的ID
        val sqlIdList = mutableListOf<String?>()
        val psiFiles = getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project))
        val pattern = Regex("\\s$tableName\\s|`$tableName`")
        psiFiles.stream().filter { file ->
            file.name.endsWith("Mapper.xml")
        }.filter { file ->
            val xmlFile = PsiManager.getInstance(project).findFile(file) as XmlFile
            pattern.containsMatchIn(xmlFile.text)
        }.forEach { file ->
            val xmlFile = PsiManager.getInstance(project).findFile(file) as XmlFile
//            println("fileName:::${xmlFile.name}")
            val rootTag = xmlFile.rootTag
            val subTags = rootTag?.subTags
            rootTag?.getAttributeValue("namespace")?.let { mapperClass ->
                val psiInterface =
                    JavaPsiFacade.getInstance(project).findClass(mapperClass, GlobalSearchScope.allScope(project))
                        ?: return@forEach
                println("mapperInterface :::${psiInterface.name}")
                subTags?.filter { tag ->
                    pattern.containsMatchIn(tag.text)
                }?.forEach { tag ->
                    val id = tag.getAttributeValue("id")
                    psiInterface.findMethodsByName(id, false).forEach {
//                        println("method:::${it.name}")
                        findReference(null, it)
                    }
                }
            }
        }
    }

    private fun createSimpleTree(): SimpleTree {
        println("init treecom")
        return object : SimpleTree() {
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

    fun findReference(from: PsiMethod?, method: PsiMethod) {
        val refModel = getMethodCallChain(from, method)
        val clazz = method.containingClass
        val key = "${clazz!!.name}:${method.name}"
        chainModelMap[key] = refModel;
        rootChainModels.add(refModel);

        refModel.toChainList().forEach {
            println(it)
        }

        val path = Paths.get(project.basePath!!, "output", "${method.name}-reference.md")
        if(!Files.exists(path.parent)) {
            Files.createDirectory(path.parent)
        }
        if(!Files.exists(path)) {
            Files.createFile(path)
        }
        val lines:MutableList<String> = mutableListOf();
        lines.add("## ${clazz.name}::${method.name}")
        lines.add("|module|key| url| comment|")
        lines.add("|---|---|---|---|")

        chainModelMap.entries.forEach(){
            if(it.value.isController) {
                lines.add("|${it.value.module}|${it.key}| ${it.value.requestMethod}:${it.value.requestUrl}|${it.value.comment}|")
//                println("API:::${it.value.clazz.name}:${it.value.method.name} \t ${it.value.requestMethod}:${it.value.requestUrl}  \t ${it.value.comment}")
            }
        }
        // 写文件
        path.toFile().writeText(lines.joinToString("\n"), Charsets.UTF_8)
        // 更新树
        updateTree()
    }


    private fun updateTree() {
//        println(project)
        // 异步执行
        if (treeStructure == null) {
            treeStructure = RefTreeStructure(project, treeCom)
        }
        treeStructure!!.update();
    }

    /**
     * 获取方法调用链
     */
    private fun getMethodCallChain(from: PsiMethod?, method: PsiMethod): RefChainModel {
        val refs = getMethodCallChainRecursive(from, method)
//        method.containingClass!!.annotations.forEach {
//            println("annotation:${it.qualifiedName}")
//        }
        return RefChainModel(
            from, method, method.containingClass!!, refs,
            isService = isSpringService(method.containingClass!!),
            isController = isSpringController(method.containingClass!!),
            isAccessor = isSpringAccessor(method.containingClass!!),
            isMapper = isSpringMapper(method.containingClass!!)
        )
    }

    /**
     * 递归获取方法调用链
     *
     * @param from 起始方法，如果为null，表示当前方法是起始方法
     * @param method 当前方法
     * @return 方法调用链的列表
     */
    private fun getMethodCallChainRecursive(from: PsiMethod?, method: PsiMethod): MutableList<RefChainModel> {
        // 创建一个查询，用于搜索当前方法的所有引用
        val query: Query<PsiReference> = ReferencesSearch.search(method)
//        println("query:::${query.findAll().size}")
        // 创建一个列表，用于存储方法调用链
        val callChain = mutableListOf<RefChainModel>()
        // 遍历查询结果中的每一个引用
        for (psiReference in query) {
            val element = psiReference.element
            // 获取引用元素的父方法
            val parent = PsiTreeUtil.getParentOfType(element, PsiMethod::class.java)

            if (parent != null) {
                // 获取父方法所在的类
                val clazz = parent.containingClass
                val key = "${clazz!!.name}:${parent.name}"
                if(!chainModelMap.containsKey(key) && parent != from) {
                    // 递归获取父方法的方法调用链
                    val refs = getMethodCallChainRecursive(method, parent)
                    // 将父方法及其方法调用链添加到当前方法的方法调用链中
//                    val refs = mutableListOf<RefChainModel>()
//                    if(isSpringController(clazz)){
//                        println("isSpringController:::${clazz.name}:${parent.name}")
//                    }

                    val controller = isSpringController(clazz)
                    var chainModel :RefChainModel
                    if(controller) {
                        val requestPaths = getRequestPaths(parent).firstOrNull().toString()
                        val requestMethod = getRequestMethod(parent)
                        val requestComment = getRequestComment(parent)
                        chainModel = RefChainModel(method,
                            parent,  clazz, refs,
                            module = getModule(clazz),
                            requestMethod = requestMethod,
                            requestUrl = requestPaths,
                            comment = requestComment,
                            isService = isSpringService(clazz),
                            isController = isSpringController(clazz),
                            isAccessor = isSpringAccessor(clazz),
                            isMapper = isSpringMapper(clazz)
                        )
                    }else {
                        chainModel = RefChainModel(
                            method,
                            parent, clazz, refs,
                            isService = isSpringService(clazz),
                            isController = isSpringController(clazz),
                            isAccessor = isSpringAccessor(clazz),
                            isMapper = isSpringMapper(clazz)
                        )
                    }
                    chainModelMap[key] = chainModel
                    callChain.add(chainModel)
                }
            }
        }
        // 返回方法调用链
        return callChain
    }
}
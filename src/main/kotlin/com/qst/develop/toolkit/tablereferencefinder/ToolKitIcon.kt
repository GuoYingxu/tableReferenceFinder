package com.qst.develop.toolkit.tablereferencefinder

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class ToolKitIcon {
        companion object {
            val MOUDLE_ICON = AllIcons.Modules.UnloadedModule
            //        val REFRESH_ICON = AllIcons.Actions.Refresh
//        val SERVICE_ICON = IconLoader.getIcon("/icons/service.png", ToolKitIconsUtil::class.java)
            private val controller = IconLoader.getIcon("/icons/controllerIcon.svg", ToolKitIcon::class.java)
            private val service = IconLoader.getIcon("/icons/serviceIcon.svg", ToolKitIcon::class.java)
            private val mapper = IconLoader.getIcon("/icons/mapperIcon.svg", ToolKitIcon::class.java)
            private val accessor = IconLoader.getIcon("/icons/accessorIcon.svg", ToolKitIcon::class.java)

            fun getIcon(method:String): Icon {
                return when(method) {
                    "controller" -> controller
                    "service" -> service
                    "mapper" -> mapper
                    "accessor" -> accessor
                    else -> MOUDLE_ICON
                }
            }
        }
    }
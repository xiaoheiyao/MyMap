package com.lqz.map

import android.app.Application
import com.lqz.imap.appliction.IAppDelegate
import com.lqz.imap_arcgis10.Arcgis10Delegate

class MapApplication : Application() {

    private val delegates = mutableListOf<IAppDelegate>()

    override fun onCreate() {
        super.onCreate()
        //用到哪个模块就添加哪个模块
        delegates.add(Arcgis10Delegate())

        delegates.forEach {
            it.onCreate(this)
        }
    }


}
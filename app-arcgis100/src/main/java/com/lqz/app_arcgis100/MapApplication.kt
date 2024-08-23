package com.lqz.app_arcgis100

import android.app.Application
import com.lqz.imap.appliction.IAppDelegate
//import com.lqz.imap_arcgis10.Arcgis10Delegate
import com.lqz.imap_arcgis100.Arcgis100Delegate

class MapApplication : Application() {

    private val delegates = mutableListOf<IAppDelegate>()

    override fun onCreate() {
        super.onCreate()
        //用到哪个模块就添加哪个模块
//        delegates.add(Arcgis10Delegate())
        delegates.add(Arcgis100Delegate())

        delegates.forEach {
            it.onCreate(this)
        }
    }


}
package com.lqz.imap_arcgis10

import android.app.Application
import com.esri.android.runtime.ArcGISRuntime
import com.lqz.imap.appliction.IAppDelegate

class Arcgis10Delegate : IAppDelegate {
    override fun onCreate(application: Application) {
        ArcGISRuntime.setClientId("Rr6B3Fkw86DZ71p0") //设置gis的ID 新注册的 Rr6B3Fkw86DZ71p0  //老的 id：u6Cuedp2BQRJBHvW
    }
}
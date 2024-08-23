package com.lqz.imap_arcgis100

import android.app.Application
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.lqz.imap.appliction.IAppDelegate

class Arcgis100Delegate : IAppDelegate {
    override fun onCreate(application: Application) {
        // license with a license key
        //网上找的
//        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud7659408794,none,ZZ0RJAY3FY0GEBZNR002")
//        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166")
        //自己申请的
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud3130458942,none,XXMFA0PL400PPF002010")

    }
}
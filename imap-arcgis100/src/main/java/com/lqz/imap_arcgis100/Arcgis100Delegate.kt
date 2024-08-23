package com.lqz.imap_arcgis100

import android.app.Application
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.lqz.imap.appliction.IAppDelegate

class Arcgis100Delegate : IAppDelegate {
    override fun onCreate(application: Application) {
        // license with a license key
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166")
    }
}
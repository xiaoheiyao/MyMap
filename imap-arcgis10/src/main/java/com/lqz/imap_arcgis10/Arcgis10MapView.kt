package com.lqz.imap_arcgis10

import android.content.Context
import android.os.Bundle
import android.view.View
import com.esri.android.map.MapView
import com.lqz.imap.core.internal.IMapViewDelegate
import com.lqz.imap.core.listener.OnMapReadyCallback

class Arcgis10MapView(context: Context) : IMapViewDelegate {

    private var mMapView: MapView

    init {
        mMapView = MapView(context)
        mMapView.setEsriLogoVisible(false) //设置esri标志不可见
    }

    override fun getMapAsync(callback: OnMapReadyCallback) {

    }

    override fun getContext(): Context {
        TODO("Not yet implemented")
    }

    override fun onCreate(bundle: Bundle) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onStop() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }

    override fun getDelfView(): View {
        TODO("Not yet implemented")
    }
}
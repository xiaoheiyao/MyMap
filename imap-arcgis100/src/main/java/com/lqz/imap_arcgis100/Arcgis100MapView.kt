package com.lqz.imap_arcgis100

import android.content.Context
import android.os.Bundle
import android.view.View
import com.esri.arcgisruntime.mapping.view.MapView
import com.lqz.imap.core.internal.IMapDelegate
import com.lqz.imap.core.internal.IMapViewDelegate

class Arcgis100MapView(context: Context) : IMapViewDelegate {

    private var mMapView: MapView

    init {
        mMapView = MapView(context)
        mMapView.isAttributionTextVisible = false //设置esri标志不可见
    }

    override fun getMapAsync(callback: (IMapDelegate) -> Unit) {
        val wrapper = Arcgis100MapWrapper(mMapView)
        callback(wrapper)
    }

    override fun getContext(): Context {
        return mMapView.context
    }

    override fun onCreate(bundle: Bundle?) {
//        TODO("Not yet implemented")
    }

    override fun onResume() {
        mMapView.resume()
    }

    override fun onPause() {
        mMapView.pause()
    }

    override fun onStart() {
//        TODO("Not yet implemented")
    }

    override fun onStop() {
//        TODO("Not yet implemented")
    }

    override fun onDestroy() {
//        TODO("Not yet implemented")
    }

    override fun getDelfView(): View {
        return mMapView
    }
}
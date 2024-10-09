package com.lqz.imap_arcgis100

import com.esri.arcgisruntime.mapping.view.MapView
import com.lqz.imap.core.internal.IUiSettingsDelegate

class Arcgis100UiSetting(
    mapView: MapView,
    val mapWrapper: Arcgis100MapWrapper
) : IUiSettingsDelegate {


    override fun setZoomControlsEnabled(enable: Boolean) {
//        TODO("Not yet implemented")
    }

    override fun setCompassEnabled(enable: Boolean) {
//        TODO("Not yet implemented")
    }

    override fun setRotateGesturesEnabled(enable: Boolean) {
        mapWrapper.setRotateEnable(enable)
    }

    override fun setScaleControlsEnabled(enable: Boolean) {
//        TODO("Not yet implemented")
    }

    override fun getWidth(): Int {
        return 0
    }

    override fun getHeight(): Int {
        return 0
    }

    override fun getPadding(): IntArray {
        return IntArray(0)
    }
}
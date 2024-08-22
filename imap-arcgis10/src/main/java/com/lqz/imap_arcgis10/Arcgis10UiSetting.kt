package com.lqz.imap_arcgis10

import com.esri.android.map.MapView
import com.lqz.imap.core.internal.IUiSettingsDelegate

class Arcgis10UiSetting(
    mapView: MapView,
    val mapWrapper: Arcgis10MapWrapper
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
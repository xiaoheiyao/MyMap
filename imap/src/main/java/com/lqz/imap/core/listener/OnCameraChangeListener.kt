package com.lqz.imap.core.listener

import com.lqz.imap.model.ILatLng

interface OnCameraChangeListener {
    /**
     * 地图改变
     */
    fun onCameraChanged(latLng: ILatLng, zoom: Float, rotate: Double)

    /**
     * 地图改变完成
     */
    fun onCameraChangedFinish(latLng: ILatLng, zoom: Float, rotate: Double)
}
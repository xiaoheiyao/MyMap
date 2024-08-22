package com.lqz.imap.core.listener

import com.lqz.imap.model.ILatLngBounds

interface OnMapBoundsCallback {
    /**
     * 地图范围就绪
     */
    fun onMapBoundsReady(bounds: ILatLngBounds)
}
package com.lqz.imap.core.listener

import com.lqz.imap.core.internal.IMapDelegate

/**
 * 地图准备就绪回调
 */
interface OnMapReadyCallback {
    /**
     * 地图准备就绪
     */
    fun onMapReady(map: IMapDelegate)
}
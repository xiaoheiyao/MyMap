package com.lqz.imap.core.listener

import android.graphics.Point

interface OnLatLngScreenLocationCallback {
    /**
     * 屏幕中心位置回调
     */
    fun onLatLngScreenLocationReady(point: Point?)
}
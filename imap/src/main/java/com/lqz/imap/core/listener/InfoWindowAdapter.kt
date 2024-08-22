package com.lqz.imap.core.listener

import android.view.View
import com.lqz.imap.core.internal.IMarkerDelegate

/**
 * 信息窗口适配器
 */
interface InfoWindowAdapter {
    /**
     * 获得信息窗口
     */
    fun getInfoWindow(iMarkerDelegate: IMarkerDelegate): View
}
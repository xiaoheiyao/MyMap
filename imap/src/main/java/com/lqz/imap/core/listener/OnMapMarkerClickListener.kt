package com.lqz.imap.core.listener

import com.lqz.imap.core.internal.IMarkerDelegate

interface OnMapMarkerClickListener {
    /**
     * 标记点击事件
     */
    fun onMapMarkerClick(iMarkerDelegate: IMarkerDelegate): Boolean
}
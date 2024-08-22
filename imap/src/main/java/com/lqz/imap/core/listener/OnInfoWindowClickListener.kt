package com.lqz.imap.core.listener

import com.lqz.imap.core.internal.IMarkerDelegate

interface OnInfoWindowClickListener {
    /**
     * 信息窗口回调
     */
    fun onInfoWindowClick(iMarkerDelegate: IMarkerDelegate)
}
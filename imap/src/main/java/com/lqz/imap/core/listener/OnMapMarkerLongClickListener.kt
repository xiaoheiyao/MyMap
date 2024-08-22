package com.lqz.imap.core.listener

import com.lqz.imap.core.internal.IMarkerDelegate

interface OnMapMarkerLongClickListener {
    /**
     * 标记长按事件
     */
    fun onMapMarkerLongClick(iMarkerDelegate: IMarkerDelegate): Boolean
}
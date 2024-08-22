package com.lqz.imap.core.listener

import com.lqz.imap.core.internal.IMarkerDelegate

interface OnMarkerDragListener {

    /**
     * 开始拖动标记
     */
    fun onMarkerDragStart(marker: IMarkerDelegate)

    /**
     * 拖动标记中
     */
    fun onMarkerDrag(marker: IMarkerDelegate)

    /**
     * 结束拖动标记
     */
    fun onMarkerDragEnd(marker: IMarkerDelegate)
}
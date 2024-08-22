package com.lqz.imap.core.listener

import com.lqz.imap.model.ILatLng

interface OnMapLongClickListener {
    /**
     * 长按
     */
    fun onMapLongClick(latLng: ILatLng)
}
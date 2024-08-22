package com.lqz.imap.core.listener

import com.lqz.imap.model.ILatLng

interface OnMapClickListener {
    fun onMapClick(latLng: ILatLng)
}
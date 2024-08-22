package com.lqz.imap.core.listener

import com.lqz.imap.core.internal.IOverlayDelegate

interface OnMapOverlayClickListener {
    fun onMapOverlayClick(iOverlayDelegate: IOverlayDelegate): Boolean
}
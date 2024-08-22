package com.lqz.imap.core.listener

import android.graphics.Bitmap

interface OnSnapshotReadyListener {
    /**
     * 快照准备就绪
     */
    fun onSnapshotReady(bitmap: Bitmap)
}
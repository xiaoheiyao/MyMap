package com.lqz.imap.model

import android.graphics.Bitmap

data class IBitmapDescriptor(
    val bitmap: Bitmap,
) {
    private var width = 0
    private var height = 0
    init {
        width = bitmap.getWidth()
        height = bitmap.getHeight()
    }

//    fun getBitmap(): Bitmap {
//        return bitmap
//    }

    fun getWidth(): Int {
        return width
    }


    fun getHeight(): Int {
        return height
    }

    fun recycle() {
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
    }
}
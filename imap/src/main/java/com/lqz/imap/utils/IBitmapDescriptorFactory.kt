package com.lqz.imap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.lqz.imap.model.IBitmapDescriptor

object IBitmapDescriptorFactory {
    fun fromBitmap(bitmap: Bitmap): IBitmapDescriptor {
        return IBitmapDescriptor(bitmap)
    }

    fun fromView(view: View): IBitmapDescriptor {
        val bitmap: Bitmap = ViewUtils.convertViewToBitmap(view)
        //        bitmap.recycle();
        return IBitmapDescriptor(bitmap)
    }

    fun fromResource(
        context: Context?,
        @DrawableRes resourceId: Int
    ): IBitmapDescriptor {
        val drawable =
            ContextCompat.getDrawable(context!!, resourceId)
        val bitmap: Bitmap
        if (drawable is BitmapDrawable) {
            bitmap = drawable.bitmap
        } else {
            bitmap = if (drawable!!.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                )
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return IBitmapDescriptor(bitmap)
    }
}
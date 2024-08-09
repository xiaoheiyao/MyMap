package com.lqz.imap.core.internal

import android.graphics.Bitmap
import android.view.View
import com.lqz.imap.model.ILatLng

/**
 * 点标记委托
 */
interface IMarkerDelegate : IOverlayDelegate {

    fun setTitle(title: String)

    fun getTitle(): String?

    fun setIcon(bitmap: Bitmap)

    fun setIcon(view: View)

    fun showInfoWindow()

    fun hideInfoWindow()

    fun setDraggable(draggable: Boolean)

    fun isDraggable(): Boolean

    fun getPosition(): ILatLng?

    fun setPosition(latLng: ILatLng)

    fun setRotate(rotate: Float)

    fun getRotate(): Float

    fun setEnable(enable: Boolean)

    fun getEnable(): Boolean

    fun setZIndex(zIndex: Int)

}
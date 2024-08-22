package com.lqz.imap.core.internal

import com.lqz.imap.model.ILatLng

interface ICircleDelegate : IOverlayDelegate {

    fun setRadius(width: Float)

    fun getRadius(): Float

    fun setStrokeWidth(width: Float)

    fun getStrokeWidth(): Float

    fun setFillColor(fillColor: Int)

    fun getFillColor(): Int

    fun setStrokeColor(strokeColor: Int)

    fun getStrokeColor(): Int

    fun setCenter(center: ILatLng)

    fun getCenter(): ILatLng

    fun setDraggable(draggable: Boolean)

    fun isDraggable(): Boolean

}
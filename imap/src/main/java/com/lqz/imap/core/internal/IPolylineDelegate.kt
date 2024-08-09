package com.lqz.imap.core.internal

import com.lqz.imap.model.ILatLng

interface IPolylineDelegate : IOverlayDelegate {

    fun setWidth(width: Float)

    fun getWidth(): Float

    fun setColor(var1: Int)

    fun getColor(): Int

    fun setPoints(latLngs: List<ILatLng>)

    fun getPoints(): List<ILatLng>

    fun setDottedLine(var1: Boolean)

    fun isDottedLine(): Boolean

    fun add(iLatLng: ILatLng)
}
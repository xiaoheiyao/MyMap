package com.lqz.imap.core.internal

import com.lqz.imap.model.ILatLng

interface IPolygonDelegate : IOverlayDelegate {
    fun setStrokeWidth(width: Float)

    fun getStrokeWidth(): Float

    fun setFillColor(fillColor: Int)

    fun getFillColor(): Int

    fun setStrokeColor(strokeColor: Int)

    fun setPoints(var1: List<ILatLng>)

    fun addPoint(latLng: ILatLng)

    fun getPoints(): List<ILatLng>

    fun getStrokeColor(): Int

    /**
     * 包含某个点
     */
    operator fun contains(latLng: ILatLng): Boolean
}
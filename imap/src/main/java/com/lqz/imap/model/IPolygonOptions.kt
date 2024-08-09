package com.lqz.imap.model

import android.graphics.Color

data class IPolygonOptions(
    var points: MutableList<ILatLng> = ArrayList(),
    var strokeWidth: Float = 1f, //笔画宽度
    var fillColor: Int = Color.BLACK,
    var strokeColor: Int = Color.BLACK,
    var zIndex: Float = 0f,
    var visible: Boolean = true,
    var alpha: Float = 1f,
){
    fun add(point: ILatLng?): IPolygonOptions {
        points.add(point!!)
        return this
    }

    fun add(vararg points: ILatLng?): IPolygonOptions {
        for (point in points) {
            add(point)
        }
        return this
    }

    fun addAll(points: Iterable<ILatLng?>): IPolygonOptions {
        for (point in points) {
            add(point)
        }
        return this
    }
}

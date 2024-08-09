package com.lqz.imap.model

import android.graphics.Color

data class IPolylineOptions(
    var points: MutableList<ILatLng> = ArrayList(),
    var width: Float = 1f, //线宽度
    var color: Int = Color.BLACK,
    var zIndex: Float = 0f,
    var isDottedLine: Boolean = false, //是否是虚线
    var visible: Boolean = true,
    var alpha: Float = 1f,
) {
    fun add(point: ILatLng): IPolylineOptions {
        points.add(point)
        return this
    }

    fun add(vararg points: ILatLng): IPolylineOptions {
        for (point in points) {
            add(point)
        }
        return this
    }

    fun addAll(points: Iterable<ILatLng>): IPolylineOptions {
        for (point in points) {
            add(point)
        }
        return this
    }
}

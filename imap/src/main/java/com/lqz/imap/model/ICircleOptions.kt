package com.lqz.imap.model

import android.graphics.Color

data class ICircleOptions(
    var centerPoint: ILatLng = ILatLng(0.0, 0.0),
    var radius: Float, //半径
    var fillColor: Int = Color.BLACK,
    var strokeColor: Int = Color.BLACK,
    var zIndex: Float = 0f,
    var strokeWidth: Float = 1f, //笔画宽度
    var draggable: Boolean = false, //是否可拖动
    var visible: Boolean = true,
    var alpha: Float = 1f,
) {
}
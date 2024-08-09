package com.lqz.imap.model

import androidx.annotation.FloatRange

/**
 * 地图上的标记点选项
 */
data class IMarkerOptions(
    var position: ILatLng = ILatLng(0.0, 0.0),
    var icon: IBitmapDescriptor? = null,
    var draggable: Boolean = false, //是否可拖动，默认不可拖动
    var enable: Boolean = true, //是否可点击，默认可点击
    var zIndex: Float = 0f, //设置在图层中的等级，默认最底层0
    var rotate: Float = 0f, //旋转角度，默认0
    var anchorX: Float = 0.5f, //锚点x
    var anchorY: Float = 0.5f, //锚点y
    var title: String? = null,
    var snippet: String? = null,
    @FloatRange(from = 0.0, to = 1.0) var infoWindowAnchorU: Float = 0.5f,
    @FloatRange(from = 0.0, to = 1.0) var infoWindowAnchorV: Float = 0.0f,
    var visible: Boolean = true,
    var `object`: Any? = null,
    var marketId: String? = null,//0 默认  1是名字选择
) {


}
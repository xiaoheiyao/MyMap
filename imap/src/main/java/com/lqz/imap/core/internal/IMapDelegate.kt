package com.lqz.imap.core.internal

import com.lqz.imap.model.ICameraUpdate
import com.lqz.imap.model.ICameraPosition
import com.lqz.imap.model.ICircleOptions
import com.lqz.imap.model.IMarkerOptions
import com.lqz.imap.model.IPolygonOptions
import com.lqz.imap.model.IPolylineOptions
import com.lqz.imap.model.MapImpType

/**
 * 地图委托
 */
interface IMapDelegate {
    /*拖动模式，触摸*/
    val DRAG_MODE_TOUCH: Int
        get() = 1

    /*拖动模式长按*/
    val DRAG_MODE_LONG_PRESS: Int
        get() = 2

    /*语言 中文*/
    val LANGUAGE_ZH: String
        get() = "zh"

    /**
     * 获取当前地图类型
     */
    fun getMapImpType(): MapImpType

    /**
     * 设置当前地图类型
     */
    fun setMapType(mapType: MapImpType)

    /**
     * 设置语言
     */
    fun setLanguage(language: String)

    /**
     * 设置地图点的拖动方式
     */
    fun setMarkerDragMode(mode: Int)

    /**
     * 获得地图图层设置接口
     */
    fun getUiSettings(): IUiSettingsDelegate

    /**
     * 获得地图图层投影接口
     */
    fun getProjection(): IProjectionDelegate

    /**
     * 获得当前等级
     */
    fun getScalePerPixel(): Double

    /**
     * 获得图层位置
     */
    fun getCameraPosition(): ICameraPosition

    /**
     * 图层更新动画
     */
    fun animateCamera(cameraUpdate: ICameraUpdate)

    /**
     * 图层更新动画，更新时间
     */
    fun animateCamera(cameraUpdate: ICameraUpdate, duration: Long)

    /**
     * 获得最大层级
     */
    fun getMaxZoom(): Float

    /**
     * 获得最小层级
     */
    fun getMinZoom(): Float
    //todo 有时间再研究，暂时不处理！
//    fun setInfoWindowAdapter(adapter: InfoWindowAdapter)

    fun addMarker(options: IMarkerOptions): IMarkerDelegate
    fun addMarkers(optionsList: List<IMarkerOptions>): List<IMarkerDelegate>
    fun addPolyline(options: IPolylineOptions): IPolylineDelegate
    fun addPolylines(optionsList: List<IPolylineOptions>): List<IPolylineDelegate>
    fun addPolygon(options: IPolygonOptions): IPolygonDelegate
    fun addPolygons(optionsList: List<IPolygonOptions>): List<IPolygonDelegate>
    fun addCircle(options: ICircleOptions): ICircleDelegate
    fun addCircles(optionsList: List<ICircleOptions>): List<ICircleDelegate>

}
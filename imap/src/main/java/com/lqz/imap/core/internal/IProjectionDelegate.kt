package com.lqz.imap.core.internal

import android.graphics.PointF
import com.lqz.imap.model.ILatLng
import com.lqz.imap.model.ILatLngBounds

/**
 * 投影接口委托 todo 这个接口需要好好测试下
 */
interface IProjectionDelegate {

    /**
     * 将屏幕点转化为经纬度坐标
     */
    fun fromScreenLocation(point: PointF): ILatLng?

    /**
     * 将经纬度坐标定位到屏幕上的点
     */
    fun toScreenLocation(latLng: ILatLng): PointF

    /**
     * 计算缩放等级
     */
    fun calculateZoom(scale: Float):Double

    /**
     * 获得可见区域
     */
    fun getVisibleRegion(): ILatLngBounds?
}
package com.lqz.imap_arcgis100

import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.Polyline
import com.esri.arcgisruntime.geometry.PolylineBuilder
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.lqz.imap.core.internal.IPolylineDelegate
import com.lqz.imap.model.ILatLng

class Arcgis100Polyline(
    var curPolyline: Graphic,
    val mapWrapper: Arcgis100MapWrapper,
) : IPolylineDelegate {

    private var `object`: Any? = null

    override fun setWidth(width: Float) {
        // 获取当前的 SimpleLineSymbol
        val lineSymbol = curPolyline.symbol as SimpleLineSymbol
        // 更新线宽
        lineSymbol.width = width
        // 设置更新后的符号
        curPolyline.symbol = lineSymbol
    }

    override fun getWidth(): Float {
        val lineSymbol = curPolyline.getSymbol() as SimpleLineSymbol
        return lineSymbol.width
    }

    override fun setColor(var1: Int) {
        // 获取当前的 SimpleLineSymbol
        val lineSymbol = curPolyline.symbol as SimpleLineSymbol
        // 更新颜色
        lineSymbol.color = var1
        // 设置更新后的符号
        curPolyline.symbol = lineSymbol
    }

    override fun getColor(): Int {
        val lineSymbol = curPolyline.symbol as SimpleLineSymbol
        return lineSymbol.color
    }

    override fun setPoints(latLngs: List<ILatLng>) {
        val polylineBuilder = PolylineBuilder(SpatialReferences.getWgs84())
        latLngs.forEach {
            val point = Point(it.longitude, it.latitude)
            polylineBuilder.addPoint(point)
        }

        // 更新线段的几何
        curPolyline.geometry = polylineBuilder.toGeometry()
    }

    override fun getPoints(): List<ILatLng> {
        val pointList: MutableList<ILatLng> = java.util.ArrayList()

        return pointList
    }

    override fun setDottedLine(var1: Boolean) {

    }

    override fun isDottedLine(): Boolean {
        val lineSymbol = curPolyline.symbol as SimpleLineSymbol
        return if (lineSymbol.style == SimpleLineSymbol.Style.DOT) {
            true
        } else {
            false
        }
    }

    /**
     * 添加线段
     */
    override fun add(iLatLng: ILatLng) {

        val point = Point(iLatLng.longitude, iLatLng.latitude)

        // 获取当前线段几何
        val currentPolyline = curPolyline.geometry as Polyline
        // 使用 PolylineBuilder 创建新几何，并添加原有点
        val polylineBuilder = PolylineBuilder(currentPolyline)
        polylineBuilder.addPoint(point) // 添加新点

        // 更新线段的几何
        curPolyline.geometry = polylineBuilder.toGeometry()

    }

    override fun getObject(): Any? {
        return `object`
    }

    override fun setObject(o: Any?) {
        `object` = o
    }

    override fun remove() {
        mapWrapper.getGraphicsOverlay()?.graphics?.remove(curPolyline)
    }

    override fun getId(): String? {
        return null
    }

    override fun setZIndex(var1: Float) {
        TODO("Not yet implemented")
    }

    override fun getZIndex(): Float {
        return curPolyline.zIndex.toFloat()
    }

    override fun setVisible(var1: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isVisible(): Boolean {
        return true
    }
}
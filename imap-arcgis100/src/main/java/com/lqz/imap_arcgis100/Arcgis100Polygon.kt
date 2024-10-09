package com.lqz.imap_arcgis100

import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.Polygon
import com.esri.arcgisruntime.geometry.PolygonBuilder
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.lqz.imap.core.internal.IPolygonDelegate
import com.lqz.imap.model.ILatLng

class Arcgis100Polygon(
    val graphic: Graphic,
    val arcgisMapWrapper: Arcgis100MapWrapper,
) : IPolygonDelegate {

    private var `object`: Any? = null

    override fun setStrokeWidth(width: Float) {
        val fillSymbol = (graphic.symbol as SimpleFillSymbol)
        // 获取当前的 SimpleLineSymbol 并更新线宽
        val lineSymbol = fillSymbol.outline as SimpleLineSymbol
        lineSymbol.width = width
        // 重新应用符号
        graphic.symbol = fillSymbol
    }

    override fun getStrokeWidth(): Float {
        val fillSymbol = graphic.symbol as SimpleFillSymbol
        return fillSymbol.outline.width
    }

    override fun setFillColor(fillColor: Int) {
        val fillSymbol = (graphic.symbol as SimpleFillSymbol)
        // 更新填充颜色
        fillSymbol.color = fillColor
        // 重新应用符号
        graphic.symbol = fillSymbol
    }

    override fun getFillColor(): Int {
        val fillSymbol = graphic.symbol as SimpleFillSymbol
        return fillSymbol.color
    }

    override fun setStrokeColor(strokeColor: Int) {
        val fillSymbol = (graphic.symbol as SimpleFillSymbol)
        // 获取当前的 SimpleLineSymbol 并更新颜色
        val lineSymbol = fillSymbol.outline
        lineSymbol.color = strokeColor
        // 重新应用符号
        graphic.symbol = fillSymbol
    }

    override fun setPoints(var1: List<ILatLng>) {
        // 创建 PolygonBuilder 并添加点
        val polygonBuilder = PolygonBuilder(SpatialReferences.getWgs84())
        var1.forEach {
            val point = Point(it.longitude, it.latitude)
            polygonBuilder.addPoint(point)
        }
        // 更新线段的几何
        graphic.geometry = polygonBuilder.toGeometry()
    }

    override fun addPoint(latLng: ILatLng) {
        var point = Point(latLng.longitude, latLng.latitude)
        // 获取当前面的几何
        val currentPolygon = graphic.geometry

        // 使用 PolygonBuilder 创建新几何，并添加原有点
        val polygonBuilder = PolygonBuilder(currentPolygon as Polygon)

        // 添加新点
        polygonBuilder.addPoint(point)

        // 更新面的几何
        graphic.geometry = polygonBuilder.toGeometry()
    }

    override fun getPoints(): List<ILatLng> {
        TODO("Not yet implemented")
    }

    override fun getStrokeColor(): Int {
        val fillSymbol = graphic.symbol as SimpleFillSymbol
        return fillSymbol.outline.color
    }

    override fun contains(latLng: ILatLng): Boolean {
        TODO("Not yet implemented")
    }

    override fun getObject(): Any? {
        return `object`
    }

    override fun setObject(o: Any?) {
        `object` = o
    }

    override fun remove() {
        arcgisMapWrapper.getGraphicsOverlay()?.graphics?.remove(graphic)
    }

    override fun getId(): String? {
        return null
    }

    override fun setZIndex(var1: Float) {
        TODO("Not yet implemented")
    }

    override fun getZIndex(): Float {
        return graphic.zIndex.toFloat()
    }

    override fun setVisible(var1: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isVisible(): Boolean {
        return true
    }
}
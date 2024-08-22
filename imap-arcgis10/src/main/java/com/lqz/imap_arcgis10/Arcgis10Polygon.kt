package com.lqz.imap_arcgis10

import com.esri.core.geometry.GeometryEngine
import com.esri.core.geometry.Point
import com.esri.core.geometry.Polygon
import com.esri.core.geometry.SpatialReference
import com.esri.core.map.Graphic
import com.esri.core.symbol.SimpleFillSymbol
import com.lqz.imap.core.internal.IPolygonDelegate
import com.lqz.imap.model.ILatLng

class Arcgis10Polygon(
    val id: Int,
    val graphic: Graphic,
    val arcgisMapWrapper: Arcgis10MapWrapper,
) : IPolygonDelegate {

    private var `object`: Any? = null

    private val points: MutableList<ILatLng> = ArrayList()

    init {
        val polygon = graphic.geometry as Polygon
        for (i in 0 until polygon.pointCount) {
            var point = polygon.getPoint(i)
            point = GeometryEngine.project(
                point,
                arcgisMapWrapper.getSpatialReference(),
                SpatialReference.create(SpatialReference.WKID_WGS84)
            ) as Point
            val latLng = ILatLng(point.y, point.x)
            points.add(latLng)
        }
    }

    override fun setStrokeWidth(width: Float) {
        val fillSymbol = graphic.symbol as SimpleFillSymbol
        fillSymbol.outline.setWidth(width)
        arcgisMapWrapper.getGraphicsOverlay()?.updateGraphic(id, fillSymbol)
    }

    override fun getStrokeWidth(): Float {
        val fillSymbol = graphic.symbol as SimpleFillSymbol
        return fillSymbol.outline.width
    }

    override fun setFillColor(fillColor: Int) {
        val fillSymbol = graphic.symbol as SimpleFillSymbol
        fillSymbol.setColor(fillColor)
        arcgisMapWrapper.getGraphicsOverlay()!!.updateGraphic(id, fillSymbol)
    }

    override fun getFillColor(): Int {
        val fillSymbol = graphic.symbol as SimpleFillSymbol
        return fillSymbol.color
    }

    override fun setStrokeColor(strokeColor: Int) {
        val fillSymbol = graphic.symbol as SimpleFillSymbol
        fillSymbol.outline.setColor(strokeColor)
        arcgisMapWrapper.getGraphicsOverlay()!!.updateGraphic(id, fillSymbol)
    }

    override fun setPoints(var1: List<ILatLng>) {
        val pointList: MutableList<Point> = java.util.ArrayList()
        val polygon = Polygon()
        var i = 0
        for ((latitude, longitude) in var1) {
            var point = Point(longitude, latitude)
            point = GeometryEngine.project(
                point,
                SpatialReference.create(SpatialReference.WKID_WGS84),
                arcgisMapWrapper.getSpatialReference()
            ) as Point
            pointList.add(point)
            if (i == 0) {
                polygon.startPath(point)
            } else {
                polygon.lineTo(point)
            }
            i++
        }
        arcgisMapWrapper.getGraphicsOverlay()!!.updateGraphic(id, polygon)
    }

    override fun addPoint(latLng: ILatLng) {
        points.add(latLng)
        var point = Point(latLng.longitude, latLng.latitude)
        point = GeometryEngine.project(
            point,
            SpatialReference.create(SpatialReference.WKID_WGS84),
            arcgisMapWrapper.getSpatialReference()
        ) as Point
        val polygon = graphic.geometry as Polygon
        polygon.lineTo(point)
        arcgisMapWrapper.getGraphicsOverlay()!!.updateGraphic(id, polygon)
    }

    override fun getPoints(): List<ILatLng> {
        return points
    }

    override fun getStrokeColor(): Int {
        val fillSymbol = graphic.symbol as SimpleFillSymbol
        return fillSymbol.outline.color
    }

    override fun contains(latLng: ILatLng): Boolean {
        return points.contains(latLng)
    }

    override fun getObject(): Any? {
        return `object`
    }

    override fun setObject(o: Any?) {
        `object` = o
    }

    override fun remove() {
        arcgisMapWrapper.getGraphicsOverlay()?.removeGraphic(id)
    }

    override fun getId(): String? {
        return null
    }

    override fun setZIndex(var1: Float) {
        TODO("Not yet implemented")
    }

    override fun getZIndex(): Float {
        return graphic.drawOrder.toFloat()
    }

    override fun setVisible(var1: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isVisible(): Boolean {
        return true
    }
}
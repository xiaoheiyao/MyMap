package com.lqz.imap_arcgis10

import com.esri.core.geometry.GeometryEngine
import com.esri.core.geometry.Point
import com.esri.core.geometry.Polygon
import com.esri.core.geometry.SpatialReference
import com.esri.core.map.Graphic
import com.esri.core.symbol.SimpleFillSymbol
import com.esri.core.symbol.SimpleLineSymbol
import com.esri.core.symbol.SimpleMarkerSymbol
import com.lqz.imap.core.internal.ICircleDelegate
import com.lqz.imap.model.ILatLng
import com.lqz.imap.utils.IMapUtils

class Arcgis10Circle(
    val id: Int,
    val mapWrapper: Arcgis10MapWrapper,
    val graphic: Graphic,
    val centerLatLng: ILatLng,
    var radiusMeter: Float,
) : ICircleDelegate {

    var centerPoint: Point = GeometryEngine.project(
        Point(centerLatLng.longitude, centerLatLng.latitude),
        SpatialReference.create(SpatialReference.WKID_WGS84),
        mapWrapper.getSpatialReference()
    ) as Point

    private var `object`: Any? = null

    private var draggable = false

    override fun setRadius(width: Float) {
        radiusMeter = width
        val centerLatlng = getCenter()
        val pointList: MutableList<Point> = ArrayList()
        val polygon = Polygon()
        for (i in 0..360 step 2) {
            val (latitude, longitude) = IMapUtils.convertDistanceToLogLat(
                centerLatlng,
                radiusMeter.toDouble(), i.toDouble()
            )
            var point = Point(
                longitude,
                latitude
            )
            point = GeometryEngine.project(
                point,
                SpatialReference.create(SpatialReference.WKID_WGS84),
                mapWrapper.getSpatialReference()
            ) as Point
            pointList.add(point)
            if (i == 0) {
                polygon.startPath(point)
            } else {
                polygon.lineTo(point)
            }
        }
        mapWrapper.getGraphicsOverlay()!!.updateGraphic(id, polygon)
    }

    override fun getRadius(): Float {
        return radiusMeter
    }

    override fun setStrokeWidth(width: Float) {
        val simpleLineSymbol = (graphic.symbol as SimpleFillSymbol).outline as SimpleLineSymbol
        simpleLineSymbol.setWidth(width)
        mapWrapper.getGraphicsOverlay()!!.updateGraphic(id, graphic.symbol)
    }

    override fun getStrokeWidth(): Float {
        val simpleLineSymbol = (graphic.symbol as SimpleFillSymbol).outline as SimpleLineSymbol
        return simpleLineSymbol.width
    }

    override fun setFillColor(fillColor: Int) {
        val symbol = graphic.symbol as SimpleFillSymbol
        symbol.setColor(fillColor)
        mapWrapper.getGraphicsOverlay()!!.updateGraphic(id, symbol)
    }

    override fun getFillColor(): Int {
        val symbol = graphic.symbol as SimpleMarkerSymbol
        return symbol.color
    }

    override fun setStrokeColor(strokeColor: Int) {
        val simpleLineSymbol = (graphic.symbol as SimpleMarkerSymbol).outline
        simpleLineSymbol.setColor(strokeColor)
        mapWrapper.getGraphicsOverlay()!!.updateGraphic(id, graphic.symbol)
    }

    override fun getStrokeColor(): Int {
        val simpleLineSymbol = (graphic.symbol as SimpleMarkerSymbol).outline
        return simpleLineSymbol.color
    }

    override fun setCenter(center: ILatLng) {
        val pointList: MutableList<Point> = java.util.ArrayList()
        val polygon = Polygon()
        for (i in 0..360) {
            val (latitude, longitude) = IMapUtils.convertDistanceToLogLat(
                center, radiusMeter.toDouble(),
                i.toDouble()
            )
            var point = Point(longitude, latitude)
            point = GeometryEngine.project(
                point,
                SpatialReference.create(SpatialReference.WKID_WGS84),
                mapWrapper.getSpatialReference()
            ) as Point
            pointList.add(point)
            if (i == 0) {
                polygon.startPath(point)
            } else {
                polygon.lineTo(point)
            }
        }
        centerPoint = GeometryEngine.project(
            Point(center.longitude, center.latitude),
            SpatialReference.create(SpatialReference.WKID_WGS84),
            mapWrapper.getSpatialReference()
        ) as Point
        mapWrapper.getGraphicsOverlay()!!.updateGraphic(id, polygon)
    }

    override fun getCenter(): ILatLng {
        val point = GeometryEngine.project(
            centerPoint,
            mapWrapper.getSpatialReference(),
            SpatialReference.create(SpatialReference.WKID_WGS84)
        ) as Point
        return ILatLng(point.y, point.x)
    }

    override fun setDraggable(draggable: Boolean) {
        this.draggable = draggable
    }

    override fun isDraggable(): Boolean {
        return draggable
    }

    override fun getObject(): Any? {
        return `object`
    }

    override fun setObject(o: Any?) {
        `object` = o
    }

    override fun remove() {
        mapWrapper.getCircleHashMap().remove(graphic)
        if (mapWrapper.getGraphicsOverlay() != null) {
            mapWrapper.getGraphicsOverlay()!!.removeGraphic(id)
        }
    }

    override fun getId(): String? {
        return graphic.toString()
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
        TODO("Not yet implemented")
    }
}
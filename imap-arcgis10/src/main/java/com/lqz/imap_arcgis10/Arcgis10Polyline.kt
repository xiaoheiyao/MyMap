package com.lqz.imap_arcgis10

import android.util.Log
import com.esri.core.geometry.GeometryEngine
import com.esri.core.geometry.Point
import com.esri.core.geometry.Polyline
import com.esri.core.geometry.SpatialReference
import com.esri.core.map.Graphic
import com.esri.core.symbol.SimpleLineSymbol
import com.lqz.imap.core.internal.IPolylineDelegate
import com.lqz.imap.model.ILatLng

class Arcgis10Polyline(
    var id: Int,
    var curPolyline: Graphic,
    val mapWrapper: Arcgis10MapWrapper,
) : IPolylineDelegate {


    private var `object`: Any? = null

    private val polylineList = HashMap<Int, Graphic>()

    private var addTime: Long = 0

    init {
        polylineList[id] = curPolyline
    }

    override fun setWidth(width: Float) {
        for ((key, value) in polylineList) {
            val lineSymbol = value.symbol as SimpleLineSymbol
            lineSymbol.setWidth(width)
            mapWrapper.getGraphicsOverlay()!!.updateGraphic(key, lineSymbol)
        }
    }

    override fun getWidth(): Float {
        val lineSymbol = curPolyline.getSymbol() as SimpleLineSymbol
        return lineSymbol.width
    }

    override fun setColor(var1: Int) {
        for ((key, value) in polylineList) {
            val lineSymbol = value.symbol as SimpleLineSymbol
            lineSymbol.setColor(var1)
            mapWrapper.getGraphicsOverlay()!!.updateGraphic(key, lineSymbol)
        }
    }

    override fun getColor(): Int {
        val lineSymbol = curPolyline.symbol as SimpleLineSymbol
        return lineSymbol.color
    }

    override fun setPoints(latLngs: List<ILatLng>) {
        val pointList: MutableList<Point> = ArrayList()
        val polyline = Polyline()
        var i = 0
        for ((latitude, longitude) in latLngs) {
            var point = Point(longitude, latitude)
            point = GeometryEngine.project(
                point,
                SpatialReference.create(SpatialReference.WKID_WGS84),
                mapWrapper.getSpatialReference()
            ) as Point
            pointList.add(point)
            if (i == 0) {
                polyline.startPath(point)
            } else {
                polyline.lineTo(point)
            }
            i++
        }
        mapWrapper.getGraphicsOverlay()!!.updateGraphic(id, polyline)
    }

    override fun getPoints(): List<ILatLng> {
        val pointList: MutableList<ILatLng> = java.util.ArrayList()
        for ((_, value) in polylineList) {
            val polyline = value.geometry as Polyline
            for (i in 0 until polyline.pointCount) {
                var point = polyline.getPoint(i)
                point = GeometryEngine.project(
                    point,
                    mapWrapper.getSpatialReference(),
                    SpatialReference.create(SpatialReference.WKID_WGS84)
                ) as Point
                val latLng = ILatLng(point.y, point.x)
                pointList.add(latLng)
            }
        }
        return pointList
    }

    override fun setDottedLine(var1: Boolean) {
        for ((key, value) in polylineList) {
            val lineSymbol = value.symbol as SimpleLineSymbol
            if (var1) {
                lineSymbol.setStyle(SimpleLineSymbol.STYLE.DOT)
            } else {
                lineSymbol.setStyle(SimpleLineSymbol.STYLE.SOLID)
            }
            mapWrapper.getGraphicsOverlay()!!.updateGraphic(key, lineSymbol)
        }
    }

    override fun isDottedLine(): Boolean {
        val lineSymbol = curPolyline.symbol as SimpleLineSymbol
        return if (lineSymbol.style == SimpleLineSymbol.STYLE.DOT) {
            true
        } else {
            false
        }
    }

    override fun add(iLatLng: ILatLng) {
        val startTime = System.currentTimeMillis()
        var point = Point(iLatLng.longitude, iLatLng.latitude)
        point = GeometryEngine.project(
            point,
            SpatialReference.create(SpatialReference.WKID_WGS84),
            mapWrapper.getSpatialReference()
        ) as Point
        val polyline = curPolyline.geometry as Polyline
        if (addTime < 90) {
            polyline.lineTo(point)
            mapWrapper.getGraphicsOverlay()!!.updateGraphic(id, polyline)
            Log.d("drawPolyline", "$this add point addTime=$addTime")
        } else {
            val newPolyline = Polyline()
            if (polyline.pointCount > 0) {
                newPolyline.startPath(polyline.getPoint(polyline.pointCount - 1))
            }
            newPolyline.lineTo(point)
            val lineSymbol = SimpleLineSymbol(getColor(), getWidth())
            if (isDottedLine()) {
                lineSymbol.setStyle(SimpleLineSymbol.STYLE.DOT)
            } else {
                lineSymbol.setStyle(SimpleLineSymbol.STYLE.SOLID)
            }
            val newGraphic = Graphic(newPolyline, lineSymbol, getZIndex().toInt())
            id = mapWrapper.getGraphicsOverlay()!!.addGraphic(newGraphic)
            polylineList[id] = newGraphic
            curPolyline = newGraphic
            Log.d("drawPolyline", "$this new polyline addTime=$addTime")
        }
        addTime = System.currentTimeMillis() - startTime
    }

    override fun getObject(): Any? {
        return `object`
    }

    override fun setObject(o: Any) {
        `object` = o
    }

    override fun remove() {
        for ((key) in polylineList) {
            mapWrapper.getGraphicsOverlay()!!.removeGraphic(key)
        }
    }

    override fun getId(): String? {
        return null
    }

    override fun setZIndex(var1: Float) {
        TODO("Not yet implemented")
    }

    override fun getZIndex(): Float {
        return curPolyline.drawOrder.toFloat()
    }

    override fun setVisible(var1: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isVisible(): Boolean {
        return true
    }
}
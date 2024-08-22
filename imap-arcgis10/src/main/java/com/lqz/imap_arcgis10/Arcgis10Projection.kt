package com.lqz.imap_arcgis10

import android.graphics.PointF
import com.esri.android.map.MapView
import com.esri.android.map.TiledServiceLayer
import com.esri.core.geometry.Envelope
import com.esri.core.geometry.GeometryEngine
import com.esri.core.geometry.Point
import com.esri.core.geometry.SpatialReference
import com.lqz.imap.core.internal.IProjectionDelegate
import com.lqz.imap.model.ILatLng
import com.lqz.imap.model.ILatLngBounds
import kotlin.math.ln

class Arcgis10Projection(
    private val mapView: MapView,
    private val mapWrapper: Arcgis10MapWrapper
) : IProjectionDelegate {

    override fun fromScreenLocation(point: PointF): ILatLng? {
        val point1: Point = mapView.toMapPoint(Point(point.x.toDouble(), point.y.toDouble()))
        try {
            val point2 = GeometryEngine.project(
                point1,
                mapWrapper.getSpatialReference(),
                SpatialReference.create(SpatialReference.WKID_WGS84)
            ) as Point
            return ILatLng(point2.y, point2.x)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun toScreenLocation(latLng: ILatLng): PointF {
        val pointF = PointF()
        val mapPoint = GeometryEngine.project(
            Point(latLng.longitude, latLng.latitude),
            SpatialReference.create(SpatialReference.WKID_WGS84),
            mapWrapper.getSpatialReference()
        ) as Point
        val point = mapView.toScreenPoint(mapPoint)
        pointF.x = point.x.toFloat()
        pointF.y = point.y.toFloat()
        return pointF
    }

    override fun calculateZoom(scale: Float): Double {
        val tileInfo: TiledServiceLayer.TileInfo = mapWrapper.getBaseTiledLayer()!!.getTileInfo()
        return run {
            val scales = tileInfo.scales
            ln(scale / scales[0]) / ln(2.0)
        }
    }

    override fun getVisibleRegion(): ILatLngBounds? {
        val extent = mapView.extent
        val envelope = Envelope()
        extent.queryEnvelope(envelope)
        var leftTopLatLng: ILatLng? = null
        var rightBottomLatLng: ILatLng? = null
        val point1 = GeometryEngine.project(
            Point(envelope.xMin, envelope.yMin),
            mapWrapper.getSpatialReference(),
            SpatialReference.create(SpatialReference.WKID_WGS84)
        ) as Point
        if (!point1.isEmpty) {
            leftTopLatLng = ILatLng(point1.y, point1.x)
        }
        val point2 = GeometryEngine.project(
            Point(envelope.xMax, envelope.yMax),
            mapWrapper.getSpatialReference(),
            SpatialReference.create(SpatialReference.WKID_WGS84)
        ) as Point
        if (!point2.isEmpty) {
            rightBottomLatLng = ILatLng(point2.y, point2.x)
        }

        if (leftTopLatLng != null && rightBottomLatLng != null) {
            val builder: ILatLngBounds.Builder = ILatLngBounds.Builder()
            builder.include(leftTopLatLng)
            builder.include(rightBottomLatLng)
            return builder.build()
        }
        return null
    }
}
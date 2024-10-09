package com.lqz.imap_arcgis100

import android.graphics.PointF
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.lqz.imap.core.internal.IProjectionDelegate
import com.lqz.imap.model.ILatLng
import com.lqz.imap.model.ILatLngBounds
import kotlin.math.ln

class Arcgis100Projection(
    private val mapView: MapView,
    private val mapWrapper: Arcgis100MapWrapper
) : IProjectionDelegate {

    override fun fromScreenLocation(point: PointF): ILatLng? {
        val mapPoint: Point =
            mapView.screenToLocation(android.graphics.Point(point.x.toInt(), point.y.toInt()))
        try {
            val projectedPoint = Point(mapPoint.x, mapPoint.y, SpatialReferences.getWgs84())

            return ILatLng(projectedPoint.y, projectedPoint.x)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun toScreenLocation(latLng: ILatLng): PointF {
        val mapPoint = Point(latLng.longitude, latLng.latitude, SpatialReferences.getWgs84())

        mapWrapper.getSpatialReference()

        val screenPoint = mapView.locationToScreen(mapPoint)
        return PointF(screenPoint.x.toFloat(), screenPoint.y.toFloat())
    }

    override fun calculateZoom(scale: Float): Double {
        val tileInfo = (mapWrapper.getBaseTiledLayer() as ArcGISTiledLayer).tileInfo
        return run {
            val lods = tileInfo.levelsOfDetail  // 获取所有 LODs
            val scales = lods.map { it.scale }  // 从 LODs 中提取缩放级别
            ln(scale / scales[0]) / ln(2.0)  // 计算缩放比例
        }
    }

    override fun getVisibleRegion(): ILatLngBounds? {
        val visibleArea = mapView.visibleArea?.extent ?: return null
        val envelope = visibleArea as Envelope

        var leftTopLatLng: ILatLng? = null
        var rightBottomLatLng: ILatLng? = null

        // 将左下角的坐标投影为 WGS84
        val bottomLeftPoint =
            Point(envelope.xMin, envelope.yMin, SpatialReferences.getWgs84())


        if (!bottomLeftPoint.isEmpty) {
            leftTopLatLng = ILatLng(bottomLeftPoint.y, bottomLeftPoint.x)
        }

        // 将右上角的坐标投影为 WGS84
        val topRightPoint = Point(
            envelope.xMin, envelope.yMin,
            SpatialReferences.getWgs84()
        )
        if (!topRightPoint.isEmpty) {
            rightBottomLatLng = ILatLng(topRightPoint.y, topRightPoint.x)
        }

        if (leftTopLatLng != null && rightBottomLatLng != null) {
            val builder = ILatLngBounds.Builder()
            builder.include(leftTopLatLng)
            builder.include(rightBottomLatLng)
            return builder.build()
        }
        return null
    }
}
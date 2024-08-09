package com.lqz.imap.model

import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import com.lqz.imap.core.internal.IMapDelegate
import com.lqz.imap.core.internal.IProjectionDelegate
import com.lqz.imap.core.internal.IUiSettingsDelegate
import kotlin.math.max
import kotlin.math.min

object ICameraUpdateFactory {
    /**
     * Returns a ICameraUpdate that moves the camera to a specified CameraPosition.
     *
     * @param cameraPosition Camera Position to change to
     * @return ICameraUpdate Final Camera Position
     */
    fun newCameraPosition(cameraPosition: ICameraPosition): ICameraUpdate {
        return CameraPositionUpdate(
            cameraPosition.bearing,
            cameraPosition.target,
            cameraPosition.tilt,
            cameraPosition.zoom
        )
    }

    /**
     * Returns a ICameraUpdate that moves the center of the screen to a latitude and longitude
     * specified by a LatLng object. This centers the camera on the LatLng object.
     *
     * @param latLng Target location to change to
     * @return ICameraUpdate Final Camera Position
     */
    fun newLatLng(latLng: ILatLng): ICameraUpdate {
        return CameraPositionUpdate(-1.0, latLng, -1.0, -1.0)
    }

    /**
     * Returns a ICameraUpdate that transforms the camera such that the specified latitude/longitude
     * bounds are centered on screen at the greatest possible zoom level.
     * You can specify padding, in order to inset the bounding box from the map view's edges.
     * The returned ICameraUpdate has a bearing of 0 and a tilt of 0.
     *
     * @param bounds  Bounds to match Camera position with
     * @param padding Padding added to the bounds
     * @return ICameraUpdate Final Camera Position
     */
    fun newLatLngBounds(bounds: ILatLngBounds, padding: Int): ICameraUpdate {
        return newLatLngBounds(bounds, padding, padding, padding, padding)
    }

    /**
     * Returns a ICameraUpdate that transforms the camera such that the specified latitude/longitude
     * bounds are centered on screen at the greatest possible zoom level.
     * You can specify padding, in order to inset the bounding box from the map view's edges.
     * The returned ICameraUpdate has a bearing of 0 and a tilt of 0.
     *
     * @param bounds        Bounds to base the Camera position out of
     * @param paddingLeft   Padding left of the bounds
     * @param paddingTop    Padding top of the bounds
     * @param paddingRight  Padding right of the bounds
     * @param paddingBottom Padding bottom of the bounds
     * @return ICameraUpdate Final Camera Position
     */
    fun newLatLngBounds(
        bounds: ILatLngBounds,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int
    ): ICameraUpdate {
        return CameraBoundsUpdate(bounds, paddingLeft, paddingTop, paddingRight, paddingBottom)
    }

    /**
     * Returns a ICameraUpdate that moves the center of the screen to a latitude and longitude specified by a LatLng object,
     * and moves to the given zoom level.
     *
     * @param latLng Target location to change to
     * @param zoom   Zoom level to change to
     * @return ICameraUpdate Final Camera Position
     */
    fun newLatLngZoom(latLng: ILatLng, zoom: Float): ICameraUpdate {
        return CameraPositionUpdate(-1.0, latLng, -1.0, zoom.toDouble())
    }

    /**
     * Returns a ICameraUpdate that scrolls the camera over the map,
     * shifting the center of view by the specified number of pixels in the x and y directions.
     *
     * @param xPixel Amount of pixels to scroll to in x direction
     * @param yPixel Amount of pixels to scroll to in y direction
     * @return ICameraUpdate Final Camera Position
     */
    fun scrollBy(xPixel: Float, yPixel: Float): ICameraUpdate {
        return CameraMoveUpdate(xPixel, yPixel)
    }

    /**
     * Returns a ICameraUpdate that shifts the zoom level of the current camera viewpoint.
     *
     * @param amount Amount of zoom level to change with
     * @param focus  Focus point of zoom
     * @return ICameraUpdate Final Camera Position
     */
    fun zoomBy(amount: Float, focus: Point): ICameraUpdate {
        return ZoomUpdate(zoom = amount, x = focus.x.toFloat(), y = focus.y.toFloat())
    }

    /**
     * Returns a ICameraUpdate that shifts the zoom level of the current camera viewpoint.
     *
     * @param amount Amount of zoom level to change with
     * @return ICameraUpdate Final Camera Position
     */
    fun zoomBy(amount: Float): ICameraUpdate {
        return ZoomUpdate(ZoomUpdate.ZOOM_BY, amount)
    }

    /**
     * Returns a ICameraUpdate that zooms in on the map by moving the viewpoint's height closer to the Earth's surface. The zoom increment is 1.0.
     *
     * @return ICameraUpdate Final Camera Position
     */
    fun zoomIn(): ICameraUpdate {
        return ZoomUpdate(ZoomUpdate.ZOOM_IN)
    }

    /**
     * Returns a ICameraUpdate that zooms out on the map by moving the viewpoint's height farther away from the Earth's surface. The zoom increment is -1.0.
     *
     * @return ICameraUpdate Final Camera Position
     */
    fun zoomOut(): ICameraUpdate {
        return ZoomUpdate(ZoomUpdate.ZOOM_OUT)
    }

    /**
     * Returns a ICameraUpdate that moves the camera viewpoint to a particular zoom level.
     *
     * @param zoom Zoom level to change to
     * @return ICameraUpdate Final Camera Position
     */
    fun zoomTo(zoom: Float): ICameraUpdate {
        return ZoomUpdate(ZoomUpdate.ZOOM_TO, zoom)
    }

    fun rotateChange(center: ILatLng, rotateAngle: Double): ICameraUpdate {
        return RotateUpdate(center, rotateAngle)
    }


}

data class CameraPositionUpdate(
    val bearing: Double,
    val target: ILatLng?,
    val tilt: Double,
    val zoom: Double
) : ICameraUpdate {

    override fun getCameraPosition(iMap: IMapDelegate): ICameraPosition {
        val previousPosition: ICameraPosition = iMap.getCameraPosition()
        return if (target == null) {
            ICameraPosition.Builder(true)
                .tilt(tilt)
                .zoom(zoom)
                .bearing(bearing)
                .target(previousPosition.target)
                .build()
        } else ICameraPosition.Builder(previousPosition).build()
    }
}

data class CameraBoundsUpdate(
    var bounds: ILatLngBounds,
    val paddingLeft: Int = 0,
    val paddingTop: Int = 0,
    val paddingRight: Int = 0,
    val paddingBottom: Int = 0,
    var paddingRectF: RectF? = null,
) : ICameraUpdate {

    init {
        if (paddingRectF == null) {
            paddingRectF = RectF(
                paddingLeft.toFloat(),
                paddingTop.toFloat(), paddingRight.toFloat(), paddingBottom.toFloat()
            )
        }
    }

    override fun getCameraPosition(iMap: IMapDelegate): ICameraPosition {
        // Get required objects

        // Get required objects
        val projection: IProjectionDelegate = iMap.getProjection()
        val uiSettings: IUiSettingsDelegate = iMap.getUiSettings()

        // calculate correct padding

        // calculate correct padding
        val mapPadding: IntArray = iMap.getUiSettings().getPadding()
        val latLngPadding: RectF = paddingRectF!!
        val padding = RectF(
            latLngPadding.left + mapPadding[0],
            latLngPadding.top + mapPadding[1],
            latLngPadding.right + mapPadding[2],
            latLngPadding.bottom + mapPadding[3]
        )

        // Calculate the bounds of the possibly rotated shape with respect to the viewport

        // Calculate the bounds of the possibly rotated shape with respect to the viewport
        val nePixel = PointF(-Float.MAX_VALUE, -Float.MAX_VALUE)
        val swPixel = PointF(Float.MAX_VALUE, Float.MAX_VALUE)
        val viewportHeight: Float = uiSettings.getHeight().toFloat()
        for (latLng in bounds.toLatLngs()) {
            val pixel: PointF = projection.toScreenLocation(latLng)
            swPixel.x = min(swPixel.x.toDouble(), pixel.x.toDouble()).toFloat()
            nePixel.x = max(nePixel.x.toDouble(), pixel.x.toDouble()).toFloat()
            swPixel.y = min(swPixel.y.toDouble(), (viewportHeight - pixel.y).toDouble()).toFloat()
            nePixel.y = max(nePixel.y.toDouble(), (viewportHeight - pixel.y).toDouble()).toFloat()
        }

        // Calculate width/height

        // Calculate width/height
        val width = nePixel.x - swPixel.x
        val height = nePixel.y - swPixel.y

        var zoom = 0.0
        var minScale = 1f
        // Calculate the zoom level
        // Calculate the zoom level
//        if (padding != null) {
//            val scaleX: Float = (uiSettings.getWidth() - padding.left - padding.right) / width
//            val scaleY: Float = (uiSettings.getHeight() - padding.top - padding.bottom) / height
//            minScale = if (scaleX < scaleY) scaleX else scaleY
//            zoom = projection.calculateZoom(minScale)
//            zoom = MathUtils.clamp(
//                zoom,
//                mapboxMap.getMinZoom() as Float,
//                mapboxMap.getMaxZoom() as Float
//            )
//        }

        // Calculate the center point

        // Calculate the center point
        val paddedNEPixel =
            PointF(nePixel.x + padding.right / minScale, nePixel.y + padding.top / minScale)
        val paddedSWPixel =
            PointF(swPixel.x - padding.left / minScale, swPixel.y - padding.bottom / minScale)
        val centerPixel =
            PointF((paddedNEPixel.x + paddedSWPixel.x) / 2, (paddedNEPixel.y + paddedSWPixel.y) / 2)
        centerPixel.y = viewportHeight - centerPixel.y
        val center: ILatLng = projection.fromScreenLocation(centerPixel)

        return ICameraPosition.Builder()
            .target(center)
            .zoom(zoom)
            .tilt(0.0)
            .bearing(0.0)
            .build()
    }
}

data class CameraMoveUpdate(
    val x: Float,
    val y: Float,
) : ICameraUpdate {

    override fun getCameraPosition(iMap: IMapDelegate): ICameraPosition {
        val uiSettings: IUiSettingsDelegate = iMap.getUiSettings()
        val projection: IProjectionDelegate = iMap.getProjection()

        // Calculate the new center point

        // Calculate the new center point
        val viewPortWidth = uiSettings.getWidth().toFloat()
        val viewPortHeight = uiSettings.getHeight().toFloat()
        val targetPoint = PointF(viewPortWidth / 2 + x, viewPortHeight / 2 + y)

        // Convert point to LatLng

        // Convert point to LatLng
        val latLng = projection.fromScreenLocation(targetPoint)

        val (target, zoom, tilt, bearing) = iMap.getCameraPosition()
        return if (latLng != null) {
            ICameraPosition.Builder()
                .target(latLng)
                .zoom(zoom)
                .tilt(tilt)
                .bearing(bearing)
                .build()
        } else {
            ICameraPosition.Builder(true)
                .tilt(tilt)
                .zoom(zoom)
                .bearing(bearing)
                .target(target)
                .build()
        }
    }
}

data class RotateUpdate(
    val center: ILatLng,
    val rotate: Double,
) : ICameraUpdate {
    override fun getCameraPosition(iMap: IMapDelegate): ICameraPosition {
        return ICameraPosition.Builder()
            .target(center)
            .zoom(0.0)
            .tilt(0.0)
            .bearing(0.0)
            .build()
    }

}

data class ZoomUpdate(
    var type: Int = ZOOM_TO_POINT,
    var zoom: Float = 0f,
    var x: Float = 0f,
    var y: Float = 0f,

    ) : ICameraUpdate {

    companion object {
        val ZOOM_IN = 0
        val ZOOM_OUT = 1
        val ZOOM_BY = 2
        val ZOOM_TO = 3
        val ZOOM_TO_POINT = 4
    }

    fun transformZoom(cz: Double): Double {
        var currentZoom = cz
        when (type) {
            ZOOM_IN -> currentZoom++
            ZOOM_OUT -> {
                currentZoom--
                if (currentZoom < 0) {
                    currentZoom = 0.0
                }
            }

            ZOOM_TO -> currentZoom = zoom.toDouble()
            ZOOM_BY -> currentZoom = currentZoom + zoom.toDouble()
            ZOOM_TO_POINT -> currentZoom = currentZoom + zoom.toDouble()
        }
        return currentZoom
    }

    override fun getCameraPosition(iMap: IMapDelegate): ICameraPosition {
        val cameraPosition: ICameraPosition = iMap.getCameraPosition()
        return if (type != ZOOM_TO_POINT) {
            ICameraPosition.Builder(cameraPosition)
                .zoom(transformZoom(cameraPosition.zoom))
                .build()
        } else {
            ICameraPosition.Builder(cameraPosition)
                .zoom(transformZoom(cameraPosition.zoom))
                .target(iMap.getProjection().fromScreenLocation(PointF(x, y)))
                .build()
        }
    }

}
package com.lqz.app_arcgis100.feature

import com.lqz.imap.model.ILatLng
import com.lqz.imap.utils.IMapUtils
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object MercatorProjection {
    const val DEGREES_PER_RADIANS = 180.0 / Math.PI
    const val RADIANS_PER_DEGREES = Math.PI / 180.0
    const val PI_OVER_2 = Math.PI / 2.0
    const val RADIUS = 6378137.0
    const val RADIUS_2 = RADIUS * 0.5
    const val RAD_RAD = RADIANS_PER_DEGREES * RADIUS


    fun toMercatorPoint(iLatLng: ILatLng): MercatorPoint {
        val mercatorPoint = MercatorPoint()
        mercatorPoint.x = longitudeToX(iLatLng.longitude)
        mercatorPoint.y = latitudeToY(iLatLng.latitude)
        return mercatorPoint
    }

    fun toLatLng(mercatorPoint: MercatorPoint): ILatLng {
        return ILatLng(yToLatitude(mercatorPoint.y), xToLongitude(mercatorPoint.x))
    }

    /**
     * Convert geo lat to vertical distance in meters.
     *
     * @param latitude the latitude in decimal degrees.
     * @return the vertical distance in meters.
     */
    fun latitudeToY(latitude: Double): Double {
        val rad = latitude * RADIANS_PER_DEGREES
        val sin = sin(rad)
        return RADIUS_2 * ln((1.0 + sin) / (1.0 - sin))
    }

    /**
     * Convert geo lon to horizontal distance in meters.
     *
     * @param longitude the longitude in decimal degrees.
     * @return the horizontal distance in meters.
     */
    fun longitudeToX(longitude: Double): Double {
        return longitude * RAD_RAD
    }

    /**
     * Convert horizontal distance in meters to longitude in decimal degress.
     *
     * @param x the horizontal distance in meters.
     * @return the longitude in decimal degrees.
     */
    fun xToLongitude(x: Double): Double {
        return xToLongitude(x, true)
    }

    /**
     * Convert horizontal distance in meters to longitude in decimal degress.
     *
     * @param x      the horizontal distance in meters.
     * @param linear if using continuous pan.
     * @return the longitude in decimal degrees.
     */
    fun xToLongitude(
        x: Double,
        linear: Boolean
    ): Double {
        val rad = x / RADIUS
        val deg = rad * DEGREES_PER_RADIANS
        if (linear) {
            return deg
        }
        val rotations = floor((deg + 180.0) / 360.0)
        return deg - rotations * 360.0
    }

    /**
     * Convert vertical distance in meters to latitude in decimal degress.
     *
     * @param y the vertical distance in meters.
     * @return the latitude in decimal degrees.
     */
    fun yToLatitude(y: Double): Double {
        val rad = PI_OVER_2 - 2.0 * atan(exp(-1.0 * y / RADIUS))
        return rad * DEGREES_PER_RADIANS
    }

    fun getMercatorDistance(latLngList: List<ILatLng>, distance: Double): Double {
        var totolDistance = 0.0
        var denominator = 0
        for (i in latLngList.indices) {
            var j = i + 1
            if (j == latLngList.size) {
                j = 0
            }
            val point1 = latLngList[i]
            val point2 = latLngList[j]
            val twoPointRealDistance = IMapUtils.calculateLineDistance(
                ILatLng(point1.latitude, point1.longitude),
                ILatLng(point2.latitude, point2.longitude)
            )
            val twoPointMeratorDistance = getTwoPointMeratorDistance(point1, point2)
            if (twoPointMeratorDistance == 0.0) {
                continue
            } else {
                val meratorDistance = distance * twoPointMeratorDistance / twoPointRealDistance
                totolDistance = totolDistance + meratorDistance
                denominator += 1
            }
        }
        return totolDistance / denominator
    }

    fun getRawDistance(latLngList: List<ILatLng>, meratorDistance: Double): Double {
        var scale = 0.0
        var size = 0
        for (i in latLngList.indices) {
            var j = i + 1
            if (j == latLngList.size) {
                j = 0
            }
            val point1 = latLngList[i]
            val point2 = latLngList[j]
            val twoPointRealDistance = IMapUtils.calculateLineDistance(
                ILatLng(point1.latitude, point1.longitude),
                ILatLng(point2.latitude, point2.longitude)
            )
            val twoPointMeratorDistance = getTwoPointMeratorDistance(point1, point2)
            if (twoPointRealDistance == 0.0) {
                continue
            } else {
                scale += twoPointMeratorDistance / twoPointRealDistance
                size++
            }
        }
        val totolDistance = meratorDistance * size
        return totolDistance / scale
    }

    private fun getTwoPointMeratorDistance(point1: ILatLng, point2: ILatLng): Double {
        val x1 = longitudeToX(point1.longitude)
        val y1 = latitudeToY(point1.latitude)
        val x2 = longitudeToX(point2.longitude)
        val y2 = latitudeToY(point2.latitude)
        return sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))
    }
}
package com.lqz.imap.utils

import com.lqz.imap.model.CoordinateSystem
import com.lqz.imap.model.ILatLng
import com.lqz.imap.utils.MathUtils.EARTH_ARC
import com.lqz.imap.utils.MathUtils.EARTH_RADIUS
import com.lqz.imap.utils.MathUtils.arcHav
import com.lqz.imap.utils.MathUtils.havDistance
import com.lqz.imap.utils.geotransport.GCJPointer
import com.lqz.imap.utils.geotransport.WGSPointer
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object IMapUtils {
    fun checkILatLng(latLng: ILatLng): Boolean {
        return !(latLng.longitude < -180 || latLng.longitude > 180 || latLng.latitude < -90 || latLng.latitude > 90)
    }

    /**
     * 根据输入的地点坐标计算中心点
     * @param coordinateList 坐标集合
     * @return 中心点坐标
     */
    fun getCenter(coordinateList: List<ILatLng>): ILatLng {
        val total = coordinateList.size
        var X = 0.0
        var Y = 0.0
        var Z = 0.0
        for (coordinate in coordinateList) {
            val lat: Double = coordinate.latitude * Math.PI / 180
            val lon: Double = coordinate.longitude * Math.PI / 180
            X += cos(lat) * cos(lon)
            Y += cos(lat) * sin(lon)
            Z += sin(lat)
        }
        X /= total
        Y /= total
        Z /= total
        val lon2 = atan2(Y, X)
        val hyp = sqrt(X * X + Y * Y)
        val lat2 = atan2(Z, hyp)
        return ILatLng(lat2 * 180 / Math.PI, lon2 * 180 / Math.PI)
    }


    fun transLatLng(latLng: ILatLng, sourceCS: Int, targetCs: Int): ILatLng {
        if (sourceCS == CoordinateSystem.GCJ) {
            val sourcePointer = GCJPointer(latLng.latitude, latLng.longitude)
            if (targetCs == CoordinateSystem.WGS) {
                val wgsPointer: WGSPointer = sourcePointer.toExactWGSPointer()
                return ILatLng(wgsPointer.getLatitude(), wgsPointer.getLongitude())
            }
        } else if (sourceCS == CoordinateSystem.WGS) {
            val sourcePointer = WGSPointer(latLng.latitude, latLng.longitude)
            if (targetCs == CoordinateSystem.GCJ) {
                val gcjPointer: GCJPointer = sourcePointer.toGCJPointer()
                return ILatLng(gcjPointer.getLatitude(), gcjPointer.getLongitude())
            }
        }
        return latLng
    }

    fun getAngle(pA: ILatLng, pB: ILatLng): Double {
        val lon1 = rad(pA.longitude)
        val lon2 = rad(pB.longitude)
        val a: Double = (90 - pB.latitude) * Math.PI / 180
        val b: Double = (90 - pA.latitude) * Math.PI / 180
        val AOC_BOC: Double = (pB.longitude - pA.longitude) * Math.PI / 180
        val cosC = cos(a) * cos(b) + sin(a) * sin(b) * cos(AOC_BOC)
        val sinC = sqrt(1 - cosC * cosC)
        val sinA = sin(a) * sin(AOC_BOC) / sinC
        var A = asin(sinA) * 180 / Math.PI
        var res = 0.0
        if (java.lang.Double.isNaN(A)) {
            A = if (lon1 < lon2) {
                90.0
            } else {
                270.0
            }
        }
        if (pB.longitude > pA.longitude && pB.latitude > pA.latitude) res = A
        else if (pB.longitude > pA.longitude && pB.latitude < pA.latitude) res = 180 - A
        else if (pB.longitude < pA.longitude && pB.latitude < pA.latitude) res = 180 - A
        else if (pB.longitude < pA.longitude && pB.latitude > pA.latitude) res = 360 + A
        else if (pB.longitude > pA.longitude && pB.latitude == pA.latitude) res = 90.0
        else if (pB.longitude < pA.longitude && pB.latitude == pA.latitude) res = 270.0
        else if (pB.longitude == pA.longitude && pB.latitude > pA.latitude) res = 0.0
        else if (pB.longitude == pA.longitude && pB.latitude < pA.latitude) res = 180.0
        return res
    }


    /**
     * 转化为弧度(rad)
     */
    private fun rad(d: Double): Double {
        return d * Math.PI / 180.0
    }

    fun calculateLineDistance(latLng1: ILatLng, latLng2: ILatLng): Double {
        val radLat1 = rad(latLng1.latitude)
        val radLat2 = rad(latLng2.latitude)
        val a = radLat1 - radLat2
        val b = rad(latLng1.longitude) - rad(latLng2.longitude)
        var s =
            2 * asin(sqrt(sin(a / 2).pow(2.0) + cos(radLat1) * cos(radLat2) * sin(b / 2).pow(2.0)))
        s *= EARTH_RADIUS
        //s = Math.round(s * 10000) / 10000;
        return abs(s)
    }

    /**
     * Returns distance on the unit sphere; the arguments are in radians.
     */
    private fun distanceRadians(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        return arcHav(havDistance(lat1, lat2, lng1 - lng2))
    }

    /**
     * Returns the angle between two LatLngs, in radians. This is the same as the distance
     * on the unit sphere.
     */
    private fun computeAngleBetween(from: ILatLng, to: ILatLng): Double {
        return distanceRadians(
            Math.toRadians(from.latitude), Math.toRadians(from.longitude),
            Math.toRadians(to.latitude), Math.toRadians(to.longitude)
        )
    }

    /**
     * Returns the distance between two LatLngs, in meters.
     */
    fun computeDistanceBetween(from: ILatLng, to: ILatLng): Double {
        return computeAngleBetween(from, to) * EARTH_RADIUS
    }

    /**
     * 已知一点经纬度A，和与另一点B的距离和方位角，求B的经纬度
     *
     * @param latLng   A的经纬度
     * @param distance AB距离（单位：米）
     * @param azimuth  AB方位角
     * @return B的经纬度
     */
    fun convertDistanceToLogLat(
        latLng: ILatLng,
        distance: Double, azimuth: Double
    ): ILatLng {
        val azi = rad(azimuth)
        // 将距离转换成经度的计算公式
        val lon = latLng.longitude + (distance * sin(azi)) / (EARTH_ARC * cos(rad(latLng.latitude)))
        // 将距离转换成纬度的计算公式
        val lat: Double = latLng.latitude + distance * cos(azi) / EARTH_ARC
        return ILatLng(lat, lon)
    }

    /**
     * Returns the length of the given path, in meters, on Earth.
     */
    fun computeLength(path: List<ILatLng>): Double {
        if (path.size < 2) {
            return 0.0
        }
        var length = 0.0
        val prev: ILatLng = path[0]
        var prevLat = Math.toRadians(prev.latitude)
        var prevLng = Math.toRadians(prev.longitude)
        for (point in path) {
            val lat = Math.toRadians(point.latitude)
            val lng = Math.toRadians(point.longitude)
            length += distanceRadians(prevLat, prevLng, lat, lng)
            prevLat = lat
            prevLng = lng
        }
        return length * EARTH_RADIUS
    }


    /**
     * Returns the area of a closed path on Earth.
     *
     * @param path A closed path.
     * @return The path's area in square meters.
     */
    fun computeArea(path: List<ILatLng>): Double {
        return abs(computeSignedArea(path))
    }

    /**
     * Returns the signed area of a closed path on Earth. The sign of the area may be used to
     * determine the orientation of the path.
     * "inside" is the surface that does not contain the South Pole.
     *
     * @param path A closed path.
     * @return The loop's area in square meters.
     */
    fun computeSignedArea(path: List<ILatLng>): Double {
        return computeSignedArea(path, EARTH_RADIUS)
    }

    /**
     * Returns the signed area of a closed path on a sphere of given radius.
     * The computed area uses the same units as the radius squared.
     * Used by SphericalUtilTest.
     */
    fun computeSignedArea(path: List<ILatLng>, radius: Double): Double {
        val size = path.size
        if (size < 3) {
            return 0.0
        }
        var total = 0.0
        val prev: ILatLng = path[size - 1]
        var prevTanLat = tan((Math.PI / 2 - Math.toRadians(prev.latitude)) / 2)
        var prevLng = Math.toRadians(prev.longitude)
        // For each edge, accumulate the signed area of the triangle formed by the North Pole
        // and that edge ("polar triangle").
        for (point in path) {
            val tanLat = tan((Math.PI / 2 - Math.toRadians(point.latitude)) / 2)
            val lng = Math.toRadians(point.longitude)
            total += polarTriangleArea(tanLat, lng, prevTanLat, prevLng)
            prevTanLat = tanLat
            prevLng = lng
        }
        return total * (radius * radius)
    }

    /**
     * Returns the signed area of a triangle which has North Pole as a vertex.
     * Formula derived from "Area of a spherical triangle given two edges and the included angle"
     * as per "Spherical Trigonometry" by Todhunter, page 71, section 103, point 2.
     * See http://books.google.com/books?id=3uBHAAAAIAAJ&pg=PA71
     * The arguments named "tan" are tan((pi/2 - latitude)/2).
     */
    private fun polarTriangleArea(tan1: Double, lng1: Double, tan2: Double, lng2: Double): Double {
        val deltaLng = lng1 - lng2
        val t = tan1 * tan2
        return 2 * atan2(t * sin(deltaLng), 1 + t * cos(deltaLng))
    }

    fun calPolygonCenterPoint(list: List<ILatLng>): ILatLng? {
        return if (1 == list.size) {
            list[0]
        } else if (2 == list.size) {
            ILatLng(
                (list[0].latitude + list[1].latitude) / 2,
                (list[0].longitude + list[1].longitude) / 2
            )
        } else if (list.size >= 3) {
            val newList: MutableList<ILatLng> = ArrayList<ILatLng>()
            for (i in 1 until list.size - 1) {
                newList.add(CalTriCenterPoint(list[0], list[i], list[i + 1]))
            }
            calPolygonCenterPoint(newList)
        } else {
            null
        }
    }

    private fun CalTriCenterPoint(pt1: ILatLng, pt2: ILatLng, pt3: ILatLng): ILatLng {
        var x: Double = pt1.latitude + pt2.latitude + pt3.latitude
        x /= 3.0
        var y: Double = pt1.longitude + pt2.longitude + pt3.longitude
        y /= 3.0
        return ILatLng(x, y)
    }

}
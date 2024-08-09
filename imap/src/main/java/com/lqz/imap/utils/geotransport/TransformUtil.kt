package com.lqz.imap.utils.geotransport

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object TransformUtil {
    fun outOfChina(lat: Double, lng: Double): Boolean {
        if (lng < 72.004 || lng > 137.8347) {
            return true
        }
        return lat < 0.8293 || lat > 55.8271
    }

    private fun transformLat(x: Double, y: Double): Double {
        var ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * sqrt(abs(x))
        ret += (20.0 * sin(6.0 * x * Math.PI) + 20.0 * sin(2.0 * x * Math.PI)) * 2.0 / 3.0
        ret += (20.0 * sin(y * Math.PI) + 40.0 * sin(y / 3.0 * Math.PI)) * 2.0 / 3.0
        ret += (160.0 * sin(y / 12.0 * Math.PI) + 320.0 * sin(y * Math.PI / 30.0)) * 2.0 / 3.0
        return ret
    }

    private fun transformLon(x: Double, y: Double): Double {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * sqrt(abs(x))
        ret += (20.0 * sin(6.0 * x * Math.PI) + 20.0 * sin(2.0 * x * Math.PI)) * 2.0 / 3.0
        ret += (20.0 * sin(x * Math.PI) + 40.0 * sin(x / 3.0 * Math.PI)) * 2.0 / 3.0
        ret += (150.0 * sin(x / 12.0 * Math.PI) + 300.0 * sin(x / 30.0 * Math.PI)) * 2.0 / 3.0
        return ret
    }

    /**
     *
     * @param lat 纬度
     * @param lng 经度
     * @return delta[0] 是纬度差，delta[1]是经度差
     */
    fun delta(lat: Double, lng: Double): DoubleArray {
        val delta = DoubleArray(2)
        val a = 6378245.0
        val ee = 0.00669342162296594323
        val dLat = transformLat(lng - 105.0, lat - 35.0)
        val dLng = transformLon(lng - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * Math.PI
        var magic = sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = sqrt(magic)
        delta[0] = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * Math.PI)
        delta[1] = dLng * 180.0 / (a / sqrtMagic * cos(radLat) * Math.PI)
        return delta
    }
}
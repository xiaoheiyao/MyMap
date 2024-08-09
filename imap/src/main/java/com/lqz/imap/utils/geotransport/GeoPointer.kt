package com.lqz.imap.utils.geotransport

import java.io.Serializable
import java.text.DecimalFormat
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

open class GeoPointer(
    open var longitude: Double,
    open var latitude: Double,
): Serializable {
    private var df = DecimalFormat("0.0000000")


    override fun equals(other: Any?): Boolean {
        return if (other === this) {
            true
        } else {
            if (other is GeoPointer) {
                df.format(latitude) == df.format(other.latitude) && df.format(longitude) == df.format(
                    other.longitude
                )
            } else {
                false
            }
        }
    }

    override fun hashCode(): Int {
        var result = df.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + latitude.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("latitude:$latitude")
        sb.append(" longitude:$longitude")
        return sb.toString()
    }

    fun distance(target: GeoPointer): Double {
        val earthR = 6371000.0
        val x =
            cos(latitude * Math.PI / 180) * cos(target.latitude * Math.PI / 180) * cos(
                (longitude - target.longitude) * Math.PI / 180
            )
        val y =
            sin(latitude * Math.PI / 180) * sin(target.latitude * Math.PI / 180)
        var s = x + y
        if (s > 1) {
            s = 1.0
        }
        if (s < -1) {
            s = -1.0
        }
        val alpha = acos(s)
        return alpha * earthR
    }

}
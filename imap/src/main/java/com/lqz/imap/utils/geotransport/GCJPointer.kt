package com.lqz.imap.utils.geotransport

import kotlin.math.abs


data class GCJPointer(
    override var longitude: Double,
    override var latitude: Double,
) : GeoPointer(longitude,latitude) {


    fun toWGSPointer(): WGSPointer {
        if (TransformUtil.outOfChina(latitude, longitude)) {
            return WGSPointer(latitude, longitude)
        }
        val delta: DoubleArray = TransformUtil.delta(latitude, longitude)
        return WGSPointer(latitude - delta[0], longitude - delta[1])
    }

    fun toExactWGSPointer(): WGSPointer {
//    if (TransformUtil.outOfChina(this.latitude, this.longitude)) {
//      return new WGSPointer(this.latitude, this.longitude);
//    }
        val initDelta = 0.01
        val threshold = 0.000001
        var dLat = initDelta
        var dLng = initDelta
        var mLat: Double = latitude - dLat
        var mLng: Double = longitude - dLng
        var pLat: Double = latitude + dLat
        var pLng: Double = longitude + dLng
        var wgsLat: Double
        var wgsLng: Double
        var currentWGSPointer: WGSPointer? = null
        for (i in 0..29) {
            wgsLat = (mLat + pLat) / 2
            wgsLng = (mLng + pLng) / 2
            currentWGSPointer = WGSPointer(wgsLat, wgsLng)
            val tmp: GCJPointer = currentWGSPointer.toGCJPointer()
            dLat = tmp.latitude - latitude
            dLng = tmp.longitude - longitude
            if (abs(dLat) < threshold && abs(dLng) < threshold) {
                return currentWGSPointer
            } else {
//        System.out.println(dLat + ":" + dLng);
            }
            if (dLat > 0) {
                pLat = wgsLat
            } else {
                mLat = wgsLat
            }
            if (dLng > 0) {
                pLng = wgsLng
            } else {
                mLng = wgsLng
            }
        }
        return currentWGSPointer!!
    }
}


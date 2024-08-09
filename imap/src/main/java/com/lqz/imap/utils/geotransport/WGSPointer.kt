package com.lqz.imap.utils.geotransport


data class WGSPointer(
    override var longitude: Double,
    override var latitude: Double,
) : GeoPointer(longitude, latitude) {

    fun toGCJPointer(): GCJPointer {
        if (TransformUtil.outOfChina(latitude, longitude)) {
            return GCJPointer(latitude, longitude)
        }
        val delta = TransformUtil.delta(latitude, longitude)
        return GCJPointer(latitude + delta[0], longitude + delta[1])
    }
}

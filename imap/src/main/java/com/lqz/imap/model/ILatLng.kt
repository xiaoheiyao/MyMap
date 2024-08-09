package com.lqz.imap.model

data class ILatLng(
    val latitude: Double,
    val longitude: Double,

    ) {
    /**
     * 判断是否大致相等
     * true: 精确到小数点后八位相等，毫米级
     * false: 不相等
     */
    fun isApproximatelyEqual(other: ILatLng, precision: Int = 8): Boolean {
        val lat1 = "%.8f".format(this.latitude).substring(0, precision)
        val lon1 = "%.8f".format(this.longitude).substring(0, precision)
        val lat2 = "%.8f".format(other.latitude).substring(0, precision)
        val lon2 = "%.8f".format(other.longitude).substring(0, precision)
        return lat1 == lat2 && lon1 == lon2
    }

}

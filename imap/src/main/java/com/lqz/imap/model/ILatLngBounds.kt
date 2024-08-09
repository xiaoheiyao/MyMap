package com.lqz.imap.model

import kotlin.math.max
import kotlin.math.min

data class ILatLngBounds(
    val mLatNorth: Double, //北
    val mLatSouth: Double, //南
    val mLonEast: Double, //东
    val mLonWest: Double, //西
) {
    /**
     * 获取中心点
     */
    fun getCenter(): ILatLng {
        return ILatLng(
            (mLatNorth + mLatSouth) / 2,
            (mLonEast + mLonWest) / 2
        )
    }

    companion object {
        /**
         * 通过list 获取到 边界数据 todo 如果list数据是错乱的，获取到的边界数据并不准
         */
        fun fromLatLngs(latLngs: List<ILatLng>): ILatLngBounds {
            var minLat = 90.0
            var minLon = 180.0
            var maxLat = -90.0
            var maxLon = -180.0

            for ((latitude, longitude) in latLngs) {
                minLat = min(minLat, latitude)
                minLon = min(minLon, longitude)
                maxLat = max(maxLat, latitude)
                maxLon = max(maxLon, longitude)
            }

            return ILatLngBounds(maxLat, maxLon, minLat, minLon)
        }


        /**
         * LatLngBounds对象生成器。
         */
        class Builder {
            private val mLatLngList: MutableList<ILatLng>

            init {
                mLatLngList = ArrayList()
            }

            fun build(): ILatLngBounds {
                return fromLatLngs(mLatLngList)
            }

            fun includes(latLngs: List<ILatLng>): Builder {
                for (point in latLngs) {
                    mLatLngList.add(point)
                }
                return this
            }

            val latLngList: List<ILatLng>
                get() = mLatLngList

            fun include(latLng: ILatLng): Builder {
                mLatLngList.add(latLng)
                return this
            }
        }

    }

    fun toLatLngs(): Array<ILatLng> {
        return arrayOf(ILatLng(mLatNorth, mLonEast), ILatLng(mLatSouth, mLonWest))
    }
}

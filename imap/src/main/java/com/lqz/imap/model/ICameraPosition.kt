package com.lqz.imap.model

import android.annotation.SuppressLint
import androidx.annotation.FloatRange
import com.lqz.imap.utils.Constants
import com.lqz.imap.utils.MathUtils

/**
 * 类似于用户视角的位置、角度、缩放和倾斜。
 */
data class ICameraPosition(
    /**
     * The location that the camera is pointing at.
     */
    val target: ILatLng?,
    /**
     * Zoom level near the center of the screen. See zoom(float) for the definition of the camera's zoom level.
     */
    val zoom: Double,
    /**
     * The angle, in degrees, of the camera angle from the nadir (directly facing the Earth). See tilt(float) for details of restrictions on the range of values.
     */
    val tilt: Double,
    /**
     * Direction that the camera is pointing in, in degrees clockwise from north.
     */
    val bearing: Double,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val (target1, zoom1, tilt1, bearing1) = other as ICameraPosition
        if (target != null && !target.equals(target1)) {
            return false
        } else if (zoom != zoom1) {
            return false
        } else if (tilt != tilt1) {
            return false
        } else if (bearing != bearing1) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + (target?.hashCode() ?: 0)
        return result
    }


    /**
     * Builder for composing [ICameraPosition] objects.
     */
    class Builder {
        private var bearing = -1.0
        private var target: ILatLng? = null
        private var tilt = -1.0
        private var zoom = -1.0
        private var isRadiant = true

        /**
         * Creates an empty builder.
         */
        constructor() : super()

        /**
         * Creates a builder for building CameraPosition objects using radiants.
         *
         * @param isRadiant true if heading is in radiants
         */
        constructor(isRadiant: Boolean) {
            this.isRadiant = isRadiant
        }

        /**
         * Create Builder with an existing CameraPosition data.
         *
         * @param previous Existing CameraPosition values to use
         */
        constructor(previous: ICameraPosition?) : super() {
            if (previous != null) {
                bearing = previous.bearing
                target = previous.target
                tilt = previous.tilt
                zoom = previous.zoom
            }
        }

        /**
         * Create Builder from an exisiting array of doubles.
         *
         * @param values Values containing target, bearing, tilt and zoom
         */
        constructor(values: DoubleArray?) : super() {
            if (values != null && values.size == 5) {
                target = ILatLng(values[0], values[1])
                bearing = values[2].toFloat().toDouble()
                tilt = values[3].toFloat().toDouble()
                zoom = values[4].toFloat().toDouble()
            }
        }

        /**
         * Sets the direction that the camera is pointing in, in degrees clockwise from north.
         *
         * @param bearing Bearing
         * @return Builder
         */
        fun bearing(bearing: Double): Builder {
            if (isRadiant) {
                this.bearing = bearing
            } else {
                // converting degrees to radiant
                this.bearing = (-bearing * Math.PI / 180.0).toFloat().toDouble()
            }
            return this
        }

        /**
         * Builds a CameraPosition.
         *
         * @return CameraPosition
         */
        fun build(): ICameraPosition {
            return ICameraPosition(target, zoom, tilt, bearing)
        }

        /**
         * Sets the location that the camera is pointing at.
         *
         * @param location Location
         * @return Builder
         */
        fun target(location: ILatLng?): Builder {
            target = location
            return this
        }

        /**
         * Set the tilt
         *
         * @param tilt Tilt value
         * @return Builder
         */
        fun tilt(@FloatRange(from = 0.0, to = 60.0) tilt: Double): Builder {
            if (isRadiant) {
                this.tilt = tilt
            } else {
                // converting degrees to radiant
                this.tilt = ((MathUtils.clamp(
                    tilt,
                    Constants.MINIMUM_TILT,
                    Constants.MAXIMUM_TILT
                ) * Constants.DEG2RAD).toFloat()).toDouble()
            }
            return this
        }

        /**
         * Set the zoom
         *
         * @param zoom Zoom value
         * @return Builder
         */
        fun zoom(zoom: Double): Builder {
            this.zoom = zoom
            return this
        }
    }


}

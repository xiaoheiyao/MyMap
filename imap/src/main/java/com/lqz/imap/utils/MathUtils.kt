package com.lqz.imap.utils

import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan


object MathUtils {
    /**
     * The earth's radius, in meters.
     * Mean radius as defined by IUGG.
     */
    const val EARTH_RADIUS = 6378137.0

    /**
     * 地球每度的弧长
     */
    const val EARTH_ARC = 111199.0

    /**
     * Test a value in specified range, returning minimum if it's below, and maximum if it's above
     *
     * @param value Value to test
     * @param min   Minimum value of range
     * @param max   Maximum value of range
     * @return value if it's between min and max, min if it's below, max if it's above
     */
    fun clamp(value: Double, min: Double, max: Double): Double {
        return max(min, min(max, value))
    }

    /**
     * Test a value in specified range, returning minimum if it's below, and maximum if it's above
     *
     * @param value Value to test
     * @param min   Minimum value of range
     * @param max   Maximum value of range
     * @return value if it's between min and max, min if it's below, max if it's above
     */
    fun clamp(value: Float, min: Float, max: Float): Float {
        return max(min.toDouble(), min(max.toDouble(), value.toDouble())).toFloat()
    }

    /**
     * Constrains value to the given range (including min, excluding max) via modular arithmetic.
     *
     *
     * Same formula as used in Core GL (wrap.hpp)
     * std::fmod((std::fmod((value - min), d) + d), d) + min;
     *
     * @param value Value to wrap
     * @param min   Minimum value
     * @param max   Maximum value
     * @return Wrapped value
     */
    fun wrap(value: Double, min: Double, max: Double): Double {
        val delta = max - min
        val firstMod = (value - min) % delta
        val secondMod = (firstMod + delta) % delta
        return secondMod + min
    }

    /**
     * Returns the non-negative remainder of x / m.
     * @param x The operand.
     * @param m The modulus.
     */
    fun mod(x: Double, m: Double): Double {
        return (x % m + m) % m
    }

    /**
     * Returns mercator Y corresponding to latitude.
     * See http://en.wikipedia.org/wiki/Mercator_projection .
     */
    fun mercator(lat: Double): Double {
        return ln(tan(lat * 0.5 + Math.PI / 4))
    }

    /**
     * Returns latitude from mercator Y.
     */
    fun inverseMercator(y: Double): Double {
        return 2 * atan(exp(y)) - Math.PI / 2
    }

    /**
     * Returns haversine(angle-in-radians).
     * hav(x) == (1 - cos(x)) / 2 == sin(x / 2)^2.
     */
    fun hav(x: Double): Double {
        val sinHalf = sin(x * 0.5)
        return sinHalf * sinHalf
    }

    /**
     * Computes inverse haversine. Has good numerical stability around 0.
     * arcHav(x) == acos(1 - 2 * x) == 2 * asin(sqrt(x)).
     * The argument must be in [0, 1], and the result is positive.
     */
    fun arcHav(x: Double): Double {
        return 2 * asin(sqrt(x))
    }

    // Given h==hav(x), returns sin(abs(x)).
    fun sinFromHav(h: Double): Double {
        return 2 * sqrt(h * (1 - h))
    }

    // Returns hav(asin(x)).
    fun havFromSin(x: Double): Double {
        val x2 = x * x
        return x2 / (1 + sqrt(1 - x2)) * .5
    }

    // Returns sin(arcHav(x) + arcHav(y)).
    fun sinSumFromHav(x: Double, y: Double): Double {
        val a = sqrt(x * (1 - x))
        val b = sqrt(y * (1 - y))
        return 2 * (a + b - 2 * (a * y + b * x))
    }

    /**
     * Returns hav() of distance from (lat1, lng1) to (lat2, lng2) on the unit sphere.
     */
    fun havDistance(lat1: Double, lat2: Double, dLng: Double): Double {
        return hav(lat1 - lat2) + hav(dLng) * cos(lat1) * cos(lat2)
    }
}

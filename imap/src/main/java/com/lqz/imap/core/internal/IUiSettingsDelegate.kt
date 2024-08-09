package com.lqz.imap.core.internal

/**
 * 地图UI图层设置委托
 */
interface IUiSettingsDelegate {
    /**
     * 设置缩放控制是否启用
     */
    fun setZoomControlsEnabled(enable: Boolean)

    /**
     * 设置指南针是否启用
     */
    fun setCompassEnabled(enable: Boolean)

    /**
     * 设置地图旋转是否启用
     */
    fun setRotateGesturesEnabled(enable: Boolean)

    /**
     * 设置比例控制是否启用
     */
    fun setScaleControlsEnabled(enable: Boolean)

    /**
     * 获得宽度
     */
    fun getWidth(): Int

    /**
     * 获得高度
     */
    fun getHeight(): Int

    /**
     * 获得padding
     */
    fun getPadding(): IntArray
}
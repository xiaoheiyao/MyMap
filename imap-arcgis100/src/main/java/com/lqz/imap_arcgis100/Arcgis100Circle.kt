package com.lqz.imap_arcgis100

import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.Polygon
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.lqz.imap.core.internal.ICircleDelegate
import com.lqz.imap.model.ILatLng
import com.lqz.imap.utils.IMapUtils

class Arcgis100Circle(
    val mapWrapper: Arcgis100MapWrapper,
    val circleGraphic: Graphic,
    val centerLatLng: ILatLng,
    var radiusMeter: Float,
) : ICircleDelegate {
    //创建初始圆心
    val centerPoint =
        Point(centerLatLng.longitude, centerLatLng.latitude, SpatialReferences.getWgs84())

    private var fillColor: Int = 0x5500FF00.toInt() // 默认半透明的绿色填充
    private var strokeColor: Int = 0xFF00FF00.toInt() // 默认绿色边框
    private var width: Float = 2f // 宽度

    private var `object`: Any? = null

    private var draggable = false

    /**
     * 更新圆的半径
     */
    override fun setRadius(width: Float) {
        radiusMeter = width
        val updatedCircleGeometry = GeometryEngine.buffer(centerPoint, width.toDouble()) as Polygon
        circleGraphic.geometry = updatedCircleGeometry
    }

    override fun getRadius(): Float {
        return radiusMeter
    }

    override fun setStrokeWidth(width: Float) {
        this.width = width
        updateCircleStyle()
    }

    override fun getStrokeWidth(): Float {
        return this.width
    }

    override fun setFillColor(fillColor: Int) {
        this.fillColor = fillColor
        updateCircleStyle()
    }

    override fun getFillColor(): Int {
        return this.fillColor
    }

    override fun setStrokeColor(strokeColor: Int) {
        this.strokeColor = strokeColor
        updateCircleStyle()
    }

    override fun getStrokeColor(): Int {
        return this.strokeColor
    }

    override fun setCenter(center: ILatLng) {

    }

    override fun getCenter(): ILatLng {
        return centerLatLng
    }

    override fun setDraggable(draggable: Boolean) {
        this.draggable = draggable
    }

    override fun isDraggable(): Boolean {
        return draggable
    }

    override fun getObject(): Any? {
        return `object`
    }

    override fun setObject(o: Any?) {
        `object` = o
    }

    override fun remove() {
        mapWrapper.getCircleHashMap().remove(circleGraphic) //删除map列表中的值
        //删除图层中的数据
        mapWrapper.getGraphicsOverlay()?.graphics?.remove(circleGraphic)

    }

    override fun getId(): String {
        return circleGraphic.toString()
    }

    override fun setZIndex(var1: Float) {
        TODO("Not yet implemented")
    }

    override fun getZIndex(): Float {
        TODO("Not yet implemented")
    }

    override fun setVisible(var1: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isVisible(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * 更新圆的样式
     */
    private fun updateCircleStyle(
        fillColor: Int = this.fillColor,
        strokeColor: Int = this.strokeColor,
        width: Float = this.width
    ) {
        val circleSymbol = createCircleSymbol(fillColor, strokeColor, width)
        circleGraphic.symbol = circleSymbol

    }

    // 创建圆形样式
    private fun createCircleSymbol(
        fillColor: Int,
        strokeColor: Int,
        width: Float
    ): SimpleFillSymbol {
        return SimpleFillSymbol(
            SimpleFillSymbol.Style.SOLID,
            fillColor,
            SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, strokeColor, width)
        )
    }
}
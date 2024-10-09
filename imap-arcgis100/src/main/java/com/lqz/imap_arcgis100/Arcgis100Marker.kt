package com.lqz.imap_arcgis100

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.lqz.imap.core.internal.IMarkerDelegate
import com.lqz.imap.model.ILatLng
import com.lqz.imap.model.IMarkerOptions
import com.lqz.imap.utils.ViewUtils
import java.lang.ref.WeakReference

class Arcgis100Marker(
    val marker: Graphic,
    val wrapper: Arcgis100MapWrapper,
    val options: IMarkerOptions,
) : IMarkerDelegate {

    private var mapWrapperWeakReference: WeakReference<Arcgis100MapWrapper> = WeakReference(wrapper)

    private var point: Point = (marker.geometry as Point)

    private var `object`: Any? = null
    private var draggable = false
    private var enable = false
    private var title: String? = null

    override fun getObject(): Any? {
        return `object`
    }

    override fun setObject(o: Any?) {
        `object` = o
    }

    override fun setTitle(title: String?) {
        this.title = title
    }

    override fun getTitle(): String? {
        return title
    }

    override fun setIcon(bitmap: Bitmap) {
        val markerSymbol: PictureMarkerSymbol
        val bitmapDrawable = BitmapDrawable(wrapper.getContext().resources, options.icon!!.bitmap)
        markerSymbol = PictureMarkerSymbol.createAsync(bitmapDrawable).get().apply {
            offsetX = ViewUtils.px2dip(
                wrapper.getContext(),
                options.icon!!.bitmap.getWidth().toFloat() * (options.anchorX - 0.5f)
            )
            offsetY = ViewUtils.px2dip(
                wrapper.getContext(),
                options.icon!!.bitmap.getHeight().toFloat() * (options.anchorY - 0.5f)
            )
        }
        markerSymbol.setAngle(options.rotate)

        marker.symbol = markerSymbol // 更新图标

    }

    override fun setIcon(view: View) {
        val markerSymbol: PictureMarkerSymbol
        val bitmapDrawable =
            BitmapDrawable(wrapper.getContext().resources, ViewUtils.convertViewToBitmap(view))
        markerSymbol = PictureMarkerSymbol.createAsync(bitmapDrawable).get().apply {
            offsetX = ViewUtils.px2dip(
                wrapper.getContext(),
                options.icon!!.bitmap.getWidth().toFloat() * (options.anchorX - 0.5f)
            )
            offsetY = ViewUtils.px2dip(
                wrapper.getContext(),
                options.icon!!.bitmap.getHeight().toFloat() * (options.anchorY - 0.5f)
            )
        }
        markerSymbol.setAngle(options.rotate)

        marker.symbol = markerSymbol // 更新图标
    }

    override fun showInfoWindow() {
        TODO("Not yet implemented")
    }

    override fun hideInfoWindow() {
        TODO("Not yet implemented")
    }

    override fun setDraggable(draggable: Boolean) {
        this.draggable = draggable
    }

    override fun isDraggable(): Boolean {
        return draggable
    }

    override fun getPosition(): ILatLng {
        return ILatLng(point.y, point.x)
    }

    override fun setPosition(latLng: ILatLng) {
        val newLocation = Point(latLng.longitude, latLng.latitude)
        marker.geometry = newLocation
    }

    override fun setRotate(rotate: Float) {
        try {
            val symbol = (marker.symbol as PictureMarkerSymbol)
            symbol.angle = rotate
            marker.symbol = symbol // 更新符号以应用新的旋转角度
        } catch (ignored: Exception) {
        }
    }

    override fun getRotate(): Float {
        val pictureMarkerSymbol = marker.symbol as PictureMarkerSymbol
        return pictureMarkerSymbol.angle
    }

    override fun setEnable(enable: Boolean) {
        this.enable = enable
    }

    override fun getEnable(): Boolean {
        return enable
    }

    override fun setZIndex(zIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun setZIndex(var1: Float) {
        TODO("Not yet implemented")
    }

    override fun remove() {
        mapWrapperWeakReference.get()?.getMapMarkerHashMap()?.remove(marker)
        mapWrapperWeakReference.get()?.getGraphicsOverlay()?.graphics?.remove(marker)
    }

    override fun getId(): String? {
        TODO("Not yet implemented")
    }

    override fun getZIndex(): Float {
        return marker.zIndex.toFloat()
    }

    override fun setVisible(var1: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isVisible(): Boolean {
        return true
    }

}
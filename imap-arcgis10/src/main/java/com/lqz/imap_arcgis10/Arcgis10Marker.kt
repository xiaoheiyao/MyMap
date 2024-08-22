package com.lqz.imap_arcgis10

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.view.MotionEvent
import android.view.View
import com.esri.android.map.Callout
import com.esri.core.geometry.GeometryEngine
import com.esri.core.geometry.Point
import com.esri.core.geometry.SpatialReference
import com.esri.core.map.Graphic
import com.esri.core.symbol.PictureMarkerSymbol
import com.lqz.imap.core.internal.IMarkerDelegate
import com.lqz.imap.core.internal.IProjectionDelegate
import com.lqz.imap.core.listener.OnMarkerDragListener
import com.lqz.imap.model.ILatLng
import com.lqz.imap.model.IMarkerOptions
import com.lqz.imap.utils.ViewUtils
import java.lang.ref.WeakReference

class Arcgis10Marker(
    val id: Int,
    val marker: Graphic,
    val wrapper: Arcgis10MapWrapper,
    val options: IMarkerOptions,
) : IMarkerDelegate {

    private var mapWrapperWeakReference: WeakReference<Arcgis10MapWrapper> = WeakReference(wrapper)

    private var point: Point = (marker.geometry as Point)

    private var `object`: Any? = null
    private var draggable = false
    private var enable = false
    private var title: String? = null

    private val mTempRect = RectF()
    private var updateTime: Long = 0

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
        val markerSymbol = PictureMarkerSymbol(BitmapDrawable(bitmap))
        var markerWidth = 0f
        var markerHeight = 0f
        markerWidth = bitmap.getWidth().toFloat()
        markerHeight = bitmap.getHeight().toFloat()
        markerSymbol.setOffsetX(
            ViewUtils.px2dip(
                mapWrapperWeakReference.get()!!.getContext(),
                markerWidth * (options.anchorX - 0.5f)
            )
        )
        markerSymbol.setOffsetY(
            ViewUtils.px2dip(
                mapWrapperWeakReference.get()!!.getContext(),
                markerHeight * (options.anchorY - 0.5f)
            )
        )
        markerSymbol.setAngle(options.rotate)
        mapWrapperWeakReference.get()?.getGraphicsOverlay()?.updateGraphic(id, markerSymbol)
    }

    override fun setIcon(view: View) {
        val markerSymbol = PictureMarkerSymbol(BitmapDrawable(ViewUtils.convertViewToBitmap(view)))
        var markerWidth = 0f
        var markerHeight = 0f
        markerWidth = view.width.toFloat()
        markerHeight = view.height.toFloat()
        markerSymbol.setOffsetX(
            ViewUtils.px2dip(
                mapWrapperWeakReference.get()!!.getContext(),
                markerWidth * (options.anchorX - 0.5f)
            )
        )
        markerSymbol.setOffsetY(
            ViewUtils.px2dip(
                mapWrapperWeakReference.get()!!.getContext(),
                markerHeight * (options.anchorY - 0.5f)
            )
        )
        markerSymbol.setAngle(options.rotate)
        mapWrapperWeakReference.get()?.getGraphicsOverlay()?.updateGraphic(id, markerSymbol)

    }

    override fun showInfoWindow() {
        val wrapper: Arcgis10MapWrapper = mapWrapperWeakReference.get()!!
        val content: View = wrapper.getInfoWindowAdapter()!!.getInfoWindow(this)
        val callout: Callout = wrapper.getArcGISMapView().getCallout()
        callout.setContent(content)
        callout.show(point)
    }

    override fun hideInfoWindow() {
        mapWrapperWeakReference.get()?.getArcGISMapView()?.getCallout()?.animatedHide()
    }

    override fun setDraggable(draggable: Boolean) {
        this.draggable = draggable
    }

    override fun isDraggable(): Boolean {
        return draggable
    }

    override fun getPosition(): ILatLng {
        val pointLatlng = GeometryEngine.project(
            point,
            mapWrapperWeakReference.get()?.getSpatialReference(),
            SpatialReference.create(SpatialReference.WKID_WGS84)
        ) as Point
        return ILatLng(pointLatlng.y, pointLatlng.x)
    }

    override fun setPosition(latLng: ILatLng) {
        point = GeometryEngine.project(
            Point(latLng.longitude, latLng.latitude),
            SpatialReference.create(SpatialReference.WKID_WGS84),
            mapWrapperWeakReference.get()?.getSpatialReference()
        ) as Point
        mapWrapperWeakReference.get()?.getGraphicsOverlay()?.updateGraphic(id, point)
    }

    override fun setRotate(rotate: Float) {
        try {
            val pictureMarkerSymbol = marker.symbol as PictureMarkerSymbol
            pictureMarkerSymbol.setAngle(rotate)
            mapWrapperWeakReference.get()?.getGraphicsOverlay()
                ?.updateGraphic(id, pictureMarkerSymbol)
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
        mapWrapperWeakReference.get()?.getGraphicsOverlay()?.removeGraphic(id)
    }

    override fun getId(): String? {
        TODO("Not yet implemented")
    }

    override fun getZIndex(): Float {
        return marker.drawOrder.toFloat()
    }

    override fun setVisible(var1: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isVisible(): Boolean {
        return true
    }


    fun isClickOn(screenPoint: PointF): Boolean {
        if (!getEnable()) {
            return false
        }
        val pictureMarkerSymbol = marker.symbol as PictureMarkerSymbol
        val width = pictureMarkerSymbol.width
        val height = pictureMarkerSymbol.height
        val offsetX = pictureMarkerSymbol.offsetX
        val offsetY = pictureMarkerSymbol.offsetY
        val touchWidth = width + 30
        val touchHeight = height + 30
        val symbolMapPoint = point
        val symbolScreenPoint: Point =
            mapWrapperWeakReference.get()!!.getArcGISMapView().toScreenPoint(symbolMapPoint)
        mTempRect.left = (symbolScreenPoint.x + offsetX - touchWidth / 2).toInt().toFloat()
        mTempRect.top = (symbolScreenPoint.y + offsetY - touchHeight / 2).toInt().toFloat()
        mTempRect.right = (symbolScreenPoint.x + offsetX + touchWidth / 2).toInt().toFloat()
        mTempRect.bottom = (symbolScreenPoint.y + offsetY + touchHeight / 2).toInt().toFloat()
        if (mTempRect.contains(screenPoint.x, screenPoint.y)) {
            return true
        }
        return false
    }

    fun drag(
        event: MotionEvent,
        onMarkerDragListener: OnMarkerDragListener?
    ) {
        val curTime = System.currentTimeMillis()
        if (!draggable || curTime < updateTime) {
            return
        }
        updateTime = curTime + 100
        val projection: IProjectionDelegate = mapWrapperWeakReference.get()!!.getProjection()
        val pos: ILatLng? = projection.fromScreenLocation(PointF(event.x, event.y))
        if (pos != null) {
            setPosition(pos)
            onMarkerDragListener?.onMarkerDrag(this)
        }
    }
}
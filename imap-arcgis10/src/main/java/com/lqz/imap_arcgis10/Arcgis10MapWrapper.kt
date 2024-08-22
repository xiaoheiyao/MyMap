package com.lqz.imap_arcgis10

import android.content.Context
import android.graphics.PointF
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import com.esri.android.map.GraphicsLayer
import com.esri.android.map.Layer
import com.esri.android.map.MapOnTouchListener
import com.esri.android.map.MapView
import com.esri.android.map.TiledServiceLayer
import com.esri.android.map.event.OnLongPressListener
import com.esri.android.map.event.OnPinchListener
import com.esri.android.map.event.OnSingleTapListener
import com.esri.android.map.event.OnStatusChangedListener
import com.esri.android.map.event.OnZoomListener
import com.esri.core.geometry.GeometryEngine
import com.esri.core.geometry.Point
import com.esri.core.geometry.SpatialReference
import com.esri.core.map.Graphic
import com.lqz.imap.core.internal.ICircleDelegate
import com.lqz.imap.core.internal.IMapDelegate
import com.lqz.imap.core.internal.IMarkerDelegate
import com.lqz.imap.core.internal.IPolygonDelegate
import com.lqz.imap.core.internal.IPolylineDelegate
import com.lqz.imap.core.internal.IProjectionDelegate
import com.lqz.imap.core.internal.IUiSettingsDelegate
import com.lqz.imap.core.listener.InfoWindowAdapter
import com.lqz.imap.core.listener.OnCameraChangeListener
import com.lqz.imap.core.listener.OnMapClickListener
import com.lqz.imap.core.listener.OnMapLoadedListener
import com.lqz.imap.core.listener.OnMapLongClickListener
import com.lqz.imap.core.listener.OnMapMarkerClickListener
import com.lqz.imap.core.listener.OnMapMarkerLongClickListener
import com.lqz.imap.core.listener.OnMarkerDragListener
import com.lqz.imap.model.CoordinateSystem
import com.lqz.imap.model.ICameraPosition
import com.lqz.imap.model.ICameraUpdate
import com.lqz.imap.model.ICircleOptions
import com.lqz.imap.model.ILatLng
import com.lqz.imap.model.IMarkerOptions
import com.lqz.imap.model.IPolygonOptions
import com.lqz.imap.model.IPolylineOptions
import com.lqz.imap.model.MapImpType
import com.lqz.imap.model.MapType
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow

class Arcgis10MapWrapper(private val arcGISMapView: MapView) : IMapDelegate {

    private var arcgisUiSetting: Arcgis10UiSetting = Arcgis10UiSetting(arcGISMapView, this)
    private var arcgisProjection: Arcgis10Projection = Arcgis10Projection(arcGISMapView, this)


    private var graphicsOverlay: GraphicsLayer? = null
    private var serviceImageTiledLayer: TiledServiceLayer? = null
    private var annotationTiledLayer: TiledServiceLayer? = null

    private var onMapLoadedListener: OnMapLoadedListener? = null
    private var onMarkerDragListener: OnMarkerDragListener? = null
    private var onMapMarkerClickListener: OnMapMarkerClickListener? = null
    private var onMapMarkerLongClickListener: OnMapMarkerLongClickListener? = null
    private var onCameraChangeListener: OnCameraChangeListener? = null
    private var onMapClickListener: OnMapClickListener? = null
    private var onMapLongClickListener: OnMapLongClickListener? = null
    private var infoWindowAdapter: InfoWindowAdapter? = null
    private var markerDragMode = DRAG_MODE_LONG_PRESS
    private var touchTime: Long = 0
    private var language: String = LANGUAGE_ZH
    private var source = "cn"
    private var mapType: MapType? = null

    private val mMapMarkerHashMap: HashMap<Graphic, Arcgis10Marker> = HashMap()
    private val circleHashMap: HashMap<Graphic, Arcgis10Circle> = HashMap<Graphic, Arcgis10Circle>()
    private var canDragMarker: Arcgis10Marker? = null

    fun getCircleHashMap(): java.util.HashMap<Graphic, Arcgis10Circle> {
        return circleHashMap
    }
    init {
        if (graphicsOverlay == null) {
            graphicsOverlay = GraphicsLayer()
            arcGISMapView.addLayer(graphicsOverlay)
        }

        arcGISMapView.onZoomListener = object : OnZoomListener {
            override fun preAction(v: Float, v1: Float, v2: Double) {}
            override fun postAction(v: Float, v1: Float, v2: Double) {
                if (onCameraChangeListener != null) {
                    onCameraChangeListener!!.onCameraChanged(
                        getCameraPosition().target!!,
                        getCameraPosition().zoom.toFloat(), getRotate()
                    )
                }
            }
        }

        arcGISMapView.onStatusChangedListener = object : OnStatusChangedListener {
            private val serialVersionUID = 1L
            override fun onStatusChanged(source: Any, status: OnStatusChangedListener.STATUS) {
                // Set the map extent once the map has been initialized, and the basemap is added
                // or changed; this will be indicated by the layer initialization of the basemap layer. As there is only
                // a single layer, there is no need to check the source object.
                if (OnStatusChangedListener.STATUS.INITIALIZED == status) {
                    if (onMapLoadedListener != null) {
                        onMapLoadedListener!!.onMapLoaded()
                    }
                }
            }
        }

        arcGISMapView.onSingleTapListener = OnSingleTapListener { x, y ->
            val arcgisMarker: Arcgis10Marker? = getClickMarker(PointF(x, y))
            if (arcgisMarker != null && onMapMarkerClickListener != null) {
                onMapMarkerClickListener!!.onMapMarkerClick(arcgisMarker)
            }
            if (arcgisMarker != null && !TextUtils.isEmpty(arcgisMarker.getTitle()) && infoWindowAdapter != null) {
                //                    arcGISMapView.getCallout().setLocation(arcgisMarker.getInfoWindowAnchorPoint());
                //                    arcGISMapView.getCallout().getStyle().setLeaderLength((int) ViewUtils.px2dip(getContext(),arcgisMarker.getIconHeight()));
                //                    arcGISMapView.getCallout().setContent(infoWindowAdapter.getInfoWindow(arcgisMarker));
                //                    arcGISMapView.getCallout().show();
            }
            if (arcgisMarker == null && onMapClickListener != null) {
                val pointF = PointF(x, y)
                val latLng: ILatLng? = getProjection().fromScreenLocation(pointF)
                if (latLng != null) {
                    onMapClickListener!!.onMapClick(latLng)
                }
            }
        }

        arcGISMapView.onPinchListener = object : OnPinchListener {
            override fun prePointersMove(v: Float, v1: Float, v2: Float, v3: Float, v4: Double) {}
            override fun postPointersMove(v: Float, v1: Float, v2: Float, v3: Float, v4: Double) {
                if (onCameraChangeListener != null) {
                    onCameraChangeListener!!.onCameraChanged(
                        getCameraPosition().target!!,
                        getCameraPosition().zoom.toFloat(), getRotate()
                    )
                }
            }

            override fun prePointersDown(v: Float, v1: Float, v2: Float, v3: Float, v4: Double) {}
            override fun postPointersDown(v: Float, v1: Float, v2: Float, v3: Float, v4: Double) {}
            override fun prePointersUp(v: Float, v1: Float, v2: Float, v3: Float, v4: Double) {}
            override fun postPointersUp(v: Float, v1: Float, v2: Float, v3: Float, v4: Double) {
                if (onCameraChangeListener != null) {
                    onCameraChangeListener!!.onCameraChangedFinish(
                        getCameraPosition().target!!,
                        getCameraPosition().zoom.toFloat(), getRotate()
                    )
                }
            }
        }

        arcGISMapView.onLongPressListener = OnLongPressListener { x, y ->
            val clickMarker: Arcgis10Marker? = getClickMarker(PointF(x, y))
            if (onMapMarkerLongClickListener != null && clickMarker != null) {
                onMapMarkerLongClickListener!!.onMapMarkerLongClick(clickMarker)
            }
            if (markerDragMode == DRAG_MODE_LONG_PRESS) {
                if (clickMarker != null && clickMarker.isDraggable()) {
                    canDragMarker = clickMarker
                    return@OnLongPressListener true
                }
            }
            if (onMapLongClickListener != null) {
                val latLng: ILatLng? = getProjection().fromScreenLocation(PointF(x, y))
                if (latLng != null) {
                    onMapLongClickListener!!.onMapLongClick(latLng)
                }
            }
            false
        }
        val mapOnTouchListener: MapOnTouchListener =
            object : MapOnTouchListener(getContext(), arcGISMapView) {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val clickMarker: Arcgis10Marker? = getClickMarker(PointF(event.x, event.y))
                        if (clickMarker != null && clickMarker.isDraggable()) {
                            canDragMarker = clickMarker
                            touchTime = System.currentTimeMillis()
                        }
                    } else if (event.action == MotionEvent.ACTION_CANCEL
                        || event.action == MotionEvent.ACTION_UP
                    ) {
                        if (canDragMarker != null && onMarkerDragListener != null) {
                            onMarkerDragListener!!.onMarkerDragEnd(canDragMarker!!)
                        }
                        canDragMarker = null
                    }
                    val curTime = System.currentTimeMillis()
                    if (canDragMarker != null && event.action == MotionEvent.ACTION_MOVE) {
                        if (curTime - touchTime > 200) {
                            canDragMarker?.drag(event, onMarkerDragListener)
                        }
                        return true
                    }
                    return super.onTouch(v, event)
                }
            }
        arcGISMapView.setOnTouchListener(mapOnTouchListener)
    }

    override fun getMapImpType(): MapImpType {
        return MapImpType.MAP_IMP_TYPE_ARCGIS
    }

    override fun setMapType(mapType: MapType) {
        if (serviceImageTiledLayer != null && containsLayer(serviceImageTiledLayer!!)) {
            arcGISMapView.removeLayer(serviceImageTiledLayer)
        }
        if (annotationTiledLayer != null && containsLayer(annotationTiledLayer!!)) {
            arcGISMapView.removeLayer(annotationTiledLayer)
        }
        val country = Locale.getDefault().country
        if (mapType == MapType.MAP_TYPE_SATELLITE) {
            if (source == "cn") {
                serviceImageTiledLayer = TiandituMapLayer(
                    getContext(),
                    language, country, source, TiandituLayerTypes.IMAGE_MAP
                )
                annotationTiledLayer = TiandituMapLayer(
                    getContext(),
                    language, country, source, TiandituLayerTypes.ANNOTATION_IMAGE_MAP
                )
                arcGISMapView.addLayer(serviceImageTiledLayer, 0)
                arcGISMapView.addLayer(annotationTiledLayer, 1)
            }
        } else if (mapType === MapType.MAP_TYPE_NORMAL) {
            if (source == "cn") {
                serviceImageTiledLayer = TiandituMapLayer(
                    getContext(),
                    language, country, source, TiandituLayerTypes.VECTOR_MAP
                )
                annotationTiledLayer = TiandituMapLayer(
                    getContext(),
                    language, country, source, TiandituLayerTypes.ANNOTATION_VECTOR_MAP
                )
                arcGISMapView.addLayer(serviceImageTiledLayer, 0)
                arcGISMapView.addLayer(annotationTiledLayer, 1)
            }
        } else if (mapType === MapType.MAP_TYPE_USER_DEFIED) {
            serviceImageTiledLayer = UserMapLayer(
                getContext(),
                language, country, source, TiandituLayerTypes.IMAGE_MAP
            )
            arcGISMapView.addLayer(serviceImageTiledLayer, 0)
        }
        arcGISMapView.minScale = getScaleByZoomLevel(1)
        arcGISMapView.maxScale = getScaleByZoomLevel(22)
        this.mapType = mapType
    }

    override fun setLanguage(language: String) {
        this.language = language
    }

    override fun getCoordinateSystem(): Int {
        val country = Locale.getDefault().country
        return if (country.equals(
                Locale.CHINA.country,
                ignoreCase = true
            ) || mapType === MapType.MAP_TYPE_NORMAL
        ) {
            CoordinateSystem.GCJ
        } else {
            CoordinateSystem.WGS
        }
    }

    override fun setMarkerDragMode(mode: Int) {
        markerDragMode = mode
    }

    override fun getUiSettings(): IUiSettingsDelegate {
        return arcgisUiSetting
    }

    override fun getProjection(): IProjectionDelegate {
        return arcgisProjection
    }

    override fun getScalePerPixel(): Double {
        return arcGISMapView.scale
    }

    override fun getCameraPosition(): ICameraPosition {
        val builder: ICameraPosition.Builder = ICameraPosition.Builder()
        builder.bearing(getRotate())

        val targetGeometry = arcGISMapView.center
        if (targetGeometry != null) {
            val targetPoint = GeometryEngine.project(
                targetGeometry,
                getSpatialReference(),
                SpatialReference.create(SpatialReference.WKID_WGS84)
            ) as Point
            if (targetPoint.isValid) {
                builder.target(
                    ILatLng(targetPoint.y, targetPoint.x)
                )
            } else {
                builder.target(
                    ILatLng(0.0, 0.0)
                )
            }
        } else {
            builder.target(
                ILatLng(
                    0.0,
                    0.0
                )
            )
        }
        builder.zoom(arcgisProjection.calculateZoom(arcGISMapView.scale.toFloat()))
        return builder.build()
    }

    override fun animateCamera(cameraUpdate: ICameraUpdate) {
        TODO("Not yet implemented")
    }

    override fun animateCamera(cameraUpdate: ICameraUpdate, duration: Long) {
        TODO("Not yet implemented")
    }

    override fun getMaxZoom(): Float {
        TODO("Not yet implemented")
    }

    override fun getMinZoom(): Float {
        TODO("Not yet implemented")
    }

    override fun addMarker(options: IMarkerOptions): IMarkerDelegate {
        TODO("Not yet implemented")
    }

    override fun addMarkers(optionsList: List<IMarkerOptions>): List<IMarkerDelegate> {
        TODO("Not yet implemented")
    }

    override fun addPolyline(options: IPolylineOptions): IPolylineDelegate {
        TODO("Not yet implemented")
    }

    override fun addPolylines(optionsList: List<IPolylineOptions>): List<IPolylineDelegate> {
        TODO("Not yet implemented")
    }

    override fun addPolygon(options: IPolygonOptions): IPolygonDelegate {
        TODO("Not yet implemented")
    }

    override fun addPolygons(optionsList: List<IPolygonOptions>): List<IPolygonDelegate> {
        TODO("Not yet implemented")
    }

    override fun addCircle(options: ICircleOptions): ICircleDelegate {
        TODO("Not yet implemented")
    }

    override fun addCircles(optionsList: List<ICircleOptions>): List<ICircleDelegate> {
        TODO("Not yet implemented")
    }


    private fun getRotate(): Double {
        val mapRotate = arcGISMapView.rotationAngle
        return if (mapRotate < 0) {
            abs(mapRotate)
        } else {
            360 - mapRotate
        }
    }

    private fun getClickMarker(pointF: PointF): Arcgis10Marker? {
        for ((_, value) in getMapMarkerHashMap().entries) {
            if (value.isClickOn(pointF)) {
                return value
            }
        }
        return null
    }

    fun getMapMarkerHashMap(): java.util.HashMap<Graphic, Arcgis10Marker> {
        return mMapMarkerHashMap
    }

    fun getArcGISMapView(): MapView {
        return arcGISMapView
    }

    fun getInfoWindowAdapter(): InfoWindowAdapter? {
        return infoWindowAdapter
    }

    fun getGraphicsOverlay(): GraphicsLayer? {
        return graphicsOverlay
    }

    fun getContext(): Context {
        return arcGISMapView.context
    }

    fun setRotateEnable(enable: Boolean) {
        arcGISMapView.isAllowRotationByPinch = enable
    }

    fun getBaseTiledLayer(): TiledServiceLayer? {
        return serviceImageTiledLayer
    }


    fun getScaleByZoomLevel(zoom: Int): Double {
        val tileInfo = serviceImageTiledLayer!!.tileInfo
        return if (tileInfo != null) {
            val scales = tileInfo.scales
            scales[0] / 2.0.pow(zoom.toDouble())
        } else {
            591657527.591555 / 2.0.pow(zoom.toDouble())
        }
    }

    fun getSpatialReference(): SpatialReference {
        return if (serviceImageTiledLayer != null) {
            serviceImageTiledLayer!!.spatialReference
        } else {
            SpatialReference.createLocal()
        }
    }

    private fun containsLayer(layer: Layer): Boolean {
        val layers = arcGISMapView.layers
        for (hasLayer in layers) {
            if (hasLayer === layer) {
                return true
            }
        }
        return false
    }

}
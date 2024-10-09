package com.lqz.imap_arcgis10

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
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
import com.esri.core.geometry.Envelope
import com.esri.core.geometry.GeometryEngine
import com.esri.core.geometry.Point
import com.esri.core.geometry.Polygon
import com.esri.core.geometry.Polyline
import com.esri.core.geometry.SpatialReference
import com.esri.core.map.Graphic
import com.esri.core.symbol.MarkerSymbol
import com.esri.core.symbol.PictureMarkerSymbol
import com.esri.core.symbol.SimpleFillSymbol
import com.esri.core.symbol.SimpleLineSymbol
import com.esri.core.symbol.SimpleMarkerSymbol
import com.lqz.imap.core.internal.ICircleDelegate
import com.lqz.imap.core.internal.IMapDelegate
import com.lqz.imap.core.internal.IMarkerDelegate
import com.lqz.imap.core.internal.IPolygonDelegate
import com.lqz.imap.core.internal.IPolylineDelegate
import com.lqz.imap.core.internal.IProjectionDelegate
import com.lqz.imap.core.internal.IUiSettingsDelegate
import com.lqz.imap.core.listener.InfoWindowAdapter
import com.lqz.imap.core.listener.OnCameraChangeListener
import com.lqz.imap.core.listener.OnInfoWindowClickListener
import com.lqz.imap.core.listener.OnMapClickListener
import com.lqz.imap.core.listener.OnMapLoadedListener
import com.lqz.imap.core.listener.OnMapLongClickListener
import com.lqz.imap.core.listener.OnMapMarkerClickListener
import com.lqz.imap.core.listener.OnMapMarkerLongClickListener
import com.lqz.imap.core.listener.OnMapOverlayClickListener
import com.lqz.imap.core.listener.OnMarkerDragListener
import com.lqz.imap.core.listener.OnSnapshotReadyListener
import com.lqz.imap.model.CameraBoundsUpdate
import com.lqz.imap.model.CameraPositionUpdate
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
import com.lqz.imap.model.RotateUpdate
import com.lqz.imap.model.ZoomUpdate
import com.lqz.imap.utils.IMapUtils
import com.lqz.imap.utils.ViewUtils
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.pow

class Arcgis10MapWrapper(private val arcGISMapView: MapView) : IMapDelegate {

    private var arcgisUiSetting: Arcgis10UiSetting = Arcgis10UiSetting(arcGISMapView, this)
    private var arcgisProjection: Arcgis10Projection = Arcgis10Projection(arcGISMapView, this)


    private var graphicsOverlay: GraphicsLayer? = null
    private var serviceImageTiledLayer: TiledServiceLayer? = null
    private var annotationTiledLayer: TiledServiceLayer? = null

    private var onMapLoadedListener: OnMapLoadedListener? = null
    private var mOnMapOverlayClickListener: OnMapOverlayClickListener? = null
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

    private val executorService = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

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

    override fun moveCamera(cameraUpdate: ICameraUpdate) {
        moveCamera(cameraUpdate, 0, false)
    }

    override fun animateCamera(cameraUpdate: ICameraUpdate) {
        moveCamera(cameraUpdate, 200, true)
    }

    override fun animateCamera(cameraUpdate: ICameraUpdate, duration: Long) {
        moveCamera(cameraUpdate, duration, true)
    }

    override fun getMaxZoom(): Float {
        return 19f
    }

    override fun getMinZoom(): Float {
        return 0f
    }

    override fun addMarker(options: IMarkerOptions): IMarkerDelegate {
        val markerSymbol: MarkerSymbol
        if (options.icon != null) {
            markerSymbol = PictureMarkerSymbol(BitmapDrawable(options.icon!!.bitmap))
        } else {
            markerSymbol = SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
        }
        var markerWidth = 0f
        var markerHeight = 0f
        if (options.icon != null) {
            markerWidth = options.icon!!.bitmap.getWidth().toFloat()
            markerHeight = options.icon!!.bitmap.getHeight().toFloat()
        }
        markerSymbol.setOffsetX(
            ViewUtils.px2dip(
                getContext(),
                markerWidth * (options.anchorX - 0.5f)
            )
        )
        markerSymbol.setOffsetY(
            ViewUtils.px2dip(
                getContext(),
                markerHeight * (options.anchorY - 0.5f)
            )
        )
        markerSymbol.setAngle(options.rotate)
        var point = Point(options.position.longitude, options.position.latitude)
        point = GeometryEngine.project(
            point,
            SpatialReference.create(SpatialReference.WKID_WGS84),
            getSpatialReference()
        ) as Point
        val graphic = Graphic(point, markerSymbol, options.zIndex.toInt())
        val id = graphicsOverlay!!.addGraphic(graphic)
        val arcgisMarker = Arcgis10Marker(id, graphic, this, options)
        arcgisMarker.setDraggable(options.draggable)
        arcgisMarker.setTitle(options.title)
        arcgisMarker.setEnable(options.enable)
        arcgisMarker.setObject(options.`object`)
        mMapMarkerHashMap[graphic] = arcgisMarker
        return arcgisMarker
    }

    override fun addMarkers(
        optionsList: List<IMarkerOptions>,
        callback: (List<IMarkerDelegate>) -> Unit
    ) {

        // 启动一个后台线程执行计算
        executorService.execute(Runnable {
            val graphics = arrayOfNulls<Graphic>(optionsList.size)
            var i = 0
            for (options in optionsList) {
                val markerSymbol: MarkerSymbol =
                    getMarkerSymbol(options)
                var point = Point(
                    options.position.longitude,
                    options.position.latitude
                )
                point = GeometryEngine.project(
                    point,
                    SpatialReference.create(SpatialReference.WKID_WGS84),
                    getSpatialReference()
                ) as Point
                val graphic = Graphic(point, markerSymbol, options.zIndex.toInt())
                graphics[i++] = graphic
            }
            mainHandler.post(Runnable {
                val markerList: MutableList<IMarkerDelegate> =
                    ArrayList()
                Log.e("test", "initView: 地图层主线程绘制点开始")
                val ids = graphicsOverlay!!.addGraphics(graphics)
                Log.e("test", "initView: 地图层主线程绘制点结束")
                for (i1 in ids.indices) {
                    val arcgisMarker =
                        Arcgis10Marker(
                            ids[i1],
                            graphics[i1]!!,
                            this@Arcgis10MapWrapper,
                            optionsList[i1]
                        )
                    arcgisMarker.setDraggable(optionsList[i1].draggable)
                    arcgisMarker.setTitle(optionsList[i1].title)
                    arcgisMarker.setEnable(optionsList[i1].enable)
                    arcgisMarker.setObject(optionsList[i1].`object`)
                    mMapMarkerHashMap[graphics[i1]!!] = arcgisMarker
                    markerList.add(arcgisMarker)
                }
                callback(markerList)
            })
        })

    }

    override fun addPolyline(options: IPolylineOptions): IPolylineDelegate {
        val pointList: MutableList<Point> =
            java.util.ArrayList()
        val polyline = Polyline()
        var i = 0
        for ((latitude, longitude) in options.points) {
            val point = GeometryEngine.project(
                Point(
                    longitude,
                    latitude
                ), SpatialReference.create(SpatialReference.WKID_WGS84), getSpatialReference()
            ) as Point
            pointList.add(point)
            if (i == 0) {
                polyline.startPath(point)
            } else {
                polyline.lineTo(point)
            }
            i++
        }
        val lineSymbol = SimpleLineSymbol(options.color, options.width)
        if (options.isDottedLine) {
            lineSymbol.setStyle(SimpleLineSymbol.STYLE.DOT)
        } else {
            lineSymbol.setStyle(SimpleLineSymbol.STYLE.SOLID)
        }
        val graphic = Graphic(polyline, lineSymbol, options.zIndex as Int)
        val id = graphicsOverlay!!.addGraphic(graphic)
        return Arcgis10Polyline(id, graphic, this)
    }

    override fun addPolylines(optionsList: List<IPolylineOptions>): List<IPolylineDelegate> {
        TODO("Not yet implemented")
    }

    override fun addPolygon(options: IPolygonOptions): IPolygonDelegate {
        val pointList: MutableList<Point> =
            java.util.ArrayList()
        var i = 0
        val polygon = Polygon()
        for ((latitude, longitude) in options.points) {
            val point = GeometryEngine.project(
                Point(
                    longitude,
                    latitude
                ), SpatialReference.create(SpatialReference.WKID_WGS84), getSpatialReference()
            ) as Point
            pointList.add(point)
            if (i == 0) {
                polygon.startPath(point)
            } else {
                polygon.lineTo(point)
            }
            i++
        }
        val outlineSymbol =
            SimpleLineSymbol(options.strokeColor, options.strokeWidth)
        outlineSymbol.setStyle(SimpleLineSymbol.STYLE.SOLID)
        val fillSymbol =
            SimpleFillSymbol(options.fillColor, SimpleFillSymbol.STYLE.SOLID)
        fillSymbol.setOutline(outlineSymbol)
        val graphic = Graphic(polygon, fillSymbol, options.zIndex as Int)
        val id = graphicsOverlay!!.addGraphic(graphic)
        return Arcgis10Polygon(id, graphic, this)
    }

    override fun addPolygons(optionsList: List<IPolygonOptions>): List<IPolygonDelegate> {
        val polygonDelegateList: MutableList<IPolygonDelegate> = java.util.ArrayList()
        for (options in optionsList) {
            val pointList: MutableList<Point> = java.util.ArrayList()
            var i = 0
            val polygon = Polygon()
            for ((latitude, longitude) in options.points) {
                var point = Point(longitude, latitude)
                point = GeometryEngine.project(
                    point,
                    SpatialReference.create(SpatialReference.WKID_WGS84),
                    getSpatialReference()
                ) as Point
                pointList.add(point)
                if (i == 0) {
                    polygon.startPath(point)
                } else {
                    polygon.lineTo(point)
                }
                i++
            }
            val outlineSymbol = SimpleLineSymbol(options.strokeColor, options.strokeWidth)
            outlineSymbol.setStyle(SimpleLineSymbol.STYLE.SOLID)
            val fillSymbol = SimpleFillSymbol(options.fillColor, SimpleFillSymbol.STYLE.SOLID)
            fillSymbol.setOutline(outlineSymbol)
            val graphic = Graphic(polygon, fillSymbol, options.zIndex.toInt())
            val id = graphicsOverlay!!.addGraphic(graphic)
            val arcgisPolygon = Arcgis10Polygon(id, graphic, this)
            polygonDelegateList.add(arcgisPolygon)
        }
//        graphicsOverlay.addGraphics((Graphic[]) graphicList.toArray());
        //        graphicsOverlay.addGraphics((Graphic[]) graphicList.toArray());
        return polygonDelegateList
    }

    override fun addCircle(options: ICircleOptions): ICircleDelegate {
        val pointList: MutableList<Point> = java.util.ArrayList()
        val centerLatlng: ILatLng = options.centerPoint
        val radius: Float = options.radius
        val polygon = Polygon()
        for (i in 0..360) {
            val (latitude, longitude) = IMapUtils.convertDistanceToLogLat(
                centerLatlng,
                radius.toDouble(),
                i.toDouble()
            )
            val point = GeometryEngine.project(
                Point(longitude, latitude),
                SpatialReference.create(SpatialReference.WKID_WGS84),
                getSpatialReference()
            ) as Point
            pointList.add(point)
            if (i == 0) {
                polygon.startPath(point)
            } else {
                polygon.lineTo(point)
            }
        }

        val outlineSymbol = SimpleLineSymbol(options.strokeColor, options.strokeWidth)
        outlineSymbol.setStyle(SimpleLineSymbol.STYLE.SOLID)
        val fillSymbol = SimpleFillSymbol(
            options.fillColor, SimpleFillSymbol.STYLE.SOLID
        )
        fillSymbol.setOutline(outlineSymbol)
        val graphic = Graphic(polygon, fillSymbol, options.zIndex.toInt())
        val id = graphicsOverlay!!.addGraphic(graphic)
        val arcgisCircle = Arcgis10Circle(id, this, graphic, centerLatlng, options.radius)
        circleHashMap[graphic] = arcgisCircle
        return arcgisCircle
    }

    override fun addCircles(optionsList: List<ICircleOptions>): List<ICircleDelegate> {
        TODO("Not yet implemented")
    }

    override fun setOnCameraChangeListener(onCameraChangeListener: OnCameraChangeListener) {
        this.onCameraChangeListener = onCameraChangeListener
    }

    override fun setOnMapLoadedListener(onMapLoadedListener: OnMapLoadedListener) {
        this.onMapLoadedListener = onMapLoadedListener
    }

    override fun setOnInfoWindowClickListener(listener: OnInfoWindowClickListener) {
        TODO("Not yet implemented")
    }

    override fun setOnMarkerClickListener(listener: OnMapMarkerClickListener) {
        onMapMarkerClickListener = listener
    }

    override fun setOnMarkerLongClickListener(listener: OnMapMarkerLongClickListener) {
        onMapMarkerLongClickListener = listener
    }

    override fun setOnOverlayClickListener(listener: OnMapOverlayClickListener) {
        mOnMapOverlayClickListener = listener
    }

    override fun setOnMarkerDragListener(listener: OnMarkerDragListener) {
        onMarkerDragListener = listener
    }

    override fun setOnMapClickListener(listener: OnMapClickListener) {
        onMapClickListener = listener
    }

    override fun setOnMapLongClickListener(listener: OnMapLongClickListener) {
        onMapLongClickListener = listener
    }

    override fun getSnapshot(listener: OnSnapshotReadyListener) {

        // export the image from the mMapView
        val viewBitmap: Bitmap = getViewBitmap(arcGISMapView)
        if (listener != null) {
            listener.onSnapshotReady(viewBitmap)
        }
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


    private fun moveCamera(cameraUpdate: ICameraUpdate, duation: Long, animate: Boolean) {
        if (cameraUpdate is CameraPositionUpdate) {
            val cameraPositionUpdate: CameraPositionUpdate = cameraUpdate as CameraPositionUpdate
            val targetLatLng = cameraPositionUpdate.target
            var target = Point(
                targetLatLng!!.longitude,
                targetLatLng!!.latitude
            )
            target = GeometryEngine.project(
                target,
                SpatialReference.create(SpatialReference.WKID_WGS84),
                getSpatialReference()
            ) as Point
            val zoom =
                if (cameraPositionUpdate.zoom < 0) 0 else cameraPositionUpdate.zoom
            val rotate: Double =
                if (cameraPositionUpdate.bearing < 0) 0.0 else cameraPositionUpdate.bearing
            if (animate) {
                arcGISMapView.rotationAngle = rotate
                arcGISMapView.zoomToScale(target, getScaleByZoomLevel(zoom.toInt()))
            } else {
                arcGISMapView.rotationAngle = rotate
                arcGISMapView.zoomToScale(target, getScaleByZoomLevel(zoom.toInt()))
            }
        } else if (cameraUpdate is ZoomUpdate) {
            val zoomUpdate: ZoomUpdate = cameraUpdate
            if (zoomUpdate.type == ZoomUpdate.ZOOM_IN) {
                arcGISMapView.zoomin(animate)
            } else if (zoomUpdate.type == ZoomUpdate.ZOOM_OUT) {
                arcGISMapView.zoomout(animate)
            } else if (zoomUpdate.type == ZoomUpdate.ZOOM_TO) {
                val targetGeometry = arcGISMapView.center
                arcGISMapView.zoomToScale(
                    targetGeometry,
                    getScaleByZoomLevel(zoomUpdate.zoom.toInt())
                )
            }
        } else if (cameraUpdate is CameraBoundsUpdate) {
            val cameraBoundsUpdate: CameraBoundsUpdate = cameraUpdate as CameraBoundsUpdate
            var point1 = Point(
                cameraBoundsUpdate.bounds.mLonWest,
                cameraBoundsUpdate.bounds.mLatNorth
            )
            point1 = GeometryEngine.project(
                point1,
                SpatialReference.create(SpatialReference.WKID_WGS84),
                getSpatialReference()
            ) as Point
            var point2 = Point(
                cameraBoundsUpdate.bounds.mLonEast,
                cameraBoundsUpdate.bounds.mLatSouth
            )
            point2 = GeometryEngine.project(
                point2,
                SpatialReference.create(SpatialReference.WKID_WGS84),
                getSpatialReference()
            ) as Point
            val envelope = Envelope(point2.x, point1.y, point1.x, point2.y)
            arcGISMapView.setExtent(
                envelope,
                ViewUtils.dip2px(getContext(), cameraBoundsUpdate.paddingRectF!!.left).toInt(),
                animate
            )
        } else if (cameraUpdate is RotateUpdate) {
            val rotateUpdate: RotateUpdate = cameraUpdate
            var angle = 0.0
            if (rotateUpdate.rotate > 0 && rotateUpdate.rotate <= 180) {
                angle = -rotateUpdate.rotate
            } else if (rotateUpdate.rotate > 180) {
                angle = 360 - rotateUpdate.rotate
            }
            val center: ILatLng = rotateUpdate.center
            val centerPoint = GeometryEngine.project(
                Point(center.longitude, center.latitude),
                SpatialReference.create(SpatialReference.WKID_WGS84),
                getSpatialReference()
            ) as Point
            arcGISMapView.setRotationAngle(angle, centerPoint, animate)
        }
        arcGISMapView.postDelayed({
            if (onCameraChangeListener != null) {
                onCameraChangeListener!!.onCameraChanged(
                    getCameraPosition().target!!,
                    getCameraPosition().zoom.toFloat(), getRotate()
                )
            }
        }, 500)
    }

    private fun getMarkerSymbol(options: IMarkerOptions): MarkerSymbol {
        val markerSymbol: MarkerSymbol
        if (options.icon != null) {
            markerSymbol = PictureMarkerSymbol(BitmapDrawable(options.icon!!.bitmap))
        } else {
            markerSymbol = SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE)
        }
        markerSymbol.setAngle(options.rotate)
        var markerWidth = 0f
        var markerHeight = 0f
        if (options.icon != null) {
            markerWidth = options.icon!!.bitmap.getWidth().toFloat()
            markerHeight = options.icon!!.bitmap.getHeight().toFloat()
        }
        markerSymbol.setOffsetX(markerWidth * (options.anchorX - 0.5f))
        markerSymbol.setOffsetY(markerHeight * (options.anchorY - 0.5f))
        return markerSymbol
    }


    /**
     * mapView 截图
     *
     * @param v
     * @return
     */
    private fun getViewBitmap(v: MapView): Bitmap {
        v.clearFocus()
        v.setPressed(false)
        // 能画缓存就返回false
        val willNotCache = v.willNotCacheDrawing()
        v.setWillNotCacheDrawing(false)
        val color = v.drawingCacheBackgroundColor
        v.drawingCacheBackgroundColor = 0
        if (color != 0) {
            v.destroyDrawingCache()
        }
        v.buildDrawingCache()
        var cacheBitmap: Bitmap? = null
        while (cacheBitmap == null) {
            cacheBitmap = v.getDrawingMapCache(
                0f, 0f, v.width,
                v.height
            )
        }
        val bitmap = Bitmap.createBitmap(cacheBitmap)
        // Restore the view
        v.destroyDrawingCache()
        v.setWillNotCacheDrawing(willNotCache)
        v.drawingCacheBackgroundColor = color
        return bitmap
    }

}
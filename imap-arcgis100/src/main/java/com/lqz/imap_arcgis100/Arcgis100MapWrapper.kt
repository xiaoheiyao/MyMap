package com.lqz.imap_arcgis100

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.Polygon
import com.esri.arcgisruntime.geometry.PolygonBuilder
import com.esri.arcgisruntime.geometry.PolylineBuilder
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.layers.Layer
import com.esri.arcgisruntime.layers.ServiceImageTiledLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.MarkerSymbol
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
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
import com.lqz.imap.utils.ViewUtils
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.pow

class Arcgis100MapWrapper(private val arcGISMapView: MapView) : IMapDelegate {

    private var arcgisUiSetting: Arcgis100UiSetting = Arcgis100UiSetting(arcGISMapView, this)
    private var arcgisProjection: Arcgis100Projection = Arcgis100Projection(arcGISMapView, this)


    private var graphicsOverlay: GraphicsOverlay? = null
    private var serviceImageTiledLayer: ArcGISTiledLayer? = null

    private var onMapLoadedListener: OnMapLoadedListener? = null
    private var onMapMarkerClickListener: OnMapMarkerClickListener? = null
    private var onMapMarkerLongClickListener: OnMapMarkerLongClickListener? = null
    private var onCameraChangeListener: OnCameraChangeListener? = null
    private var onMapClickListener: OnMapClickListener? = null
    private var onMapLongClickListener: OnMapLongClickListener? = null

    private var markerDragMode = DRAG_MODE_LONG_PRESS

    private var language: String = LANGUAGE_ZH

    private var mapType: MapType? = null

    //点
    private val mMapMarkerHashMap: HashMap<Graphic, Arcgis100Marker> = HashMap()

    //圆形区域
    private val circleHashMap: HashMap<Graphic, Arcgis100Circle> = HashMap()

    fun getCircleHashMap(): HashMap<Graphic, Arcgis100Circle> {
        return circleHashMap
    }

    init {
        if (graphicsOverlay == null) {
            graphicsOverlay = GraphicsOverlay()
            arcGISMapView.graphicsOverlays.add(graphicsOverlay)
        }

        // 创建 ArcGISMap
        val arcGISMap = ArcGISMap(Basemap.Type.IMAGERY, 32.056295, 118.195800, 14)

        arcGISMap.addLoadStatusChangedListener { loadStatusChangedEvent ->
            when (loadStatusChangedEvent.newLoadStatus) {
                LoadStatus.LOADED -> {
                    // 地图加载完成
                    onMapLoadedListener?.onMapLoaded()
                }

                LoadStatus.FAILED_TO_LOAD -> {
                    // 地图加载失败时进行处理
                    Log.e("ArcGIS", "Map failed to load")
                }

                else -> {
                    // 处理其他加载状态
                }
            }
        }

        // 将 ArcGISMap 设置到 MapView
        arcGISMapView.map = arcGISMap


    }

    override fun getMapImpType(): MapImpType {
        return MapImpType.MAP_IMP_TYPE_ARCGIS
    }

    override fun setMapType(mapType: MapType) {
        if (serviceImageTiledLayer != null && containsLayer(serviceImageTiledLayer!!)) {
            arcGISMapView.map.operationalLayers.remove(serviceImageTiledLayer)
        }

        if (mapType === MapType.MAP_TYPE_USER_DEFIED) {
            serviceImageTiledLayer =  ArcGISTiledLayer("你的图层 URL")
            arcGISMapView.map.operationalLayers.add(serviceImageTiledLayer)
        }
        serviceImageTiledLayer?.minScale = getScaleByZoomLevel(1)
        serviceImageTiledLayer?.maxScale = getScaleByZoomLevel(22)
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
        TODO("Not yet implemented")
    }

    override fun getCameraPosition(): ICameraPosition {
        val builder: ICameraPosition.Builder = ICameraPosition.Builder()
        builder.bearing(getRotate())

        return builder.build()
    }

    override fun moveCamera(cameraUpdate: ICameraUpdate) {
        moveCamera(cameraUpdate, 0, false)
    }

    fun moveCamera(iLatLng: ILatLng) {
//        // 根据层级计算缩放比例（假设每个层级的缩放比例为 2）
//        val scale = Math.pow(2.0, level.toDouble()) * 10000 // 调整基数 10000 以匹配你的地图
        // 创建一个点对象
        val targetPoint = Point(iLatLng.longitude, iLatLng.latitude, SpatialReferences.getWgs84())
        arcGISMapView.setViewpointAsync(Viewpoint(targetPoint, 10000.0)) // 10000f 是缩放级别
    }

    override fun animateCamera(cameraUpdate: ICameraUpdate) {
        moveCamera(cameraUpdate, 200, true)
    }

    override fun animateCamera(cameraUpdate: ICameraUpdate, duration: Long) {

    }

    override fun getMaxZoom(): Float {
        return 19f
    }

    override fun getMinZoom(): Float {
        return 0f
    }

    override fun addMarker(options: IMarkerOptions): IMarkerDelegate {
        val markerSymbol: PictureMarkerSymbol
        val bitmapDrawable = BitmapDrawable(getContext().resources, options.icon!!.bitmap)
        markerSymbol = PictureMarkerSymbol.createAsync(bitmapDrawable).get().apply {
            offsetX = ViewUtils.px2dip(
                getContext(),
                options.icon!!.bitmap.getWidth().toFloat() * (options.anchorX - 0.5f)
            )
            offsetY = ViewUtils.px2dip(
                getContext(),
                options.icon!!.bitmap.getHeight().toFloat() * (options.anchorY - 0.5f)
            )
        }
        markerSymbol.setAngle(options.rotate)
        val point = Point(options.position.longitude, options.position.latitude)
        val graphic = Graphic(point, markerSymbol) //todo 设置点的显示优先级
        graphic.zIndex = options.zIndex.toInt()
        graphicsOverlay?.graphics?.add(graphic)
        val arcgisMarker = Arcgis100Marker(graphic, this, options)
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
        val markerList: MutableList<IMarkerDelegate> =
            ArrayList()

        optionsList.forEach {
            markerList.add(addMarker(it))
        }
        callback(markerList)
    }

    override fun addPolyline(options: IPolylineOptions): IPolylineDelegate {

        val polylineBuilder = PolylineBuilder(SpatialReferences.getWgs84())
        options.points.forEach {
            val point = Point(it.longitude, it.latitude)
            polylineBuilder.addPoint(point)
        }
        // 创建线段样式
        val lineSymbol =
            SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, options.color, options.width)

        if (options.isDottedLine) {
            lineSymbol.setStyle(SimpleLineSymbol.Style.DOT)
        } else {
            lineSymbol.setStyle(SimpleLineSymbol.Style.SOLID)
        }
        // 创建 Graphic 对象
        val lineGraphic = Graphic(polylineBuilder.toGeometry(), lineSymbol)
        lineGraphic.zIndex = options.zIndex.toInt()
        // 将线段添加到图层
        graphicsOverlay?.graphics?.add(lineGraphic)

        return Arcgis100Polyline(lineGraphic, this)
    }

    override fun addPolylines(optionsList: List<IPolylineOptions>): List<IPolylineDelegate> {
        val delegateList: MutableList<IPolylineDelegate> = ArrayList()
        optionsList.forEach {
            delegateList.add(addPolyline(it))
        }
        return delegateList
    }

    override fun addPolygon(options: IPolygonOptions): IPolygonDelegate {

        // 创建 PolygonBuilder 并添加点
        val polygonBuilder = PolygonBuilder(SpatialReferences.getWgs84())
        options.points.forEach {
            val point = Point(it.longitude, it.latitude)
            polygonBuilder.addPoint(point)
        }

        // 创建填充样式
        val fillSymbol = SimpleFillSymbol(
            SimpleFillSymbol.Style.SOLID,
            options.fillColor,
            SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, options.strokeColor, options.strokeWidth)
        )

        // 创建 Graphic 对象
        val graphic = Graphic(polygonBuilder.toGeometry(), fillSymbol)

        // 将面添加到图层
        graphicsOverlay?.graphics?.add(graphic)

        return Arcgis100Polygon(graphic, this)
    }

    override fun addPolygons(optionsList: List<IPolygonOptions>): List<IPolygonDelegate> {
        val polygonDelegateList: MutableList<IPolygonDelegate> = ArrayList()
        optionsList.forEach {
            polygonDelegateList.add(addPolygon(it))
        }
        return polygonDelegateList
    }

    override fun addCircle(options: ICircleOptions): ICircleDelegate {

        val centerPoint = Point(
            options.centerPoint.longitude,
            options.centerPoint.latitude,
            SpatialReferences.getWgs84()
        )
        val circleGeometry =
            GeometryEngine.buffer(centerPoint, options.radius.toDouble()) as Polygon
        val circleSymbol = SimpleFillSymbol(
            SimpleFillSymbol.Style.SOLID,
            options.fillColor,
            SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, options.strokeColor, options.strokeWidth)
        )

        val graphic = Graphic(circleGeometry, circleSymbol)
        //图层绘制结果
        graphicsOverlay!!.graphics.add(graphic)
        val arcgisCircle = Arcgis100Circle(this, graphic, options.centerPoint, options.radius)
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

    }

    override fun setOnMarkerDragListener(listener: OnMarkerDragListener) {

    }

    override fun setOnMapClickListener(listener: OnMapClickListener) {
        onMapClickListener = listener
    }

    override fun setOnMapLongClickListener(listener: OnMapLongClickListener) {
        onMapLongClickListener = listener
    }

    override fun getSnapshot(listener: OnSnapshotReadyListener) {

    }


    private fun getRotate(): Double {
        val mapRotate = arcGISMapView.mapRotation
        return if (mapRotate < 0) {
            abs(mapRotate)
        } else {
            360 - mapRotate
        }
    }

    fun getMapMarkerHashMap(): java.util.HashMap<Graphic, Arcgis100Marker> {
        return mMapMarkerHashMap
    }

    fun getArcGISMapView(): MapView {
        return arcGISMapView
    }

    fun getInfoWindowAdapter(): InfoWindowAdapter? {
        TODO("Not yet implemented")
    }

    fun getGraphicsOverlay(): GraphicsOverlay? {
        return graphicsOverlay
    }

    fun getContext(): Context {
        return arcGISMapView.context
    }

    fun setRotateEnable(enable: Boolean) {
        arcGISMapView.interactionOptions.isRotateEnabled = enable
    }

    fun getBaseTiledLayer(): ArcGISTiledLayer? {
        return serviceImageTiledLayer
    }


    fun getScaleByZoomLevel(zoom: Int): Double {
        return 591657527.591555 / 2.0.pow(zoom.toDouble())

    }

    fun getSpatialReference(): SpatialReference {
        return if (serviceImageTiledLayer != null) {
            serviceImageTiledLayer!!.spatialReference
        } else {
            SpatialReferences.getWgs84()
        }
    }

    private fun containsLayer(layer: Layer): Boolean {
        // 获取 ArcGISMapView 中的地图
        val arcGISMap = arcGISMapView.map as ArcGISMap

        // 获取图层列表
        val layers = arcGISMap.operationalLayers

        // 检查图层是否存在
        return layers.contains(layer)
    }


    private fun moveCamera(cameraUpdate: ICameraUpdate, duation: Long, animate: Boolean) {

    }

}
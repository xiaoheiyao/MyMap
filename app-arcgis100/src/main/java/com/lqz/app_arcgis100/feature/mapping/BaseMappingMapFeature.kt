package com.lqz.app_arcgis100.feature.mapping

import android.content.Context
import android.view.View
import com.lqz.app_arcgis100.R
import com.lqz.imap.core.internal.IMapDelegate
import com.lqz.imap.core.internal.IMapViewDelegate
import com.lqz.imap.core.internal.IMarkerDelegate
import com.lqz.imap.core.internal.IPolylineDelegate
import com.lqz.imap.model.ICameraUpdateFactory
import com.lqz.imap.model.ILatLng
import com.lqz.imap.model.ILatLngBounds
import com.lqz.imap.model.IMarkerOptions
import com.lqz.imap.model.IPolylineOptions
import com.lqz.imap.utils.IBitmapDescriptorFactory
import com.lqz.app_arcgis100.feature.util.FeatureUtil

open class BaseMappingMapFeature(
    val mMapView: IMapViewDelegate,
    val aMap: IMapDelegate
) {
    // 点 11-15
    protected val Z_INDEX_PLANE = 15 //飞机图标
    protected val Z_INDEX_RTK = 11 //rtk图标
    protected val Z_TITLE_POINT_MARKER = 14 //地块上的标题
    protected val Z_INDEX_GROUND_POINT_MARKER = 13 //地块上的点

    //线段 6-10
    protected val Z_INDEX_PATH_LINE = 10 //飞机轨迹路线
    protected val Z_INDEX_GROUND_LINE_MARKER = 8 //地块上的线

    //区域 1-5
    protected val Z_INDEX_GROUND_ZONE_MARKER = 3 //地块区域

    protected val Z_INDEX_GROUND = 2 //地块
    protected val Z_INDEX_GROUND_LINE = 1 //地块辅助线
//
//    private var compassView: CompassView? = null

    private var followNose: Boolean = false //跟随机头,设置地图角度跟随机头

    /*人的位置标识*/
    protected var personMarker: IMarkerDelegate? = null

    /*RTK位置标识*/
    private var rtkMarker: IMarkerDelegate? = null

    /*农机位置标识*/
    protected var planeMarker: IMarkerDelegate? = null

    /*航线轨迹*/
    protected var pathLine: IPolylineDelegate? = null


    //上次更新时间，对航线轨迹更新时间做限制，400毫秒以上更新一次
    private var lastUpdateTime: Long = 0

    //用于判断农机位置是否在原地不动，如果一直不动，也不更新轨迹
    var curLatLng = ILatLng(0.0, 0.0)

    /*是否显示运行轨迹*/
    private var isDrawPathLine = true

    protected fun isShowPathLine(): Boolean {
        return isDrawPathLine
    }

    init {

        //设置地图可旋转，必须设置，否则地图无法旋转
//        aMap.getUiSettings().setRotateGesturesEnabled(true)
//        aMap.getUiSettings().setScaleControlsEnabled(true)

//        aMap.setOnMarkerClickListener(object : OnMapMarkerClickListener {
//            override fun onMapMarkerClick(iMarkerDelegate: IMarkerDelegate?): Boolean {
//                val z = aMap.getCameraPosition().zoom.toFloat()
//                aMap.moveCamera(
//                    ICameraUpdateFactory.newLatLngZoom(
//                        iMarkerDelegate!!.getPosition(),
//                        z * -1 + 2
//                    )
//                )
//
//                return true
//            }
//        })
    }

    fun getMap(): IMapDelegate {
        return aMap
    }

    fun getContext(): Context {
        return mMapView.getContext()
    }


    /**
     * 显示机器，机头方向，以及机器的行驶轨迹
     * @param iLatLng 机器位置
     * @param angle 机器角度
     */
    fun showPlane(iLatLng: ILatLng, angle: Float) {

        //数据错误，不做任何处理
        if (angle < 0 || angle > 360) {
            return
        }

        if (iLatLng.latitude == 0.0 && iLatLng.longitude == 0.0) {
            return
        }

        if (planeMarker == null) {
            val markerOptions = IMarkerOptions()
            markerOptions.position = iLatLng
            markerOptions.draggable = false
            markerOptions.enable = false //不可点击
            markerOptions.zIndex = Z_INDEX_PLANE.toFloat()
            val planeView =
                View.inflate(mMapView.getContext(), R.layout.view_plane_marker, null)
            markerOptions.icon = IBitmapDescriptorFactory.fromView(planeView)

            planeMarker = aMap.addMarker(markerOptions)
        } else {
            planeMarker!!.setPosition(iLatLng)
        }
        //调整地图或者农机角度
        if (!followNose) {
            val rotate = getMap().getCameraPosition().bearing + angle
            planeMarker!!.setRotate(rotate.toFloat())
        } else {
            planeMarker!!.setRotate(0f)
            rotateMap(iLatLng, 360 - angle, true)
        }

        drawPathLine(iLatLng)
    }

    /**
     * 绘制航线轨迹
     */
    private fun drawPathLine(latLng: ILatLng) {
        if (!isShowPathLine() || FeatureUtil.isZeroLatLng(latLng)) {
            return
        }

        if (System.currentTimeMillis() - lastUpdateTime > 400 && !FeatureUtil.isZeroLatLng(latLng) && latLng != curLatLng) {

            lastUpdateTime = System.currentTimeMillis()
            curLatLng = latLng
            if (pathLine == null) {
                val polylineOptions = IPolylineOptions()
                polylineOptions.color =
                    (getContext().resources.getColor(R.color.color_spary_region_line))
                polylineOptions.isDottedLine = false
                polylineOptions.width = 1.5f
                polylineOptions.add(latLng)
                polylineOptions.zIndex = Z_INDEX_PATH_LINE.toFloat()
                pathLine = aMap.addPolyline(polylineOptions)
            } else {
                pathLine!!.add(latLng)
            }
        }
    }

    /**
     * 在地图上显示RTK的图标
     */
    fun showRtk(iLatLng: ILatLng) {
        if (rtkMarker == null) {
            val markerOptions = IMarkerOptions()
            markerOptions.position = iLatLng
            val personView =
                View.inflate(mMapView.getContext(), R.layout.view_rtk_point, null)
            markerOptions.icon = IBitmapDescriptorFactory.fromView(personView)
            markerOptions.draggable = false
            markerOptions.enable = false //不可点击
            markerOptions.zIndex = Z_INDEX_RTK.toFloat()
            rtkMarker = aMap.addMarker(markerOptions)
        } else {
            rtkMarker?.setPosition(iLatLng)
        }
    }

    /**
     * 移动到RTK的位置
     */
    fun moveToRtk() {
        if (rtkMarker != null) {
            val latLng: ILatLng = rtkMarker!!.getPosition()
            moveToPoint(latLng, 18f)
        }
    }

    /**
     * 移除RTK
     */
    fun removeRtk() {
        if (rtkMarker != null) {
            rtkMarker?.remove()
            rtkMarker = null
        }
    }


    /**
     * 移动到指定点
     */
    fun moveToPoint(latLng: ILatLng, zoomLevel: Float = aMap.getCameraPosition().zoom.toFloat()) {
        mMapView.getDelfView().post {
            aMap.moveCamera(ICameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
        }
    }

    /**
     * 移动到一个区域
     */
    fun moveToPointList(latLngList: List<ILatLng>?, padding: Int = 0) {
        moveToPointList(latLngList, padding, padding, padding, padding)
    }

    /**
     * 移动到一个区域
     */
    fun moveToPointList(
        latLngList: List<ILatLng>?,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int
    ) {
        if (latLngList.isNullOrEmpty()) {
            return
        }
        val builder = ILatLngBounds.Builder()
        val iCameraUpdate = ICameraUpdateFactory.newLatLngBounds(
            builder.includes(latLngList).build(),
            paddingLeft, paddingTop, paddingRight, paddingBottom
        )
        aMap.moveCamera(iCameraUpdate)
    }

    /**
     * 旋转地图
     */
    fun rotateMap(centerPoint: ILatLng, angle: Float, anim: Boolean = true) {
        mMapView.getDelfView().post {
            if (anim) {
                aMap.animateCamera(ICameraUpdateFactory.rotateChange(centerPoint, angle.toDouble()))
            } else {
                aMap.moveCamera(ICameraUpdateFactory.rotateChange(centerPoint, angle.toDouble()))
            }
        }
    }


    private fun getString(res: Int): String {
        return getContext().getString(res)
    }

    fun destroy() {

//        if (null != compassView) {
//            compassView!!.setOnClickListener(null)
//            compassView = null
//        }
    }

    open fun clearAll() {

        removeRtk() //移除RTK位置

    }

}
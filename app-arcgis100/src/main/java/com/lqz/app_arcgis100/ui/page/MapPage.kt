package com.lqz.app_arcgis100.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.MapView
import com.lqz.imap_arcgis100.tdt.LayerInfoFactory
import com.lqz.imap_arcgis100.tdt.TianDiTuLayer
import com.lqz.tianditu.TianDiTuLayerTypes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MapPage() {
    Arcgis100MapView()
}

@Composable
fun Arcgis100MapView() {

    val lifecycleOwner = LocalLifecycleOwner.current
    // 创建一个可变的引用，用于持有 MapView 实例
    var mapView: MapView? = null

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            mapView = MapView(it)
//            val map = ArcGISMap(Basemap.Type.TOPOGRAPHIC, 32.056295, 118.195800, 16)
//            mapView?.map = map
            addTDT(mapView!!)
            val centralPoint = Point(116.41, 39.902);
//            val map = ArcGISMap(Basemap.Type.TOPOGRAPHIC, 32.056295, 118.195800, 16)
//            mapView?.map = map
            mapView?.setViewpointCenterAsync(centralPoint, 400000.0) //设置地图中心点和初始放缩比
            mapView?.setAttributionTextVisible(false); //隐藏Esri logo

            mapView!!
        },
        update = {
            mapView?.resume()
        })
    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                // 当生命周期进入暂停状态时调用 MapView 的 onPause()
                mapView?.pause()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                // 当生命周期进入销毁状态时调用 MapView 的 onDestroy()
                mapView?.dispose()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}

fun addTDT(mapView: MapView) {
    val layerInfo = LayerInfoFactory.getLayerInfo(TianDiTuLayerTypes.TIANDITU_VECTOR_2000)
    val info = layerInfo.tileInfo
    val fullExtent = layerInfo.fullExtent
    val layer = TianDiTuLayer(info, fullExtent)
    layer.layerInfo = layerInfo

//    val layerInfoCva = LayerInfoFactory.getLayerInfo(TianDiTuLayerTypes.TIANDITU_IMAGE_ANNOTATION_CHINESE_MERCATOR)
//    val infoCva = layerInfoCva.tileInfo
//    val fullExtentCva = layerInfoCva.fullExtent
//    val layerCva = TianDiTuLayer(infoCva, fullExtentCva)
//    layerCva.layerInfo = layerInfoCva

    val map = ArcGISMap().apply {
        basemap.baseLayers.add(layer)
//        basemap.baseLayers.add(layerCva)
    }
    mapView.map = map
}





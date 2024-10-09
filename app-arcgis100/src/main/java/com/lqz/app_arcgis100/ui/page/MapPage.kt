package com.lqz.app_arcgis100.ui.page

import android.content.Context
import android.os.Bundle
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
import com.lqz.imap.core.internal.IMapDelegate
import com.lqz.imap.core.internal.IMapViewDelegate
import com.lqz.imap.core.listener.OnMapLoadedListener
import com.lqz.imap.model.MapType
import com.lqz.imap_arcgis100.Arcgis100MapView
import com.lqz.imap_arcgis100.tdt.LayerInfoFactory
import com.lqz.imap_arcgis100.tdt.TianDiTuLayer
import com.lqz.tianditu.TianDiTuLayerTypes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MapPage(
    savedInstanceState: Bundle?,
) {
//    Arcgis100MapView1()
    WtMapView(savedInstanceState)
}

@Composable
fun Arcgis100MapView1() {

    val lifecycleOwner = LocalLifecycleOwner.current
    // 创建一个可变的引用，用于持有 MapView 实例
    var mapView: MapView? = null

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            mapView = MapView(it)
            val map = ArcGISMap(Basemap.Type.IMAGERY, 32.056295, 118.195800, 14)
            mapView?.map = map
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



@Composable
fun WtMapView(
    savedInstanceState: Bundle?,
//    mapFeatureCallback: (IMappingMapFeature) -> Unit = {},
    mapCallback: (IMapDelegate) -> Unit = {},
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            val mapView = com.lqz.imap.core.MapView(it)
            mapView.initialize(getIMpViewImp(it)) //初始化地图控件

            mapView.onCreate(savedInstanceState) //创建地图控件

            mapView.getIMapView()?.getMapAsync { map ->
                /*设置地图加载监听器*/
                map.setOnMapLoadedListener(object : OnMapLoadedListener {
                    override fun onMapLoaded() {
//                        mapFeatureCallback(MappingMapFeature(mapView.getIMapView(), map))
                        mapCallback(map)
                    }

                })
                showMapType(map) //设置地图类型
            }
            mapView
        },
        update = {
            it.onResume()
        })
}

/**
 * 设置地图类型
 * @param map 地图委托类
 */
fun showMapType(map: IMapDelegate?) {
    map?.setMapType(MapType.MAP_TYPE_USER_DEFIED)
}

private fun getIMpViewImp(context: Context): IMapViewDelegate {
    return Arcgis100MapView(context)
}





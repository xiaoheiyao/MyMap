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
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.MapView

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
            val map = ArcGISMap(Basemap.Type.TOPOGRAPHIC, 32.056295, 118.195800, 16)
            mapView?.map = map
            mapView?.setAttributionTextVisible(false)
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


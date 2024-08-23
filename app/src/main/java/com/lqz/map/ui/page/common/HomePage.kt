package com.lqz.map.ui.page.common

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.lqz.imap.core.MapView
import com.lqz.imap.core.internal.IMapDelegate
import com.lqz.imap.core.internal.IMapViewDelegate
import com.lqz.imap.core.listener.OnMapLoadedListener
import com.lqz.imap.model.MapType
import com.lqz.imap_arcgis10.Arcgis10MapView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePage(
    navCtrl: NavHostController,
    savedInstanceState: Bundle?,
) {
    WtMapView(savedInstanceState)
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
            val mapView = MapView(it)
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
    return Arcgis10MapView(context)
}
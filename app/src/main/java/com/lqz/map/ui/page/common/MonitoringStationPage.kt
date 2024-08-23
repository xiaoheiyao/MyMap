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
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MonitoringStationPage(
    navCtrl: NavHostController,
    savedInstanceState: Bundle?,
) {
    WtMapView(savedInstanceState)
}


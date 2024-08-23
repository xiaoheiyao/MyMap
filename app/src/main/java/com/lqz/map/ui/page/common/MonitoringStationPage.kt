package com.lqz.map.ui.page.common

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
import androidx.navigation.NavHostController


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MonitoringStationPage(
    navCtrl: NavHostController,
    savedInstanceState: Bundle?,
) {
    WtMapView(savedInstanceState)
}




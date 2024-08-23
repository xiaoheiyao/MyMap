package com.lqz.map.ui.page.common

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun ProfilePage (
    navCtrl: NavHostController,
    savedInstanceState: Bundle?,
) {
    WtMapView(savedInstanceState)
}
package com.lqz.map.ui.page.common

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lqz.map.ui.widgets.BottomNavBarView


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppScaffold(savedInstanceState: Bundle?) {
    val navCtrl = rememberNavController()
    val navBackStackEntry by navCtrl.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        bottomBar = {
            when (currentDestination?.route) {
                RouteName.HOME -> BottomNavBarView(navCtrl = navCtrl)
                RouteName.MONITORING_STATION -> BottomNavBarView(navCtrl = navCtrl)
                RouteName.PROFILE -> BottomNavBarView(navCtrl = navCtrl)
            }
        },
        content = { paddingValues ->
//            var homeIndex = remember { 0 }
//            var categoryIndex = remember { 0 }

            NavHost(
                modifier = Modifier
                    .padding(paddingValues)  // 应用内边距
                    .background(MaterialTheme.colorScheme.background),
                navController = navCtrl,
                startDestination = RouteName.HOME
            ) {
                //首页
                composable(route = RouteName.HOME) {
                    HomePage(navCtrl, savedInstanceState)
                }

                //分类
                composable(route = RouteName.MONITORING_STATION) {
                    MonitoringStationPage(navCtrl, savedInstanceState)
                }

                //我的
                composable(route = RouteName.PROFILE) {
                    ProfilePage(navCtrl, savedInstanceState)
                }

            }
        },
    )
}
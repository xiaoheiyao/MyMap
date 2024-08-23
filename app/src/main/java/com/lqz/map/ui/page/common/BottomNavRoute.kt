package com.lqz.map.ui.page.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.lqz.map.R

sealed class BottomNavRoute(
    var routeName: String,
    @StringRes var stringId: Int,
    var icon: ImageVector
) {
    object Home: BottomNavRoute(RouteName.HOME, R.string.home, Icons.Default.Home)
    object MonitoringStation: BottomNavRoute(RouteName.MONITORING_STATION, R.string.category, Icons.Default.Menu)
//    object Collection: BottomNavRoute(RouteName.COLLECTION, R.string.collection, Icons.Default.Favorite)
    object Profile: BottomNavRoute(RouteName.PROFILE, R.string.profile, Icons.Default.Person)
}
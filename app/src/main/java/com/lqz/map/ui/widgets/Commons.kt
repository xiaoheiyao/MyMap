package com.lqz.map.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lqz.map.ui.page.common.BottomNavRoute


@Composable
fun BottomNavBarView(navCtrl: NavHostController) {
    val bottomNavList = listOf(
        BottomNavRoute.Home,
        BottomNavRoute.MonitoringStation,
        BottomNavRoute.Profile,
    )

    val currentRoute = navCtrl.currentBackStackEntryAsState().value?.destination?.route

    BottomNavigation(
        modifier = Modifier
//            .background(AppTheme.colors.themeUi)
            .height(60.dp), // 设置高度为80dp，可以根据需要调整
        backgroundColor = MaterialTheme.colorScheme.background// 设置整个BottomNavigation的背景色
    ) {
        val navBackStackEntry by navCtrl.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomNavList.forEach { screen ->
            val isSelected = currentRoute == screen.routeName
            BottomNavigationItem(
                modifier = Modifier.background(Color.White),
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(screen.stringId)) },
//                selectedContentColor = wt_selected_color,
//                unselectedContentColor = wt_unselected_color,
                selected = currentDestination?.hierarchy?.any { it.route == screen.routeName } == true,
                onClick = {

                    if (currentDestination?.route != screen.routeName) {
                        navCtrl.navigate(screen.routeName) {
                            popUpTo(navCtrl.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                })
        }

    }
}

/**
 * @param title 标题
 * @param content 内容
 * @param callback 按钮回调
 */
@Composable
fun ProfileItem(title: String, content: String, isClick: Boolean = true, callback: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp),
        ) {
            Text(text = title, fontSize = 15.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = content, fontSize = 12.sp, color = Color.Gray)
        }
        Box(
            modifier = Modifier.padding(end = 20.dp)
            // 这里设置距离
        ) {
            Icon(
                modifier = Modifier
                    .size(30.dp)
                    .clickable(enabled = isClick) { callback() },
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
//            painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "返回",
                tint = Color.Black,
            )
        }
    }
}
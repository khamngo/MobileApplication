package com.example.foodorderingapplication.view

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.foodorderingapplication.model.BottomNavItem

@Composable
fun BottomNavBar(navController: NavController, items: List<BottomNavItem> = listOf(
    BottomNavItem.Home,
    BottomNavItem.Menu,
    BottomNavItem.Notification,
    BottomNavItem.Profile
)) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedItem = items.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0

    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    // Tùy chỉnh kích thước chữ dựa trên kích thước màn hình
    val fontSize = when {
        screenWidthDp < 360 -> 10.sp
        screenWidthDp < 400 -> 11.sp
        else -> 12.sp
    }

    NavigationBar(containerColor = Color.White) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = {
                    Text(
                        item.title,
                        fontSize = fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = selectedItem == index,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFFC107),
                    selectedTextColor = Color(0xFFFFC107),
                    indicatorColor = Color.White
                )
            )
        }
    }
}

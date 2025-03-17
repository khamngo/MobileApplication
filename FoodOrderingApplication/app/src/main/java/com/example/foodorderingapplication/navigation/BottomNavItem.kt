package com.example.foodorderingapplication.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    object Menu : BottomNavItem("menu", "Menu", Icons.Filled.Menu)
    object Notification : BottomNavItem("notifications", "Notifications", Icons.Filled.Notifications)
    object Profile : BottomNavItem("profile", "Profile", Icons.Filled.Person)
}

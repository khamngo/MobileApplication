package com.example.foodorderingapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodorderingapplication.home.HomeScreen
import com.example.foodorderingapplication.menu.MenuScreen
import com.example.foodorderingapplication.navigation.BottomNavBar
import com.example.foodorderingapplication.navigation.BottomNavItem
import com.example.foodorderingapplication.notifications.NotificationScreen
import com.example.foodorderingapplication.profile.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = Color(0xFFF7F7F7),
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues)) {
            NavigationGraph(navController)
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.Menu.route) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Menu.route) { MenuScreen() }
        composable(BottomNavItem.Notification.route) { (NotificationScreen()) }
        composable(BottomNavItem.Profile.route) { ProfileScreen() }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MainScreen()
}
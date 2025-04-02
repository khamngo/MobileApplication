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
import com.example.foodorderingapplication.view.HomeScreen
import com.example.foodorderingapplication.view.MenuScreen
import com.example.foodorderingapplication.view.BottomNavBar
import com.example.foodorderingapplication.view.BottomNavItem
import com.example.foodorderingapplication.view.NotificationScreen
import com.example.foodorderingapplication.view.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            NavigationGraph()
        }
    }
}

//@Composable
//fun MainScreen() {
//    val navController = rememberNavController()
//
//    Scaffold(
//        containerColor = Color(0xFFF7F7F7),
//        bottomBar = { BottomNavBar(navController) }
//    ) { paddingValues ->
//
//        Box(modifier = Modifier.padding(paddingValues)) {
//            NavigationGraph(navController)
//        }
//    }
//}

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = BottomNavItem.Menu.route) {
        composable(BottomNavItem.Home.route) { HomeScreen(navController = navController) }
        composable(BottomNavItem.Menu.route) { MenuScreen( navController = navController) }
        composable(BottomNavItem.Notification.route) { (NotificationScreen(navController = navController)) }
        composable(BottomNavItem.Profile.route) { ProfileScreen(navController = navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NavigationGraph()
}
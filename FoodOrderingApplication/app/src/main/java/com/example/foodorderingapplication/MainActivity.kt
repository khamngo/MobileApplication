package com.example.foodorderingapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodorderingapplication.view.AddShippingScreen
import com.example.foodorderingapplication.view.BottomNavItem
import com.example.foodorderingapplication.view.CartScreen
import com.example.foodorderingapplication.view.CategoryScreen
import com.example.foodorderingapplication.view.CheckoutScreen
import com.example.foodorderingapplication.view.FoodDetailScreen
import com.example.foodorderingapplication.view.HomeScreen
import com.example.foodorderingapplication.view.MenuScreen
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

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = BottomNavItem.Menu.route) {
        composable(BottomNavItem.Home.route) { HomeScreen(navController = navController) }
        composable(BottomNavItem.Menu.route) { MenuScreen( navController = navController) }
        composable(BottomNavItem.Notification.route) { (NotificationScreen(navController = navController)) }
        composable(BottomNavItem.Profile.route) { ProfileScreen(navController = navController) }

        composable("cart") { CartScreen(navController = navController) }
        composable("checkout") { CheckoutScreen(navController = navController) }
        composable("add_shopping_address") { AddShippingScreen(navController = navController) }
        composable("category/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            CategoryScreen(navController = navController, name = name) }

        composable("detail/{foodId}") { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId")?.toIntOrNull() ?: 0
            FoodDetailScreen(navController = navController, foodId = foodId)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NavigationGraph()
}
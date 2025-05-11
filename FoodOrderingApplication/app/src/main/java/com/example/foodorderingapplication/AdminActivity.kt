package com.example.foodorderingapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodorderingapplication.model.BottomNavItem
import com.example.foodorderingapplication.ui.theme.FoodOrderingApplicationTheme
import com.example.foodorderingapplication.view.admin.AddFoodScreen
import com.example.foodorderingapplication.view.admin.AdminAccountScreen
import com.example.foodorderingapplication.view.admin.CreateNewAccountScreen
import com.example.foodorderingapplication.view.admin.EditFoodScreen
import com.example.foodorderingapplication.view.admin.FoodDetailScreen
import com.example.foodorderingapplication.view.admin.HomeAdminScreen
import com.example.foodorderingapplication.view.admin.OrderDetailScreen
import com.example.foodorderingapplication.view.admin.OrderListScreen
import com.example.foodorderingapplication.view.admin.ProfileAdminScreen
import com.example.foodorderingapplication.view.admin.RevenueScreen
import com.example.foodorderingapplication.view.admin.ReviewDetailScreen
import com.example.foodorderingapplication.view.admin.ReviewListScreen
import com.example.foodorderingapplication.view.admin.UserManagementScreen
import com.example.foodorderingapplication.view.home.SignInScreen

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodOrderingApplicationTheme {
                NavigationGraphAdmin()
            }
        }
    }
}

@Composable
fun NavigationGraphAdmin() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeAdminScreen(navController = navController) }
        composable(BottomNavItem.Profile.route) { ProfileAdminScreen(navController = navController) }

        composable("add_food") { AddFoodScreen(navController = navController) }
        composable("edit_food/{foodId}") {backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
            EditFoodScreen(navController = navController, foodId = foodId)
        }
        composable("food_detail/{foodId}") {backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
            FoodDetailScreen(navController = navController, foodId = foodId)
        }
        composable("order") { OrderListScreen(navController = navController) }

        composable("order_detail/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(navController = navController, orderId = orderId)
        }
        composable("revenue") { RevenueScreen(navController = navController) }
        composable("review") { ReviewListScreen(navController = navController) }

        composable("review/{foodId}") { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
            ReviewDetailScreen(navController = navController, foodId = foodId)
        }
        composable("new_account") { CreateNewAccountScreen(navController = navController) }
        composable("admin_account") { AdminAccountScreen(navController = navController) }
        composable("user_management") { UserManagementScreen(navController = navController) }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview1() {
    NavigationGraphAdmin()
}
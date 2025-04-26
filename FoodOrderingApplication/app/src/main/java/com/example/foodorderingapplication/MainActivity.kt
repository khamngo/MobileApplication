package com.example.foodorderingapplication

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodorderingapplication.model.BottomNavItem
import com.example.foodorderingapplication.view.home.HomeScreen
import com.example.foodorderingapplication.view.home.IntroScreen
import com.example.foodorderingapplication.view.home.LoginScreen
import com.example.foodorderingapplication.view.home.SignUpScreen
import com.example.foodorderingapplication.view.home.ThanksScreen
import com.example.foodorderingapplication.view.menu.AddShippingScreen
import com.example.foodorderingapplication.view.menu.CartScreen
import com.example.foodorderingapplication.view.menu.CategoryScreen
import com.example.foodorderingapplication.view.menu.CheckoutScreen
import com.example.foodorderingapplication.view.menu.FoodDetailScreen
import com.example.foodorderingapplication.view.menu.MenuScreen
import com.example.foodorderingapplication.view.menu.ThankYouScreen
import com.example.foodorderingapplication.view.notification.NotificationScreen
import com.example.foodorderingapplication.view.profile.MyAccountScreen
import com.example.foodorderingapplication.view.profile.MyReviewScreen
import com.example.foodorderingapplication.view.profile.OrderDetailScreen
import com.example.foodorderingapplication.view.profile.OrderListScreen
import com.example.foodorderingapplication.view.profile.PaymentMethodScreen
import com.example.foodorderingapplication.view.profile.ProfileScreen
import com.example.foodorderingapplication.view.profile.ReviewScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            NavigationGraph()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "intro") {
        composable(BottomNavItem.Home.route) { HomeScreen(navController = navController) }
        composable(BottomNavItem.Menu.route) { MenuScreen(navController = navController) }
        composable(BottomNavItem.Notification.route) { (NotificationScreen(navController = navController)) }
        composable(BottomNavItem.Profile.route) { ProfileScreen(navController = navController) }

        composable("intro") {
            IntroScreen(navController = navController)
        }

        composable("login") { LoginScreen(navController = navController) }
        composable("sign_up") {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignInClick = { navController.navigate("sign_in_screen") }
            )
        }

        composable("cart") { CartScreen(navController = navController) }
        composable("checkout") { CheckoutScreen(navController = navController) }
        composable("add_shopping_address") { AddShippingScreen(navController = navController) }
        composable("category/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            CategoryScreen(navController = navController, name = name)
        }

        composable(
            route = "food_detail/{foodId}",
            arguments = listOf(navArgument("foodId") { type = NavType.StringType })
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId")
            FoodDetailScreen(navController = navController, foodId = foodId)
        }

        composable("order") { OrderListScreen(navController = navController) }
        composable("order_detail") { OrderDetailScreen(navController = navController) }
        composable("my_account") { MyAccountScreen(navController = navController) }
        composable("payment_method") { PaymentMethodScreen(navController = navController) }
        composable("my_review") { MyReviewScreen(navController = navController) }
        composable("thank_you") { ThankYouScreen(navController = navController) }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NavigationGraph()
}
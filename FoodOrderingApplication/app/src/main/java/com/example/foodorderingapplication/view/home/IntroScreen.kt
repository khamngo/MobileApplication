package com.example.foodorderingapplication.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.BottomNavItem
import com.example.foodorderingapplication.ui.theme.MograFont
import com.example.foodorderingapplication.viewmodel.AuthViewModel

@Composable
fun IntroScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val navigateTo by authViewModel.navigateTo.collectAsState()

    LaunchedEffect(navigateTo) {
        navigateTo?.let { route ->
            when (route) {
                "user_home" -> {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo("intro") { inclusive = true }
                        launchSingleTop = true
                    }
                }

                "login" -> {
                    navController.navigate("login") {
                        popUpTo("intro") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_screen),
            contentDescription = "Splash Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.weight(1f / 3f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_title),
                        contentDescription = "Logo",
                        modifier = Modifier.size(140.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "KFoods",
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        fontFamily = MograFont,
                        color = White
                    )
                }

                Spacer(modifier = Modifier.weight(2f / 3f))

                Text(
                    text = "Welcome to KFoods",
                    fontSize = 20.sp,
                    color = White,
                    modifier = Modifier.offset(y = (-25).dp)
                )
            }
        }
    }
}



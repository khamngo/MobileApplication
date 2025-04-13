package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.ui.theme.MograFont
import com.example.foodorderingapplication.view.BottomNavBar
import com.example.foodorderingapplication.model.BottomNavItem
import com.example.foodorderingapplication.view.profile.ProfileSettingsSection
import com.example.foodorderingapplication.view.profile.SettingOption

@Composable
fun ProfileAdminScreen(
    navController: NavController,
    onNavigate: (String) -> Unit = {}
) {
    val bottomNavItems = listOf(BottomNavItem.Home, BottomNavItem.Profile)
    var userName = "Hoang Quy"
    var location = "Sai Gon, Viet Nam"
    var avatarUrl = "https://your-image-url.com/avatar.jpg"

    val settingOptions = listOf(
        SettingOption(Icons.Default.Person, "Admin", "admin"),
        SettingOption(Icons.Default.ShoppingCart, "Orders", "order"),
        SettingOption(Icons.Default.CreditCard, "Revenue", "revenue"),
        SettingOption(Icons.Default.StarBorder, "Reviews", "review"),
        SettingOption(Icons.Default.PersonAddAlt, "New account", "new_account")
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController, bottomNavItems) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F7F7))
                    .padding(paddingValues)
            ) {
                ProfileHeaderSection(userName, location, avatarUrl)
                Spacer(modifier = Modifier.height(24.dp))
                ProfileSettingsSection(
                    title = "Account Settings",
                    settings = settingOptions,
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
        }
    )
}

@Composable
fun ProfileHeaderSection(userName: String, location: String, avatarUrl: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
        )

        Text(
            text = userName,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            fontFamily = MograFont
        )

        Text(
            text = location,
            color = Color.Gray,
            fontSize = 16.sp
        )
    }
}


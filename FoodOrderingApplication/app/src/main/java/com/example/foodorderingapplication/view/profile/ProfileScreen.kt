package com.example.foodorderingapplication.view.profile

import android.R.attr.onClick
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.view.BottomNavBar
import com.example.foodorderingapplication.view.admin.ProfileHeaderSection
import com.example.foodorderingapplication.viewmodel.MyAccountViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController, viewModel: MyAccountViewModel = viewModel()) {
    val userState by viewModel.user.collectAsState()

    val settingOptions = listOf(
        SettingOption(Icons.Default.Person, "My Account", "my_account"),
        SettingOption(Icons.Default.ShoppingCart, "My Orders", "order"),
        SettingOption(Icons.Default.AddCard, "Payment Method", "payment_method"),
        SettingOption(Icons.Default.StarBorder, "My Reviews", "my_review"),
    )

    Scaffold(bottomBar = { BottomNavBar(navController) }, content = { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(paddingValues)
        ) {
            ProfileHeaderSection(userState.username, userState.email,
                userState.avatarUrl.toString()
            )
            Spacer(modifier = Modifier.height(24.dp))

            ProfileSettingsSection(
                title = "Account Settings",
                settings = settingOptions,
                onNavigate = { route -> navController.navigate(route) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            ProfileSettingsSection(
                title = "General Settings",
                settings = listOf(SettingOption(Icons.Default.Info, "About us", "about_as")),
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    })
}


@Composable
fun ProfileSettingsSection(
    title: String,
    settings: List<SettingOption>,
    onNavigate: (String) -> Unit
) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFFFFC107),
        modifier = Modifier.padding(start = 16.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(modifier = Modifier.background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            settings.forEach { setting ->
                SettingItem(
                    icon = setting.icon,
                    title = setting.title
                ) {
                    onNavigate(setting.route)
                }
            }
        }
    }
}

@Composable
fun SettingItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick).padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Icon",
            modifier = Modifier.size(24.dp),
            tint = Color.Gray,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Next",
            tint = Color.Gray,
        )
    }
}

data class SettingOption(
    val icon: ImageVector,
    val title: String,
    val route: String
)

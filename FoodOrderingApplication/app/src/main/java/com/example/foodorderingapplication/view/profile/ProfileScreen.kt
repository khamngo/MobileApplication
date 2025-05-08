package com.example.foodorderingapplication.view.profile

import android.app.Activity
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodorderingapplication.MainActivity
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.SettingOption
import com.example.foodorderingapplication.ui.theme.MograFont
import com.example.foodorderingapplication.view.BottomNavBar
import com.example.foodorderingapplication.viewmodel.MyAccountViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: MyAccountViewModel = viewModel()) {
    val userState by viewModel.user.collectAsState()

    val settingOptions = listOf(
        SettingOption(Icons.Default.Person, "My Account", "my_account"),
        SettingOption(Icons.Default.ShoppingCart, "My Orders", "order"),
        SettingOption(Icons.Default.Star, "My Reviews", "my_review"),
        SettingOption(Icons.Default.Favorite, "My Favorite", "my_favorite"),
        SettingOption(Icons.Default.AddCard, "Payment Method", "payment_method"),
    )

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    Scaffold(bottomBar = { BottomNavBar(navController) }, content = { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(paddingValues).verticalScroll(rememberScrollState())
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
                settings = listOf(SettingOption(Icons.Default.Info, "About us", "about_us")),
                onNavigate = { route -> navController.navigate(route) }
            )

            Spacer(modifier = Modifier.weight(1f))
            var showLogoutDialog by remember { mutableStateOf(false) }
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Confirm logout") },
                    text = { Text("Are you sure you want to log out of the application?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showLogoutDialog = false
                            viewModel.logout()
                            navController.navigate("login") {
                                popUpTo("intro") { inclusive = true }
                            }
                        }) {
                            Text("Agree")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            // Logout Button
            Button(
                onClick = {
                        showLogoutDialog  =true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Log out", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    })
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
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .crossfade(true)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .error(R.drawable.ic_placeholder_avatar)
                .build(),
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



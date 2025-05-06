package com.example.foodorderingapplication.view.admin

import android.app.Activity
import android.content.Intent
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.MainActivity
import com.example.foodorderingapplication.ui.theme.MograFont
import com.example.foodorderingapplication.view.BottomNavBar
import com.example.foodorderingapplication.model.BottomNavItem
import com.example.foodorderingapplication.model.SettingOption
import com.example.foodorderingapplication.view.profile.ProfileHeaderSection
import com.example.foodorderingapplication.view.profile.ProfileSettingsSection
import com.example.foodorderingapplication.viewmodel.MyAccountViewModel

@Composable
fun ProfileAdminScreen(
    navController: NavController,
    viewModel: MyAccountViewModel = viewModel()
) {
    val userState by viewModel.user.collectAsState()
    val context = LocalContext.current
    val bottomNavItems = listOf(BottomNavItem.Home, BottomNavItem.Profile)

    val settingOptions = listOf(
        SettingOption(Icons.Default.Person, "Admin", "admin_account"),
        SettingOption(Icons.Default.ShoppingCart, "Orders", "order"),
        SettingOption(Icons.Default.CreditCard, "Revenue", "revenue"),
        SettingOption(Icons.Default.StarBorder, "Reviews", "review"),
        SettingOption(Icons.Default.PersonAddAlt, "New account", "new_account")
    )

    // State để hiển thị AlertDialog xác nhận đăng xuất
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavBar(navController, bottomNavItems) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F7F7))
                    .padding(paddingValues)
            ) {

                ProfileHeaderSection(
                    userState.username,
                    userState.email,
                    userState.avatarUrl.toString()
                )
                Spacer(modifier = Modifier.height(24.dp))

                ProfileSettingsSection(
                    title = "Account Settings",
                    settings = settingOptions,
                    onNavigate = { route -> navController.navigate(route) }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        showLogoutDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Log out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Hiển thị AlertDialog nếu người dùng nhấn nút "Log out"
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Xác nhận đăng xuất") },
                    text = { Text("Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showLogoutDialog = false
                            viewModel.logout()
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                            if (context is Activity) {
                                context.finish()
                            }
                        }) {
                            Text("Đồng ý")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Hủy")
                        }
                    }
                )
            }
        }
    )
}
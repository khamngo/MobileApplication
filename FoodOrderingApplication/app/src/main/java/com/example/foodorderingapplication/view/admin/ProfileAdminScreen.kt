package com.example.foodorderingapplication.view.admin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.AdminActivity
import com.example.foodorderingapplication.MainActivity
import com.example.foodorderingapplication.model.BottomNavItem
import com.example.foodorderingapplication.model.SettingOption
import com.example.foodorderingapplication.view.BottomNavBar
import com.example.foodorderingapplication.view.profile.ProfileHeaderSection
import com.example.foodorderingapplication.view.profile.ProfileSettingsSection
import com.example.foodorderingapplication.viewmodel.AuthViewModel
import com.example.foodorderingapplication.viewmodel.MyAccountViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun ProfileAdminScreen(
    navController: NavController,
    viewModel: MyAccountViewModel = viewModel()
) {
    val userState by viewModel.user.collectAsState()
    val activity = LocalContext.current as? Activity
    val bottomNavItems = listOf(BottomNavItem.Home, BottomNavItem.Profile)
    val authViewModel: AuthViewModel = viewModel()

    val settingOptions = listOf(
        SettingOption(Icons.Default.Person, "Admin", "admin_account"),
        SettingOption(Icons.Default.ShoppingCart, "Orders", "order"),
        SettingOption(Icons.Default.CreditCard, "Revenue", "revenue"),
        SettingOption(Icons.Default.Star, "Reviews", "review"),
        SettingOption(Icons.Default.PersonAddAlt1, "New account", "new_account") ,
        SettingOption(Icons.Default.ListAlt, "User Management", "user_management")
    )

    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm logout") },
            text = { Text("Do you want to move to user mode?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    activity?.let {
                        val intent = Intent(it, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            putExtra(BottomNavItem.Home.route, true) // nếu bạn cần chỉ định màn hình
                        }
                        it.startActivity(intent)
                        it.finish()
                    }
                }) {
                    Text("Agree")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, bottomNavItems) },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F7F7))
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(4.dp)
                        .clickable {
                            showDialog = true
                        }
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout")
                }
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

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Confirm logout") },
                    text = { Text("Are you sure you want to log out of the application?") },
                    confirmButton = {

                        TextButton(onClick = {
                            showLogoutDialog = false
                            authViewModel.logout()

                            activity?.let {
                                val intent = Intent(it, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                it.startActivity(intent)
                                it.finish()
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
        }
    )
}
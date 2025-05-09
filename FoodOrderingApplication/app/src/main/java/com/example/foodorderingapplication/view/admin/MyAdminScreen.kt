package com.example.foodorderingapplication.view.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.ui.theme.MograFont
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.profile.CustomTextField
import com.example.foodorderingapplication.view.profile.MyAccountScreen
import com.example.foodorderingapplication.view.profile.PasswordField
import com.example.foodorderingapplication.viewmodel.MyAccountViewModel
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun AdminAccountScreen(navController: NavController, viewModel: MyAccountViewModel = viewModel()) {
    val userState by viewModel.user.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    // Kiểm tra quyền
    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher để chọn ảnh từ thư viện
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.uploadAvatarImage(uri, context)
        }
    }

    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var currentPassword by rememberSaveable { mutableStateOf("") }

    var isEditing by rememberSaveable { mutableStateOf(false) }
    val isGoogleSignIn = userState.provider == GoogleAuthProvider.PROVIDER_ID

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            if (errorMessage == "Profile updated successfully") {
                isEditing = false // Thoát chế độ chỉnh sửa sau khi cập nhật thành công
            }
            viewModel.clearErrorMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            HeaderSection("My Account") {
                navController.popBackStack()
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(userState.avatarUrl)
                                    .crossfade(true)
                                    .placeholder(R.drawable.ic_placeholder_avatar)
                                    .error(R.drawable.ic_placeholder_avatar)
                                    .build(),
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                        if (isEditing) {
                            IconButton(
                                onClick = {
                                    requestStoragePermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                    launcher.launch("image/*")
                                },
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.BottomEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change Avatar",
                                    tint = Color.Black,
                                )
                            }
                        }
                    }

                    Text(
                        text = userState.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = MograFont
                    )
                }

                Text(
                    text = if (isGoogleSignIn) "Signed in with Google" else "Signed in with Email",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                CustomTextField(
                    value = userState.username,
                    onValueChange = { if (isEditing) viewModel.updateUsername(it) },
                    label = "Username",
                    enabled = isEditing,
                    isError = errorMessage.contains("Username")
                )

                CustomTextField(
                    value = userState.email,
                    onValueChange = {},
                    label = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = false
                )

                CustomTextField(
                    value = userState.phone ?: "",
                    onValueChange = { if (isEditing) viewModel.updatePhone(it) },
                    label = "Phone",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    enabled = isEditing,
                    isError = errorMessage.contains("Phone")
                )

                if (!isGoogleSignIn && isEditing) {
                    PasswordField(
                        label = "Current Password",
                        password = currentPassword,
                        onPasswordChange = { currentPassword = it },
                        isError = errorMessage.contains("reauthenticate")
                    )

                    PasswordField(
                        label = "New Password (optional)",
                        password = password,
                        onPasswordChange = { password = it },
                        isError = errorMessage.contains("Password")
                    )

                    PasswordField(
                        label = "Confirm New Password",
                        password = confirmPassword,
                        onPasswordChange = { confirmPassword = it },
                        isError = errorMessage.contains("Confirm Password")
                    )
                }
            }
        }

        // Button luôn ở cuối màn hình
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (isEditing) {
                        viewModel.updateUserProfile(password, confirmPassword, currentPassword)
                    } else {
                        isEditing = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (isEditing) "Update" else "Edit", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
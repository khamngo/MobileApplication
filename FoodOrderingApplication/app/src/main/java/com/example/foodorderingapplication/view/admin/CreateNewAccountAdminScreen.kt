package com.example.foodorderingapplication.view.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.AuthViewModel

@Composable
fun CreateNewAccountScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var selectedRole by rememberSaveable { mutableStateOf("user") } // Mặc định là user

    val createAccountSuccess by authViewModel.createAccountSuccess.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Xử lý điều hướng với xác nhận
    LaunchedEffect(createAccountSuccess) {
        if (createAccountSuccess) {
            showDialog.value = true
        }
    }

    // Hiển thị hộp thoại xác nhận
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = { Text(text = "Xác nhận") },
            text = { Text("Bạn có chắc chắn muốn chuyển đến trang đăng nhập?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Điều hướng đến trang đăng nhập
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        authViewModel.resetCreateAccountState()
                        showDialog.value = false
                    }
                ) {
                    Text("Đồng ý")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    // Hiển thị lỗi
    if (errorMessage.isNotEmpty()) {
        LaunchedEffect(errorMessage) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        HeaderSection("Create New Account") {
            navController.popBackStack()
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                isError = errorMessage.contains("Username")
            )

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = errorMessage.contains("Email")
            )

            CustomTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = errorMessage.contains("Email")
            )

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = errorMessage.contains("Password")
            )

            CustomTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = errorMessage.contains("Confirm Password")
            )

            // Chọn vai trò
            Text("Role", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    RadioButton(
                        selected = selectedRole == "user",
                        onClick = { selectedRole = "user" }
                    )
                    Text("User", modifier = Modifier.padding(start = 8.dp))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    RadioButton(
                        selected = selectedRole == "admin",
                        onClick = { selectedRole = "admin" }
                    )
                    Text("Admin", modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    authViewModel.createAccount(
                        username = username,
                        email = email,
                        phone = phone,
                        password = password,
                        confirmPassword = confirmPassword,
                        role = selectedRole
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Create New Account", fontSize = 16.sp, color = Color.White)
            }

            // Hiển thị lỗi
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = if (isError) Color.Red else Color.Gray
            )
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        isError = isError,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    )
}

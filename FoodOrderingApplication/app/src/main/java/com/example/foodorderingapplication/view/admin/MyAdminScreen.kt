package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.profile.PasswordField
import com.example.foodorderingapplication.viewmodel.MyAccountViewModel

@Composable
fun AdminAccountScreen(navController: NavController, viewModel: MyAccountViewModel = viewModel()) {
    val userState by viewModel.user.collectAsState()

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        HeaderSection("Admin Account") {
            navController.popBackStack()
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextField(
                value = userState.username,
                onValueChange = { viewModel.updateUsername(it) },
                label = "Username"
            )

            CustomTextField(
                value = userState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = "Email"
            )

            CustomTextField(
                value = userState.phone ?: "",
                onValueChange = { viewModel.updatePhone(it) },
                label = "Phone"
            )

            PasswordField(
                label = "Password",
                password = password,
                onPasswordChange = { password = it })
            PasswordField(
                label = "Confirm Password",
                password = confirmPassword,
                onPasswordChange = { confirmPassword = it })

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (password == confirmPassword) {

                        navController.popBackStack()
                    } else {
                        // thông báo lỗi xác nhận mật khẩu
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), // Màu vàng
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Update", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
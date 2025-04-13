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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.profile.PasswordField

@Composable
fun AdminAccountScreen(  navController: NavController) {
    var username by remember { mutableStateOf("Ngo Minh Kham") }
    var email by remember { mutableStateOf("ngominhkham25@gmail.com") }
    var phone by remember { mutableStateOf("+ 84 524168234") }
    var password by remember { mutableStateOf("ngominhkham25") }
    var confirmPassword by remember { mutableStateOf("ngominhkham25") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        HeaderSection("Admin Account", navController)

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username"
            )

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )

            CustomTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Email"
            )

            PasswordField(label = "Password", password = password, onPasswordChange = { password = it })
            PasswordField(label = "Confirm Password", password = confirmPassword, onPasswordChange = { confirmPassword = it })

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* Xử lý thanh toán */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), // Màu vàng
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Update", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
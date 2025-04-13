package com.example.foodorderingapplication.view.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.admin.CustomTextField

@Composable
fun MyAccountScreen(  navController: NavController) {
    var username by remember { mutableStateOf("Ngo Minh Kham") }
    var email by remember { mutableStateOf("ngominhkham25@gmail.com") }
    var phone by remember { mutableStateOf("+ 84 524168234") }
    var password by remember { mutableStateOf("ngominhkham25") }
    var confirmPassword by remember { mutableStateOf("ngominhkham25") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        HeaderSection("My Account", navController)

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

@Composable
fun PasswordField(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(text = label) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = icon, contentDescription = description)
            }
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Yellow,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.Gray
        )
    )
}


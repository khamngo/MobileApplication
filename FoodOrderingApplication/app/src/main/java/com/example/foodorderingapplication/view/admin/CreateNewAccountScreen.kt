package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.view.AddShippingScreen

@Composable
fun CreateNewAccountScreen(  navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD700)) // Màu vàng
                .padding(top = 16.dp)
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Arrow back",
                )
            }

            Text(
                text = "Create New Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
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
                label = "Username"
            )

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password"
            )

            CustomTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password"
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* Xử lý thanh toán */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), // Màu vàng
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Create New Account", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

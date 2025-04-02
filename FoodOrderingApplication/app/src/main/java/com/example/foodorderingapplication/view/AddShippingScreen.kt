package com.example.foodorderingapplication.view

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.models.Restaurant

@Composable
fun AddShippingScreen() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    val restaurants = listOf(
        Restaurant(
            "KFOODS1 - LANDMARK 81",
            "720A Điện Biên Phủ, P22, Q. Bình Thạnh, TP HCM",
            "056 3167 5325",
            "8:30 AM - 10:00 PM"
        ),
        Restaurant(
            "KFOODS2 - BITEXCO",
            "2 Hải Triều, Quận 1, TP HCM",
            "056 3428 8245",
            "9:30 AM - 11:00 PM"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
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
                text = "Add shipping address",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Contact",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )

            // Contact Fields
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Yellow, // Màu khi được focus
                    unfocusedContainerColor = Color.White // Màu khi không focus
                )
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Yellow, // Màu khi được focus
                    unfocusedContainerColor = Color.White // Màu khi không focus
                )
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Yellow, // Màu khi được focus
                    unfocusedContainerColor = Color.White // Màu khi không focus
                )
            )

        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Address",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            // Address Fields
            OutlinedTextField(
                value = province,
                onValueChange = { province = it },
                label = { Text("Province, District, Commune") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Yellow, // Màu khi được focus
                    unfocusedContainerColor = Color.White // Màu khi không focus
                )
            )
            OutlinedTextField(
                value = street,
                onValueChange = { street = it },
                label = { Text("Street, No.") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Yellow, // Màu khi được focus
                    unfocusedContainerColor = Color.White // Màu khi không focus
                )
            )
        }

        // Default Account Checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Text("Set as default account", fontSize = 16.sp)
            Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })

        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Choose a restaurant
            Text(
                text = "Choose a restaurant",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            restaurants.forEach { restaurant ->
                RestaurantCard(restaurant)
            }

        }
        // Confirm Button
        Button(
            onClick = { /* Xử lý thanh toán */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), // Màu vàng
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Confirm", fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
fun RestaurantCard(restaurant: Restaurant) {
    Card(
        border = BorderStroke(2.dp, Color(0xFFFFD700)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Icon(Icons.Default.Home, contentDescription = "Address", tint = Color.Black)
                Text(restaurant.address)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.Black)

                Text(restaurant.phone)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Icon(Icons.Default.DateRange, contentDescription = "Time", tint = Color.Black)

                Text(restaurant.hours)
            }
            Button(
                onClick = { /* Xử lý chọn cửa hàng */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), // Màu vàng
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                Text("Chọn cửa hàng", fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddShippingPreview() {
    AddShippingScreen()
}
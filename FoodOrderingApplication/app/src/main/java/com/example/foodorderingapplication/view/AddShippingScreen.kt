package com.example.foodorderingapplication.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.models.Restaurant

@Composable
fun AddShippingScreen(navController: NavController) {
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
        HeaderSection("Add shopping address", navController)

        ContactSection(
            firstName,
            lastName,
            phoneNumber,
            onFirstNameChange = { firstName = it },
            onLastNameChange = { lastName = it },
            onPhoneNumberChange = { phoneNumber = it })

        AddressSection(
            province,
            street,
            onProvinceChange = { province = it },
            onStreetChange = { street = it })

        DefaultAccountCheckbox(isDefault, onCheckedChange = { isDefault = it })
        RestaurantSelectionSection(restaurants)
        ConfirmButton { navController.popBackStack() }
    }
}

@Composable
fun ContactSection(
    firstName: String,
    lastName: String,
    phoneNumber: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Text("Contact", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AddressSection(
    province: String,
    street: String,
    onProvinceChange: (String) -> Unit,
    onStreetChange: (String) -> Unit
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Text("Address", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = province,
            onValueChange = onProvinceChange,
            label = { Text("Province, District, Commune") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = street,
            onValueChange = onStreetChange,
            label = { Text("Street, No.") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DefaultAccountCheckbox(isDefault: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text("Set as default account", fontSize = 16.sp)
        Checkbox(checked = isDefault, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun RestaurantSelectionSection(restaurants: List<Restaurant>) {
    val selectedRestaurant = remember { mutableStateOf(restaurants.firstOrNull()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Choose a restaurant",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        restaurants.forEach { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                isSelected = selectedRestaurant.value == restaurant,
                onSelect = { selectedRestaurant.value = restaurant }
            )
        }
    }
}


@Composable
fun ConfirmButton(onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
        ) {
            Text(
                "Confirm",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        border = BorderStroke(2.dp, if (isSelected) Color(0xFFFFD700) else Color.Gray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, contentDescription = "Address", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(restaurant.address)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(restaurant.phone)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = "Time", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(restaurant.hours)
            }

            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFFFFD700) else Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    "Chọn cửa hàng",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}


package com.example.foodorderingapplication.view.menu

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.model.RestaurantItem
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.OrderViewModel
import com.example.foodorderingapplication.viewmodel.RestaurantViewModel
import com.example.foodorderingapplication.viewmodel.ShippingViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AddShippingScreen(
    navController: NavController,
    viewModel: ShippingViewModel = viewModel(),
    resViewModel: RestaurantViewModel = viewModel()
) {
    // State cho các trường UI
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var ward by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val shippingAddress by viewModel.shippingAddress.collectAsState()
    val restaurantItems by resViewModel.restaurantItems.collectAsState()

    var restaurant by remember { mutableStateOf(RestaurantItem()) }

    LaunchedEffect(restaurantItems) {
        if (restaurantItems.isNotEmpty()) {
            restaurant = restaurantItems.first()
        }
    }

    // Đồng bộ dữ liệu từ ViewModel vào state UI
    LaunchedEffect(shippingAddress) {
        firstName = shippingAddress.firstName
        lastName = shippingAddress.lastName
        phoneNumber = shippingAddress.phoneNumber
        province = shippingAddress.province
        district = shippingAddress.district
        ward = shippingAddress.ward
        street = shippingAddress.street
        restaurant = shippingAddress.restaurant
        isDefault = shippingAddress.isDefault
    }

    // Nếu chưa đăng nhập
    if (userId == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Please login to add address!", color = Color.Red, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Login")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        HeaderSection("Add shipping address") {
            navController.popBackStack()
        }

        // Hiển thị thông báo lỗi chung
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Contact Section
        ContactSection(
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            firstNameError = fieldErrors.firstNameError,
            lastNameError = fieldErrors.lastNameError,
            phoneNumberError = fieldErrors.phoneNumberError || fieldErrors.phoneNumberInvalid,
            onFirstNameChange = {
                firstName = it
                viewModel.onFirstNameChange(it)
            },
            onLastNameChange = {
                lastName = it
                viewModel.onLastNameChange(it)
            },
            onPhoneNumberChange = {
                phoneNumber = it
                viewModel.onPhoneNumberChange(it)
            }
        )

        AddressSection(
            province = province,
            district = district,
            ward = ward,
            street = street,
            provinceError = fieldErrors.provinceError,
            streetError = fieldErrors.streetError,
            districtError = fieldErrors.provinceError,
            wardError = fieldErrors.streetError,
            onProvinceChange = {
                province = it
                viewModel.onProvinceChange(it)
            },
            onStreetChange = {
                street = it
                viewModel.onStreetChange(it)
            },
            onDistrictChange = {
                district = it
                viewModel.onDistrictChange(it)
            },
            onWardChange = {
                ward = it
                viewModel.onWardChange(it)
            }
        )


        // Checkbox
        DefaultAccountCheckbox(isDefault, onCheckedChange = {
            isDefault = it
            viewModel.onIsDefaultChange(it)
        })

        // Restaurant Selection
        RestaurantScreen(viewModel = resViewModel,selectedRestaurant = restaurant)

        // Confirm Button
        ConfirmButton {
            viewModel.clearError()
            viewModel.saveShippingAddressToFirebase(
                userId = userId,
                onSuccess = {
                    Toast.makeText(context, "Address saved successfully!", Toast.LENGTH_SHORT).show()
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("add_shopping_address", true)
                    navController.popBackStack()
                },
                onFailure = {
                    Toast.makeText(context, "Error saving address: ${it.message}", Toast.LENGTH_LONG)
                        .show()
                }
            )
        }
    }
}

@Composable
fun ContactSection(
    firstName: String,
    lastName: String,
    phoneNumber: String,
    firstNameError: Boolean,
    lastNameError: Boolean,
    phoneNumberError: Boolean,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Contact", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = {
                Text(
                    if (firstNameError) "First Name cannot be empty" else "First Name",
                    color = if (firstNameError) Color.Red else Color.Gray
                )
            },
            isError = firstNameError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = {
                Text(
                    if (lastNameError) "Last Name cannot be empty" else "Last Name",
                    color = if (lastNameError) Color.Red else Color.Gray
                )
            },
            isError = lastNameError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = {
                Text(
                    when {
                        phoneNumberError && phoneNumber.isBlank() -> "Phone Number cannot be empty"
                        phoneNumberError -> "Invalid Phone Number (10 digits)"
                        else -> "Phone Number"
                    },
                    color = if (phoneNumberError) Color.Red else Color.Gray
                )
            },
            isError = phoneNumberError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
    }
}

@Composable
fun AddressSection(
    province: String,
    street: String,
    district: String,
    ward: String,
    provinceError: Boolean,
    streetError: Boolean,
    districtError: Boolean,
    wardError: Boolean,
    onProvinceChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onWardChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Address", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = province,
            onValueChange = onProvinceChange,
            label = {
                Text(
                    if (provinceError) "Province/City cannot be empty" else "Province/City",
                    color = if (provinceError) Color.Red else Color.Gray
                )
            },
            isError = provinceError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = district,
            onValueChange = onDistrictChange,
            label = {
                Text(
                    if (districtError) "District  cannot be empty" else "District",
                    color = if (districtError) Color.Red else Color.Gray
                )
            },
            isError = districtError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = ward,
            onValueChange = onWardChange,
            label = {
                Text(
                    if (wardError) "Ward  cannot be empty" else "Ward",
                    color = if (wardError) Color.Red else Color.Gray
                )
            },
            isError = wardError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = street,
            onValueChange = onStreetChange,
            label = {
                Text(
                    if (streetError) "Street cannot be empty" else "Street",
                    color = if (streetError) Color.Red else Color.Gray
                )
            },
            isError = streetError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
fun DefaultAccountCheckbox(isDefault: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isDefault,
            onCheckedChange = onCheckedChange
        )
        Text("Set as default address", modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun RestaurantScreen(
    viewModel: RestaurantViewModel,
    selectedRestaurant: RestaurantItem
) {
    val restaurantItems by viewModel.restaurantItems.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.errorRestaurant.collectAsState()

    when {
        isLoading -> CircularProgressIndicator()
        error != null -> Text("Error: $error", color = Color.Red)
        else -> RestaurantSelectionSection(restaurantItems = restaurantItems, selectedRestaurant = selectedRestaurant)
    }
}

@Composable
fun RestaurantSelectionSection(
    restaurantItems: List<RestaurantItem>,
    selectedRestaurant: RestaurantItem,
    viewModel: ShippingViewModel = viewModel()
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Choose a restaurant",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        restaurantItems.forEach { restaurant ->
            RestaurantCard(
                restaurantItem = restaurant,
                isSelected = selectedRestaurant == restaurant,
                onSelect = {
                    viewModel.selectRestaurant(restaurant)
                }
            )
        }
    }
}

@Composable
fun RestaurantCard(
    restaurantItem: RestaurantItem,
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
            Text(restaurantItem.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, contentDescription = "Address", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(restaurantItem.address)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(restaurantItem.phone)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = "Time", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(restaurantItem.hours)
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
                    "Select",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ConfirmButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
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
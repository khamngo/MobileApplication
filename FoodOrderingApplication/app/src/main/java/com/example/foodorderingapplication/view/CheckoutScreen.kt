package com.example.foodorderingapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.models.Cart
import com.example.foodorderingapplication.viewmodel.CartViewModel

@Composable
fun CheckoutScreen(viewModel: CartViewModel = viewModel(), navController: NavController) {
    val cartItems by viewModel.cartItems.collectAsState()
    val total by viewModel.total.collectAsState()

    val subtotal = total + 2.0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            // Header
            HeaderSection("Checkout", navController)

            // Shipping, Delivery, Promos
            ShippingDeliveryPromosSection(navController)

            // Items List
            ItemsList(cartItems)

            // Order Summary
            OrderSummary()

            // Payment Method
            PaymentMethod()
        }

        // Subtotal & Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
        ) {
            SubtotalAndButton(
                title = "Place Order",
                subtotal = subtotal,
                navController = navController,
                pageName = "payment"
            )
        }
    }
}

@Composable
fun ShippingDeliveryPromosSection(navController: NavController) {
    val showDialog = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf("Shipping now") }
    val selectedTime = remember { mutableStateOf("") }
    val showPromoDialog = remember { mutableStateOf(false) }
    val selectedPromo = remember { mutableStateOf("Free Shipping") }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider()

        ShippingRow("SHIPPING", "Add shipping address") {
            navController.navigate("add_shopping_address")
        }
        HorizontalDivider()

        ShippingRow("DELIVERY", selectedDate.value) {
            showDialog.value = true
        }
        HorizontalDivider()

        ShippingRow("PROMOS", selectedPromo.value) {
            showPromoDialog.value = true
        }
        HorizontalDivider()

        // Hiển thị dialog chọn ngày giao hàng
        DeliveryTimeDialog(showDialog) { date, time ->
            selectedDate.value = date
            selectedTime.value = time
            showDialog.value = false
        }

        // Hiển thị dialog chọn mã giảm giá
        PromoDialog(showPromoDialog) { promo ->
            showPromoDialog.value = false
            selectedPromo.value = promo
        }
    }
}

@Composable
fun ShippingRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.Bold)

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = value, color = Color.Gray)
            IconButton(onClick = onClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = "Arrow Forward",
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun ItemsList(cartItems: List<Cart>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ITEMS", fontWeight = FontWeight.Bold)
            Text("DESCRIPTION", fontWeight = FontWeight.Bold)
            Text("PRICE", fontWeight = FontWeight.Bold)
        }

        cartItems.forEach { cart ->
            ItemRow(cart) // Chỉ truyền đối tượng Cart
        }
    }
}

@Composable
fun ItemRow(cart: Cart) {
    val context = LocalContext.current
    val imageId = remember(cart.imageRes) {
        context.resources.getIdentifier(cart.imageRes, "drawable", context.packageName)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = cart.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(cart.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Quantity: ${cart.quantity}", fontSize = 14.sp)
        }

        Text("$${"%.2f".format(cart.price)}", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OrderSummary() {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),

        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        SummaryRow("Subtotal (2)", "$23.98")
        SummaryRow("Shipping total", "Free")
        SummaryRow("Taxes", "$2.00")
        HorizontalDivider()
        SummaryRow("Total", "$25.98", Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    textColor: Color = Color.Black,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = fontSize, fontWeight = fontWeight)
        Text(text = value, fontSize = fontSize, fontWeight = fontWeight, color = textColor)
    }
}

@Composable
fun PaymentMethod() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Payment Gateway", fontWeight = FontWeight.Bold)
        PaymentOption(R.drawable.banking_method, "Cash on Delivery", true)
        PaymentOption(R.drawable.cod_method, "Bank Account", false)
    }
}

@Composable
fun PaymentOption(image: Int, label: String, selected: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {

        Image(
            painter = painterResource(id = image),
            contentDescription = "Arrow back",
            modifier = Modifier.size(30.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Text(label)
            RadioButton(selected = selected, onClick = {})
        }
    }
}

@Composable
fun DeliveryTimeDialog(showDialog: MutableState<Boolean>, onConfirm: (String, String) -> Unit) {
    val selectedDate = remember { mutableStateOf("Today") }
    val selectedTime = remember { mutableStateOf("Now") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Change delivery time", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { showDialog.value = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date", fontWeight = FontWeight.Bold)
                        Text("Time", fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider()
                    DeliveryOption("Today", "Now", selectedDate, selectedTime)
                    HorizontalDivider()
                    DeliveryOption("05/03/2025", "16:15", selectedDate, selectedTime)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(selectedDate.value, selectedTime.value)
                        showDialog.value = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("Confirm")
                }
            }
        )
    }
}

@Composable
fun DeliveryOption(
    date: String, time: String,
    selectedDate: MutableState<String>,
    selectedTime: MutableState<String>
) {
    val isSelected = selectedDate.value == date && selectedTime.value == time

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                selectedDate.value = date
                selectedTime.value = time
            }
            .background(if (isSelected) Color(0xFFFFD500) else Color.Transparent) // Màu nền khi được chọn
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(date)
        }
        Text(time)
    }
}

@Composable
fun PromoDialog(showDialog: MutableState<Boolean>, onPromoSelected: (String) -> Unit) {
    val promoOptions = listOf(
        "Free Shipping",
        "5% off for orders above 100K",
        "10% off for orders above 200K",
        "15% off for orders above 500K"
    )
    val selectedPromo = remember { mutableStateOf(promoOptions[0]) } // Mặc định là "Free Shipping"

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Select Promo Code") },
            text = {
                Column {
                    promoOptions.forEach { promo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPromo.value = promo
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedPromo.value == promo),
                                onClick = { selectedPromo.value = promo }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(promo, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onPromoSelected(selectedPromo.value)
                    showDialog.value = false
                }) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview1() {
    NavigationGraph()
}
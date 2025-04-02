package com.example.foodorderingapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.R

@Composable
fun CheckoutScreen(navController: NavController) {
    val subtotal = 19.2
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        // Header
        HeaderSection("Checkout", navController)

        // Shipping, Delivery, Promos
        ShippingDeliveryPromosSection(navController)

        // Items List
        ItemsList()

        // Order Summary
        OrderSummary()

        // Payment Method
        PaymentMethod()

        // Subtotal & Place Order Button
        SubtotalAndAddToCart("Checkout", subtotal, navController, "")
    }
}

@Composable
fun ShippingDeliveryPromosSection(navController: NavController) {
    val showDialog = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf("Shipping now") }
    val selectedTime = remember { mutableStateOf("") }

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

        ShippingRow("PROMOS", "Apply promo code") {
            // Xử lý khi nhấn vào PROMOS
            println("PROMOS clicked!")
        }
        HorizontalDivider()

        // Hiển thị dialog chọn ngày giao hàng
        DeliveryTimeDialog(showDialog) { date, time ->
            selectedDate.value = date
            selectedTime.value = time
            showDialog.value = false
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
fun ItemsList() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ITEMS", fontWeight = FontWeight.Bold)
            Text("DESCRIPTION", fontWeight = FontWeight.Bold)
            Text("PRICE", fontWeight = FontWeight.Bold)
        }

        ItemRow(R.drawable.tteok, "Tteok", "Korean rice-cake.", "$10.99", 1)
        ItemRow(R.drawable.hobakjuk, "Hobakjuk", "Pumpkin-porridge.", "$12.99", 1)
    }
}

@Composable
fun ItemRow(image: Int, title: String, description: String, price: String, quantity: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = title,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(description, fontSize = 14.sp)
            Text("Quantity : $quantity", fontSize = 14.sp)
        }

        Text(price, fontWeight = FontWeight.Bold)
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
                    DeliveryOption("Today", "Now", onConfirm)
                    HorizontalDivider()
                    DeliveryOption("05/03/2025", "16:15", onConfirm)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog.value = false },
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
fun DeliveryOption(date: String, time: String, onConfirm: (String, String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onConfirm(date, time) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(date)
        Text(time)
    }
}

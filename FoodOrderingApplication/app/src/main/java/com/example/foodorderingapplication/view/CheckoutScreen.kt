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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.R

@Composable
fun CheckoutScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        HeaderSection()

        // Shipping, Delivery, Promos
        ShippingDeliveryPromosSection()

        // Items List
        ItemsList()

        // Order Summary
        OrderSummary()

        // Payment Method
        PaymentMethod()

        // Subtotal & Place Order Button
        CheckoutFooter()
    }
}

@Composable
fun HeaderSection() {
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
            text = "Checkout",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ShippingDeliveryPromosSection() {
    val showDialog = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf("Shipping now") }
    val selectedTime = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider()

        ShippingRow("SHIPPING", "Add shipping address") {
            // Xử lý khi nhấn vào SHIPPING
            println("SHIPPING clicked!")
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
            .clickable { onClick() } // Xử lý khi nhấn cả hàng
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
        SummaryRow("Total", "$25.98", Color.Red)
    }
}

@Composable
fun SummaryRow(label: String, value: String, textColor: Color = Color.Black) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value, fontWeight = FontWeight.Bold, color = textColor)
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
fun CheckoutFooter() {
    val subtotal = 25.98
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$${"%.2f".format(subtotal)}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("$25.98", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* Xử lý thanh toán */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), // Màu vàng
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Place Order", fontSize = 16.sp, color = Color.White)
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

@Preview(showBackground = true)
@Composable
fun CheckoutPreview() {
    CheckoutScreen()
}

package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.Order
import com.example.foodorderingapplication.view.HeaderSection

@Composable
fun OrderDetailScreen(navController: NavController, orderId: String) {
    val orderList = listOf(
        Order(1, "11 AM : 03-03-2025", "Tteok", "Korean rice-caked.", 10.99, "None", 21.98, "Preparing"),
        Order(2, "12 PM : 04-03-2025", "Bibimbap", "Korean mixed rice.", 12.50, "Extra sauce", 25.00, "Completed"),
        Order(3, "10 AM : 05-03-2025", "Kimchi", "Fermented vegetables.", 5.99, "Less spicy", 11.98, "Cancelled")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        HeaderSection("Order Detail", navController)

        // Danh sách hóa đơn
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(orderList) { order ->
                OrderItem(order)
                HorizontalDivider() // Ngăn cách các đơn hàng
            }
        }
    }
}

@Composable
fun OrderItem(order: Order) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Time & Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = order.time,
                fontSize = 14.sp,
                color = Color.Gray,
                fontStyle = FontStyle.Italic
            )

            Text(
                text = order.status,
                fontSize = 16.sp,
                color = when (order.status) {
                    "Preparing" -> Color(0xFFFFC107)
                    "Completed" -> Color(0xFF34A854)
                    "Cancelled" -> Color(0xFFFF5151)
                    else -> Color.Black
                },
                fontWeight = FontWeight.Bold
            )
        }

        // Product Details
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
        )  {
//            Image(
//                painter = painterResource(id = R.drawable.tteok),
//                contentDescription = "Product Image",
//                modifier = Modifier.size(84.dp)
//            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)            ,modifier = Modifier.fillMaxWidth()) {
                Text(text = order.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = order.description, fontSize = 14.sp)
                Text(text = "$${order.price}", fontSize = 14.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                Text(text = "Note: ${order.note}", fontSize = 14.sp)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Order Information
        Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Information order", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            InfoRow(label = "Full Name:", value = "Le Hoang Quy")
            InfoRow(label = "Phone Number:", value = "1234567")
            InfoRow(label = "Address:", value = "Sai Gon")
            InfoRow(label = "Payment Gateway:", value = "COD")
        }

        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))


        // Tổng tiền & Nút Accept
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total: $${order.total}",
                fontSize = 18.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { /* Xử lý khi nhấn Accept */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (order.status) {
                        "Preparing" -> Color(0xFFFFD700) // Yellow
                        "Complete", "Cancelled" -> Color.Gray
                        else -> Color.Gray
                    }
                ),
                enabled = order.status == "Preparing",
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Accept", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp, color = Color.Black)
    }
}


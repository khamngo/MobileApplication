package com.example.foodorderingapplication.view.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.OrderStatus
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.OrderDetailViewModel
import com.example.foodorderingapplication.viewmodel.OrderViewModel

@Composable
fun OrderDetailScreen(navController: NavController, orderId: String, viewModel: OrderDetailViewModel = viewModel()) {
    val orderDetail by viewModel.orderDetail.collectAsState()
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
            HeaderSection("Order Detail"){
                navController.popBackStack()
            }

            // Status delivery progress (Fake with Icons)
            DeliveryStatusSection()

            HorizontalDivider()

            // From / To Info
            FromToInfo()

            HorizontalDivider()

            // Order Detail
            OrderDetailContent()

            Spacer(modifier = Modifier.weight(1f))

        }

        // Subtotal & Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .padding(16.dp)
        ) {
            val viewModel: OrderViewModel = viewModel()
//            OrderActionButton(
//                orderStatus = viewModel.orderStatus,
//                onCancel = { viewModel.cancelOrder() },
//                onBuyAgain = { viewModel.buyAgain() }
//            )

        }
    }
}

@Composable
fun DeliveryStatusSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Status Delivery",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.List, contentDescription = null, tint = Color.Gray)
            HorizontalDivider(modifier = Modifier.width(40.dp), color = Color.Gray)

            Icon(Icons.Default.List, contentDescription = null, tint = Color.Gray)
            HorizontalDivider(modifier = Modifier.width(40.dp), color = Color.Gray)

            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.Gray)
            HorizontalDivider(modifier = Modifier.width(40.dp), color = Color.Gray)

            Icon(Icons.Default.Home, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun FromToInfo() {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth())
    {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(color = Color.Red)
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text("From", fontWeight = FontWeight.Bold, color = Color.Red)
            }

            Text("Đồ ăn | Nhà hàng cao cấp cao - 70 Tô Ký")
            Text("70 Tô Ký, P. Tân Chánh Hiệp, Quận 12", color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(color = Color.Green)
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text("To", fontWeight = FontWeight.Bold, color = Color.Green)
            }

            Text("31/18, Tân Chánh Hiệp, Tân Thới Hiệp, Quận 12")
            Text("Hoàng Quý - 0764018173", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun OrderDetailContent() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("Order detail", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth()
        ) {
//            Image(
//                painter = painterResource(id = R.drawable.gimbap),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(60.dp)
//                    .clip(RoundedCornerShape(8.dp))
//            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Gimbap", fontWeight = FontWeight.Bold)
                Text("A bowl of white rice topped with vegetable, egg and sliced meat.")
                Text("Quantity: 1")
            }

            Text("$7.99", fontWeight = FontWeight.Bold)
        }

        OrderInfoRow("Order id:", "999999999")
        OrderInfoRow("Note:", "None")
        OrderInfoRow("Order date:", "11/07/2025")
        OrderInfoRow("Payment Gateway:", "COD")
        OrderInfoRow("Shipping fee:", "$0")
        OrderInfoRow("Apply fee:", "$2")
        OrderInfoRow("Discount fee:", "$0")

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("$9.99", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 16.sp)
        }
    }
}

@Composable
fun OrderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value)
    }
}

@Composable
fun OrderActionButton(orderStatus: OrderStatus, onCancel: () -> Unit, onBuyAgain: () -> Unit) {
    val (text, enabled, onClick) = when (orderStatus) {
        OrderStatus.PENDING -> Triple("Cancel Order", true, onCancel)
        OrderStatus.COMPLETED -> Triple("Buy Again", true, onBuyAgain)
        OrderStatus.CANCELLED -> Triple("Cancelled", false) {} // disabled
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(52.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFD700),
            disabledContainerColor = Color(0xFFE0E0E0),
            disabledContentColor = Color.DarkGray
        )
    ) {
        Text(
            text,
            fontSize = 16.sp,
            color = if (enabled) Color.White else Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}

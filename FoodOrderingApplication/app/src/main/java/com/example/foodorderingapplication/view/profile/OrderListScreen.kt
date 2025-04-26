package com.example.foodorderingapplication.view.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.model.FoodItem
import com.example.foodorderingapplication.model.OrderItem
import com.example.foodorderingapplication.model.OrderItem1
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderListScreen(navController: NavController, orderViewModel: OrderViewModel = viewModel()) {
    val orders by orderViewModel.orders.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection("Orders", navController)

        RecentOrdersSection()

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(orders) { order ->
                OrderItemCardDisplay(order)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun OrderItemCardDisplay(order: OrderItem) {
    val statusColor = when (order.status) {
        "Completed" -> Color(0xFF228B22)
        "Cancelled" -> Color.Red
        else -> Color(0xFFFFA500)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = order.orderId.takeLast(5), // hiển thị mã đơn ngắn gọn
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Column(modifier = Modifier
            .weight(1f)
            .padding(start = 16.dp)) {
            Text(
                text = order.items.firstOrNull()?.name ?: "Food",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(order.orderDate.toDate()), fontSize = 14.sp, color = Color.Gray
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = order.status,
                color = statusColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next",
                tint = statusColor
            )
        }
    }
}

@Composable
fun RecentOrdersSection(viewModel: OrderViewModel = viewModel()) {
    val recentOrders by viewModel.recentOrders.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Recent Order",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyRow(
            modifier = Modifier.padding(16.dp)
        ) {
            items(recentOrders) { food ->
                RecentOrderCard(food = food)
            }
        }
    }
}

@Composable
fun RecentOrderCard(food: FoodItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        AsyncImage(
            model = food.imageUrl,
            contentDescription = food.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = food.name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = "$${food.price}",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun OrderItemCard(order: OrderItem1) {
    val statusColor = when (order.status) {
        "Completed" -> Color(0xFF228B22) // green
        "Cancelled" -> Color.Red
        else -> Color(0xFFFFA500) // orange (Preparing)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = order.id.toString().padStart(2, '0'),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Column(modifier = Modifier
            .weight(1f)
            .padding(start = 16.dp)) {
            Text(text = order.foodName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = order.date, fontSize = 14.sp, color = Color.Gray)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = order.status,
                color = statusColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next",
                tint = statusColor
            )
        }
    }
}


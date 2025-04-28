package com.example.foodorderingapplication.view.profile

import androidx.compose.foundation.clickable
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
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderListScreen(navController: NavController, orderViewModel: OrderViewModel = viewModel()) {
    val orders by orderViewModel.orders.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection("My Orders"){
            navController.popBackStack()
        }

        RecentOrdersSection()

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(orders) { order ->
                OrderItemCardDisplay(order){
                    navController.navigate("order_detail/${order.orderId}")
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun OrderItemCardDisplay(order: OrderItem, onClick: () -> Unit) {
    val statusColor = when (order.status) {
        "Completed" -> Color(0xFF228B22)
        "Cancelled" -> Color.Red
        else -> Color(0xFFFFA500)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp).clickable{onClick()},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = order.orderId.takeLast(5),
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
                tint = statusColor,
                modifier = Modifier.clickable{onClick()}
            )
        }
    }
}

@Composable
fun RecentOrdersSection(viewModel: OrderViewModel = viewModel()) {
    val recentOrders by viewModel.recentOrders.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "Recent Order",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(0.75f).padding(end = 16.dp)
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
            .fillMaxWidth(0.75f)
            .wrapContentHeight()
    ) {
        AsyncImage(
            model = food.imageUrl,
            contentDescription = food.name,
            modifier = Modifier
                .fillMaxWidth().height(120.dp)
                .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = food.name,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = "$${food.price}",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}



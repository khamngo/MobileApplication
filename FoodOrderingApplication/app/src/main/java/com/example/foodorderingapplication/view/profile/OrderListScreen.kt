package com.example.foodorderingapplication.view.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.model.OrderItem
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.menu.FoodItem
import com.example.foodorderingapplication.viewmodel.FoodViewModel

@Composable
fun OrderListScreen(navController: NavController, foodViewModel: FoodViewModel = viewModel())
{
    val orders = listOf(
        OrderItem(1, "Tteok", "3 April, 2025", "Preparing"),
        OrderItem(2, "Tteok", "5 April, 2025", "Completed"),
        OrderItem(3, "Tteok", "10 April, 2025", "Cancelled"),
        OrderItem(4, "Tteok", "15 April, 2025", "Completed"),
        OrderItem(5, "Tteok", "20 April, 2025", "Completed"),
        OrderItem(6, "Tteok", "25 April, 2025", "Cancelled"),
    )
    val foodItems by foodViewModel.foods.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection("Orders", navController)

        // Popular Section
        Text(
            text = "Recent Order",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(foodItems) { item ->
                FoodItem(item, onClick = {
                    navController.navigate("detail/1")
                })
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(orders) { order ->
                OrderItemCard(order)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun OrderItemCard(order: OrderItem) {
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
        Column (modifier = Modifier.padding(horizontal = 8.dp)){
            Text(
                text = order.id.toString().padStart(2, '0'),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
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


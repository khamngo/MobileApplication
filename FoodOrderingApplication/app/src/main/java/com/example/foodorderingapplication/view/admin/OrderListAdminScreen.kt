package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.profile.OrderItemCard
import com.example.foodorderingapplication.model.OrderItem

@Composable
fun OrderListScreen(navController: NavController) {
    val orders = listOf(
        OrderItem(1, "Tteok", "3 April, 2025", "Preparing"),
        OrderItem(2, "Tteok", "5 April, 2025", "Completed"),
        OrderItem(3, "Tteok", "10 April, 2025", "Cancelled"),
        OrderItem(4, "Tteok", "15 April, 2025", "Completed"),
        OrderItem(5, "Tteok", "20 April, 2025", "Completed"),
        OrderItem(6, "Tteok", "25 April, 2025", "Cancelled"),
    )

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection("Orders", navController)

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

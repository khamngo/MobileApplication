package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.profile.OrderItemCardDisplay
import com.example.foodorderingapplication.viewmodel.AdminOrderViewModel
import com.example.foodorderingapplication.viewmodel.OrderViewModel

@Composable
fun OrderListScreen(navController: NavController, orderViewModel: AdminOrderViewModel = viewModel()) {
    val orders by orderViewModel.orders.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection("Orders"){
            navController.popBackStack()
        }

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

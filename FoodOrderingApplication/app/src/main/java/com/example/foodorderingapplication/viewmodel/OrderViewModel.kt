package com.example.foodorderingapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.foodorderingapplication.model.OrderStatus
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class OrderViewModel : ViewModel() {
    var orderStatus by mutableStateOf(OrderStatus.PENDING)
        private set

    fun cancelOrder() {
        orderStatus = OrderStatus.CANCELLED
    }

    fun buyAgain() {
        // logic đặt lại đơn hàng
    }
}

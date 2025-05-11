package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.OrderItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class AdminOrderViewModel : ViewModel() {
    private val _orders = MutableStateFlow<List<OrderItem>>(emptyList())
    val orders: StateFlow<List<OrderItem>> = _orders

    private val db = FirebaseFirestore.getInstance()

    init {
        fetchAllOrders()
    }

    internal fun fetchAllOrders() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("orders")
                    .orderBy("orderDate", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val orderList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(OrderItem::class.java)?.copy(orderId = doc.id)
                }
                _orders.value = orderList
            } catch (e: Exception) {
                println("Lỗi khi tải đơn hàng: ${e.message}")
            }
        }
    }
}

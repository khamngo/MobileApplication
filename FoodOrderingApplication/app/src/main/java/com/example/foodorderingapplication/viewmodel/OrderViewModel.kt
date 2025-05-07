package com.example.foodorderingapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.foodorderingapplication.model.OrderStatus
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.FoodItem
import com.example.foodorderingapplication.model.OrderItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class OrderViewModel : ViewModel() {
    private val _orders = MutableStateFlow<List<OrderItem>>(emptyList())
    val orders: StateFlow<List<OrderItem>> = _orders

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _recentOrders = MutableStateFlow<List<FoodItem>>(emptyList())
    val recentOrders: StateFlow<List<FoodItem>> = _recentOrders

    init {
        fetchUserOrders()
        fetchRecentOrders()
    }

    private fun fetchRecentOrders() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("orders")
            .whereEqualTo("userId", userId)
//            .orderBy("orderDate", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                val items = result.flatMap { document ->
                    val orderItems = document["items"] as? List<HashMap<String, Any>> ?: emptyList()
                    orderItems.mapNotNull {
                        try {
                            FoodItem(
                                id = it["foodId"] as? String ?: "",
                                name = it["name"] as? String ?: "",
                                price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                                imageUrl = it["imageUrl"] as? String ?: "",
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }

                // Lọc trùng lặp theo foodId
                val distinctItems = items.distinctBy { it.id }

                // Gán vào LiveData/StateFlow
                _recentOrders.value = distinctItems
            }

            .addOnFailureListener { e ->
                println("Order taking error: $e")
            }
    }

    private fun fetchUserOrders() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("orders")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val orderList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(OrderItem::class.java)?.copy(orderId = doc.id)
                }
                _orders.value = orderList.sortedByDescending { it.orderDate }
            } catch (e: Exception) {
                println("Error loading order: ${e.message}")
            }
        }
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("orders")
                    .get()
                    .await()

                val orderList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(OrderItem::class.java)?.copy(orderId = doc.id)
                }
                _orders.value = orderList.sortedByDescending { it.orderDate }
            } catch (e: Exception) {
                println("Error loading order: ${e.message}")
            }
        }
    }
}

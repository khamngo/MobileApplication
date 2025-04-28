package com.example.foodorderingapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.OrderItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _orderDetail = MutableStateFlow<OrderItem?>(null)
    val orderDetail: StateFlow<OrderItem?> = _orderDetail.asStateFlow()

    fun fetchOrderDetail(orderId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("orders")
                    .document(orderId)
                    .get()
                    .await()

                val order = snapshot.toObject(OrderItem::class.java)?.copy(orderId = snapshot.id)
                _orderDetail.value = order
            } catch (e: Exception) {
                println("Lỗi lấy chi tiết đơn hàng: ${e.message}")
            }
        }
    }

    fun acceptOrder() {
        viewModelScope.launch {
            _orderDetail.value?.let { currentOrder ->
                val updatedOrder = currentOrder.copy(status = "Completed")
                _orderDetail.value = updatedOrder
                try {
                    db.collection("orders")
                        .document(updatedOrder.orderId)
                        .update("status", "Completed")
                        .addOnSuccessListener {
                            Log.d("OrderUpdate", "Update status to Completed thành công.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("OrderUpdate", "Lỗi update Firestore", e)
                        }

                } catch (e: Exception) {
                    Log.e("OrderUpdate", "Exception trong acceptOrder", e)
                }
            }
        }
    }

    fun cancelOrder() {
        viewModelScope.launch {
            _orderDetail.value?.let { currentOrder ->
                val updatedOrder = currentOrder.copy(status = "Cancelled")
                _orderDetail.value = updatedOrder

                try {
                    db.collection("orders")
                        .document(updatedOrder.orderId)
                        .update("status", "Cancelled")
                        .addOnSuccessListener {
                            Log.d("OrderUpdate", "Update status to Cancelled thành công.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("OrderUpdate", "Lỗi update Firestore", e)
                        }
                } catch (e: Exception) {
                    Log.e("OrderUpdate", "Exception trong cancelOrder", e)
                }
            }
        }
    }

}

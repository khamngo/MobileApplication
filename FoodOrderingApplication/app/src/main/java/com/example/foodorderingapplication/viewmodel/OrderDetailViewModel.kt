package com.example.foodorderingapplication.viewmodel

import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.model.OrderItem
import com.example.foodorderingapplication.model.OrderStatus
import com.example.foodorderingapplication.model.RestaurantItem
import com.example.foodorderingapplication.model.ShippingAddress
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _orderDetail = MutableStateFlow<OrderItem?>(null)
    val orderDetail: StateFlow<OrderItem?> = _orderDetail.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasReviewed = MutableStateFlow(true)
    val hasReviewed: StateFlow<Boolean> = _hasReviewed.asStateFlow()

    // Lấy chi tiết đơn hàng
    fun fetchOrderDetail(orderId: String) {
        _isLoading.value = true
        db.collection("orders").document(orderId)
            .addSnapshotListener { snapshot, e ->
                viewModelScope.launch {
                    if (e != null) {
                        _errorMessage.value = "Error fetching order: ${e.message}"
                        _isLoading.value = false
                        return@launch
                    }
                    if (snapshot != null && snapshot.exists()) {
                        try {
                            val data = snapshot.data
                            val items = (data?.get("items") as? List<Map<String, Any>>)?.map {
                                CartItem(
                                    foodId = it["foodId"] as? String ?: "",
                                    name = it["name"] as? String ?: "",
                                    price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                                    quantity = (it["quantity"] as? Number)?.toInt() ?: 1,
                                    imageUrl = it["imageUrl"] as? String ?: "",
                                    portion = it["portion"] as? String ?: "",
                                    drink = it["drink"] as? String ?: "",
                                    instructions = it["instructions"] as? String ?: ""
                                )
                            } ?: emptyList()

                            val shippingAddressData = data?.get("shippingAddress") as? Map<String, Any>
                            val restaurantData = shippingAddressData?.get("restaurant") as? Map<String, Any>
                            val shippingAddress = ShippingAddress(
                                firstName = shippingAddressData?.get("firstName") as? String ?: "",
                                lastName = shippingAddressData?.get("lastName") as? String ?: "",
                                phoneNumber = shippingAddressData?.get("phoneNumber") as? String ?: "",
                                province = shippingAddressData?.get("province") as? String ?: "",
                                district = shippingAddressData?.get("district") as? String ?: "",
                                ward = shippingAddressData?.get("ward") as? String ?: "",
                                street = shippingAddressData?.get("street") as? String ?: "",
                                restaurant = RestaurantItem(
                                    name = restaurantData?.get("name") as? String ?: "",
                                    address = restaurantData?.get("address") as? String ?: "",
                                    phone = restaurantData?.get("phone") as? String ?: "",
                                    hours = restaurantData?.get("hours") as? String ?: ""
                                ),
                                isDefault = shippingAddressData?.get("isDefault") as? Boolean ?: false
                            )

                            _orderDetail.value = OrderItem(
                                userId = data?.get("userId") as? String ?: "",
                                items = items,
                                subtotal = (data?.get("subtotal") as? Number)?.toDouble() ?: 0.0,
                                shippingFee = (data?.get("shippingFee") as? Number)?.toDouble() ?: 0.0,
                                taxes = (data?.get("taxes") as? Number)?.toDouble() ?: 0.0,
                                discount = (data?.get("discount") as? Number)?.toDouble() ?: 0.0,
                                total = (data?.get("total") as? Number)?.toDouble() ?: 0.0,
                                shippingAddress = shippingAddress,
                                deliveryDate = data?.get("deliveryDate") as? String ?: "",
                                deliveryTime = data?.get("deliveryTime") as? String ?: "",
                                promo = data?.get("promo") as? String ?: "",
                                paymentMethod = data?.get("paymentMethod") as? String ?: "",
                                orderDate = data?.get("orderDate") as? Timestamp ?: Timestamp.now(),
                                status = data?.get("status") as? String ?: "Preparing",
                                orderId = snapshot.id
                            )
                            checkHasReviewed(orderId, items)
                            _errorMessage.value = ""
                        } catch (ex: Exception) {
                            _errorMessage.value = "Error parsing order: ${ex.message}"
                        }
                    } else {
                        _errorMessage.value = "Order not found"
                    }
                    _isLoading.value = false
                }
            }
    }

    private fun checkHasReviewed(orderId: String, items: List<CartItem>) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val foodIds = items.map { it.foodId }
                if (foodIds.isEmpty()) {
                    _hasReviewed.value = true
                    return@launch
                }
                val snapshot = db.collection("reviews")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("orderId", orderId)
                    .whereIn("foodId", foodIds)
                    .get()
                    .await()
                _hasReviewed.value = snapshot.size() >= foodIds.size
            } catch (e: Exception) {
                _errorMessage.value = "Error checking reviews"
            }
        }
    }

    // Hủy đơn hàng
    fun cancelOrder(orderId: String, onConfirm: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val order = _orderDetail.value ?: throw IllegalArgumentException("Order not found")
                if (order.status != OrderStatus.Preparing.name) {
                    throw IllegalArgumentException("Can only cancel orders in Preparing status")
                }
                db.collection("orders").document(orderId)
                    .update("status", OrderStatus.Cancelled.name)
                    .await()
                _errorMessage.value = "Order cancelled successfully"
                sendNotification(
                    title = "Order Cancelled",
                    body = "Your order #${order.orderId} has been cancelled."
                )
                onConfirm()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error cancelling order"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Mua lại
    fun buyAgain(orderId: String, deliveryDate: String, deliveryTime: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val selectedDate = dateFormat.parse(deliveryDate)
                if (selectedDate != null) {
                    if (selectedDate.before(Calendar.getInstance().time)) {
                        throw IllegalArgumentException("Delivery date must be in the future")
                    }
                }
                val order = _orderDetail.value ?: throw IllegalArgumentException("Order not found")
                val newOrder = order.copy(
                    orderId = db.collection("orders").document().id,
                    status = OrderStatus.Preparing.name,
                    orderDate = Timestamp.now(),
                    userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    deliveryDate = deliveryDate,
                    deliveryTime = deliveryTime
                )
                db.collection("orders").document(newOrder.orderId)
                    .set(newOrder)
                    .await()
                _errorMessage.value = "New order created successfully"
                sendNotification(
                    title = "New Order Created",
                    body = "Your new order #${newOrder.orderId} has been placed."
                )
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error creating new order"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Gửi thông báo qua FCM
    private fun sendNotification(title: String, body: String) {
        viewModelScope.launch {
            try {
                val message = mapOf(
                    "to" to "/topics/user_${FirebaseAuth.getInstance().currentUser?.uid}",
                    "notification" to mapOf(
                        "title" to title,
                        "body" to body
                    )
                )
                // Lưu vào Firestore
                db.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                    .collection("notifications")
                    .add(
                        mapOf(
                            "title" to title,
                            "body" to body,
                            "timestamp" to Timestamp.now()
                        )
                    ).await()
                println("Sending FCM: $message")
            } catch (e: Exception) {
                _errorMessage.value = "Error sending notification: ${e.message}"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = ""
    }

    override fun onCleared() {
        db.collection("orders").document()
            .addSnapshotListener { _, _ -> }.remove()
        super.onCleared()
    }
}
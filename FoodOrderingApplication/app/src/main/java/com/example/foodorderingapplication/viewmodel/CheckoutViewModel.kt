package com.example.foodorderingapplication.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.model.NotificationItem
import com.example.foodorderingapplication.model.OrderItem
import com.example.foodorderingapplication.model.RestaurantItem
import com.example.foodorderingapplication.model.ShippingAddress
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
class CheckoutViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Giỏ hàng
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Subtotal
    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()

    // Mã khuyến mãi
    private val _selectedPromo = MutableStateFlow("Free Shipping")
    val selectedPromo: StateFlow<String> = _selectedPromo.asStateFlow()

    // Giảm giá dựa trên promo
    val discount: StateFlow<Double> = combine(_selectedPromo, _subtotal) { promo, subtotal ->
        when (promo) {
            "Free Shipping" -> 0.0
            "5% off for orders above 5$" -> if (subtotal > 5.0) subtotal * 0.05 else 0.0
            "10% off for orders above 10$" -> if (subtotal > 10.0) subtotal * 0.10 else 0.0
            "15% off for orders above 20$" -> if (subtotal > 20.0) subtotal * 0.15 else 0.0
            else -> 0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val taxes = 2.0
    val shippingFee = 2.0

    val total: StateFlow<Double> = combine(_subtotal, discount) { sub, dis ->
        val fee = if (_selectedPromo.value == "Free Shipping" || dis > 0.0) 0.0 else shippingFee
        val rawTotal = (sub - dis + taxes + fee).coerceAtLeast(0.0)
        kotlin.math.round(rawTotal * 100) / 100.0 // Làm tròn 2 chữ số
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Thông tin giao hàng
    private val _shippingAddress = MutableStateFlow(ShippingAddress())
    val shippingAddress: StateFlow<ShippingAddress> = _shippingAddress.asStateFlow()

    private val dateFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.ENGLISH)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    private val calendar = Calendar.getInstance()

    private val _deliveryDate = MutableStateFlow(dateFormat.format(calendar.time))
    val deliveryDate: StateFlow<String> = _deliveryDate.asStateFlow()

    private val _deliveryTime = MutableStateFlow(timeFormat.format(calendar.time))
    val deliveryTime: StateFlow<String> = _deliveryTime.asStateFlow()

    // Phương thức thanh toán
    private val _paymentMethod = MutableStateFlow("COD")
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    // Trạng thái kiểm tra địa chỉ hợp lệ
    private val _isShippingAddressValid = MutableStateFlow(false)
    val isShippingAddressValid: StateFlow<Boolean> = _isShippingAddressValid.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    init {
        loadCartItems()
        loadShippingAddress()
    }

    // Tải giỏ hàng từ Firestore
    private fun loadCartItems() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            db.collection("carts").document(userId)
                .collection("items")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        _errorMessage.value = "Error while getting cart: $e"
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val items = snapshot.map { doc ->
                            CartItem(
                                foodId = doc.id,
                                name = doc.getString("name") ?: "",
                                price = doc.getDouble("price") ?: 0.0,
                                quantity = (doc.getLong("quantity") ?: 0L).toInt(),
                                imageUrl = doc.getString("imageUrl") ?: "",
                                portion = doc.getString("portion") ?: "",
                                drink = doc.getString("drink") ?: "",
                                instructions = doc.getString("instructions") ?: ""
                            )
                        }
                        _cartItems.value = items
                        updateSubtotal()
                    }
                }
        }
    }

    // Cập nhật subtotal
    private fun updateSubtotal() {
        val subtotal = _cartItems.value.sumOf { it.price * it.quantity }
        _subtotal.value = (subtotal * 100).roundToInt() / 100.0
    }

    // Tải địa chỉ giao hàng từ Firestore
    private fun loadShippingAddress() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            db.collection("users").document(userId)
                .collection("shippingAddress").document("default")
                .get()
                .addOnSuccessListener { doc ->
                    val restaurantMap = doc.get("restaurant") as? Map<*, *>
                    val restaurant = if (restaurantMap != null) {
                        RestaurantItem(
                            name = restaurantMap["name"] as? String ?: "",
                            address = restaurantMap["address"] as? String ?: "",
                            phone = restaurantMap["phone"] as? String ?: "",
                            hours = restaurantMap["hours"] as? String ?: ""
                        )
                    } else RestaurantItem()

                    if (doc.exists()) {
                        _shippingAddress.value = ShippingAddress(
                            firstName = doc.getString("firstName") ?: "",
                            lastName = doc.getString("lastName") ?: "",
                            phoneNumber = doc.getString("phoneNumber") ?: "",
                            province = doc.getString("province") ?: "",
                            district = doc.getString("district") ?: "",
                            ward = doc.getString("ward") ?: "",
                            street = doc.getString("street") ?: "",
                            restaurant = restaurant,
                            isDefault = doc.getBoolean("isDefault") == true
                        )
                        validateShippingAddress()
                    } else {
                        _shippingAddress.value = ShippingAddress()
                        _isShippingAddressValid.value = false
                    }
                }
                .addOnFailureListener { e ->
                    _errorMessage.value = "Error loading address: $e"
                }
        }
    }

    fun reloadShippingAddress() {
        loadShippingAddress()
    }

    // Kiểm tra tính hợp lệ của địa chỉ
    private fun validateShippingAddress() {
        val address = _shippingAddress.value
        _isShippingAddressValid.value = address.firstName.isNotBlank() &&
                address.lastName.isNotBlank() &&
                address.phoneNumber.isNotBlank() &&
                address.province.isNotBlank() &&
                address.district.isNotBlank() &&
                address.ward.isNotBlank() &&
                address.street.isNotBlank()
    }

    // Cập nhật ngày giờ giao hàng
    fun updateDeliveryTime(date: String, time: String) {
        _deliveryDate.value = date
        _deliveryTime.value = time
    }

    // Cập nhật mã khuyến mãi
    fun updatePromo(promo: String) {
        _selectedPromo.value = promo
    }

    // Cập nhật phương thức thanh toán
    fun updatePaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder: StateFlow<Boolean> = _isPlacingOrder.asStateFlow()

    // Đặt hàng
    fun placeOrder() {
        Log.d("Notification saved:", _isShippingAddressValid.toString())

        if (!_isShippingAddressValid.value) {

            _errorMessage.value = "Please add a shipping address before placing the order"
            return
        }

        val userId = auth.currentUser?.uid ?: run {
            _errorMessage.value = "User not logged in"
            return
        }
        val orderId = UUID.randomUUID().toString()
        val orderItem = OrderItem(
            userId = userId,
            orderId = orderId,
            items = _cartItems.value,
            subtotal = _subtotal.value,
            shippingFee = if (_selectedPromo.value == "Free Shipping") 0.0 else 5_000.0,
            taxes = taxes,
            discount = discount.value,
            total = total.value,
            shippingAddress = _shippingAddress.value,
            deliveryDate = _deliveryDate.value,
            deliveryTime = _deliveryTime.value,
            promo = _selectedPromo.value,
            paymentMethod = _paymentMethod.value,
            orderDate = Timestamp.now(),
            status = "Preparing"
        )

        viewModelScope.launch {
            _isPlacingOrder.value = true
            try {
                delay(2000)
                db.collection("orders").document(orderId)
                    .set(orderItem)
                    .addOnSuccessListener {
                        clearCart(userId)
                        _errorMessage.value = "Order placed successfully"
                    }
                    .addOnFailureListener { e ->
                        _errorMessage.value = "Error when ordering: $e"
                    }
                sendNotification("Order Placed", "Your order #${orderId.takeLast(5)} has been placed successfully!",orderId)
            } catch (e: Exception) {
                _errorMessage.value = "Error placing order: $e"
            } finally {
                _isPlacingOrder.value = false
            }
        }
    }

    // Xóa giỏ hàng
    private fun clearCart(userId: String) {
        viewModelScope.launch {
            val batch = db.batch()
            db.collection("carts").document(userId)
                .collection("items")
                .get()
                .addOnSuccessListener { snapshot ->
                    for (doc in snapshot) {
                        batch.delete(doc.reference)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            _cartItems.value = emptyList()
                            _subtotal.value = 0.0
                        }
                        .addOnFailureListener { e ->
                            _errorMessage.value = "Error clearing cart: $e"
                        }
                }
        }
    }

    private fun sendNotification(title: String, body: String, orderId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                val notificationId = UUID.randomUUID().toString()
                val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                val time = timeFormatter.format(Date())

                val notification = NotificationItem(
                    id = notificationId,
                    title = title,
                    message = body,
                    orderId = orderId,
                    timestamp = Timestamp.now(),
                    isRead = false,
                    type = "order", // Có thể tùy chỉnh loại thông báo
                    time = time,
                    dotColor = Color.Blue
                )

                // Lưu NotificationItem vào Firestore
                db.collection("users")
                    .document(userId)
                    .collection("notifications")
                    .document(notificationId)
                    .set(notification)
                    .await()

                println("Notification saved: $notification")
            } catch (e: Exception) {
                _errorMessage.value = "Error sending notification: ${e.message}"
            }
        }
    }

}
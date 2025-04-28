package com.example.foodorderingapplication.viewmodel

import android.net.http.HttpResponseCache.install
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.util.CoilUtils.result
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.model.MoMoRequest
import com.example.foodorderingapplication.model.MoMoResponse
import com.example.foodorderingapplication.model.OrderItem
import com.example.foodorderingapplication.model.ShippingAddress
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

class CheckoutViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Giỏ hàng
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    // Subtotal
    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal

    // Mã khuyến mãi
    private val _selectedPromo = MutableStateFlow("Free Shipping")
    val selectedPromo: StateFlow<String> = _selectedPromo

    // Giảm giá dựa trên promo
    val discount: StateFlow<Double> = _selectedPromo.map { promo ->
        when (promo) {
            "Free Shipping" -> 0.0
            "5% off for orders above 5$" -> if (_subtotal.value > 5.0) _subtotal.value * 0.05 else 0.0
            "10% off for orders above 10$" -> if (_subtotal.value > 10.0) _subtotal.value * 0.10 else 0.0
            "15% off for orders above 20$" -> if (_subtotal.value > 20.0) _subtotal.value * 0.15 else 0.0
            else -> 0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Tổng tiền (bao gồm thuế, phí giao hàng)
    val total: StateFlow<Double> = combine(_subtotal, discount) { sub, dis ->
        val taxes = 2.0
        val shippingFee = if (dis == 0.0) 0.0 else 2.0
        val result = (sub - dis + taxes + shippingFee).coerceAtLeast(0.0)

        Log.d("TOTAL_DEBUG", "Subtotal: $sub, Discount: $dis, Total: $result")
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)


    // Thông tin giao hàng
    private val _shippingAddress = MutableStateFlow(ShippingAddress())
    val shippingAddress: StateFlow<ShippingAddress> = _shippingAddress

    private val _deliveryDate = MutableStateFlow("Today")
    val deliveryDate: StateFlow<String> = _deliveryDate

    private val _deliveryTime = MutableStateFlow("Now")
    val deliveryTime: StateFlow<String> = _deliveryTime

    // Phương thức thanh toán
    private val _paymentMethod = MutableStateFlow("Cash on Delivery")
    val paymentMethod: StateFlow<String> = _paymentMethod

    private val _orderStatus = MutableStateFlow<String?>(null)
    val orderStatus: StateFlow<String?> = _orderStatus.asStateFlow()

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
                        println("Lỗi khi lấy giỏ hàng: $e")
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
        _subtotal.value = subtotal
    }

    // Tải địa chỉ giao hàng từ Firestore
    private fun loadShippingAddress() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            db.collection("users").document(userId)
                .collection("shippingAddress").document("default")
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        _shippingAddress.value = ShippingAddress(
                            firstName = doc.getString("firstName") ?: "",
                            lastName = doc.getString("lastName") ?: "",
                            phoneNumber = doc.getString("phoneNumber") ?: "",
                            province = doc.getString("province") ?: "",
                            district = doc.getString("district") ?: "",
                            ward = doc.getString("ward") ?: "",
                            street = doc.getString("street") ?: "",
                            isDefault = doc.getBoolean("isDefault") == true
                        )
                    }
                }
                .addOnFailureListener { e ->
                    println("Lỗi khi tải địa chỉ: $e")
                }
        }
    }

    fun reloadShippingAddress() {
        loadShippingAddress()
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

    // Đặt hàng
    fun placeOrder() {
        val userId = auth.currentUser?.uid ?: return
        val orderId = UUID.randomUUID().toString()
        val orderItem = OrderItem(
            userId = userId,
            items = _cartItems.value,
            subtotal = _subtotal.value,
            shippingFee = if (_selectedPromo.value == "Free Shipping") 0.0 else 5_000.0,
            taxes = 2_000.0,
            discount = discount.value,
            total = total.value,
            shippingAddress = _shippingAddress.value,
            deliveryDate = _deliveryDate.value,
            deliveryTime = _deliveryTime.value,
            promo = _selectedPromo.value,
            paymentMethod = _paymentMethod.value,
            orderDate = Timestamp.now(),
            status = "Preparing" // THÊM TRẠNG THÁI ĐƠN HÀNG
        )

        viewModelScope.launch {
            db.collection("orders").document(orderId)
                .set(orderItem)
                .addOnSuccessListener {
                    // Xóa giỏ hàng sau khi đặt hàng
                    clearCart(userId)
                    println("Đã đặt hàng thành công!")
                }
                .addOnFailureListener { e ->
                    println("Lỗi khi đặt hàng: $e")
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
                            println("Đã xóa giỏ hàng")
                        }
                }
        }
    }
}
package com.example.foodorderingapplication.model

import com.google.firebase.Timestamp

data class Order(
    val id: Int,
    val time: String,
    val productName: String,
    val description: String,
    val price: Double,
    val note: String,
    val total: Double,
    val status: String
)

data class OrderItem1(val id: Int, val foodName: String, val date: String, val status: String)

data class OrderItem(
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val shippingFee: Double = 0.0,
    val taxes: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val shippingAddress: ShippingAddress = ShippingAddress(),
    val deliveryDate: String = "",
    val deliveryTime: String = "",
    val promo: String = "",
    val paymentMethod: String = "",
    val orderDate: Timestamp = Timestamp.now(),
    val status: String = "Preparing", // NEW FIELD
    val orderId: String = ""          // optional để lấy về từ Firestore document id
)

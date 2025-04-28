package com.example.foodorderingapplication.model

import com.google.firebase.Timestamp

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
    val status: String = "Preparing",
    val orderId: String = ""
)

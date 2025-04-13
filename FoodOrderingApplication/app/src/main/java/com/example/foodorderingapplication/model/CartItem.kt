package com.example.foodorderingapplication.model

data class CartItem(
    val foodId: String,
    val imageRes: String,
    val name: String,
    val price: Double,
    var quantity: Int
)

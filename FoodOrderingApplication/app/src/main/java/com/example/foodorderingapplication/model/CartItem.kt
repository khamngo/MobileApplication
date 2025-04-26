package com.example.foodorderingapplication.model

data class CartItem(
    val foodId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val imageUrl: String = "",
    val portion: String = "",
    val drink: String = "",
    val instructions: String = ""
)

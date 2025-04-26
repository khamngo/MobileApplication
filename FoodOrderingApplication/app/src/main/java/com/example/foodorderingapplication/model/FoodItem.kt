package com.example.foodorderingapplication.model

data class FoodItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val rating: Double = 0.0,
    val price: Double = 0.0,
    val imageUrl: String = "",
    val tags: List<String> = emptyList()
)
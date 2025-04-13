package com.example.foodorderingapplication.model

data class FoodItem(
    val id: String,
    val name: String,
    val description: String, val price: Double, val rating: Double, val imageRes: String
)
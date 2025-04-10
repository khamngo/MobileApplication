package com.example.foodorderingapplication.models

data class Food(
    val id: String,
    val name: String,
    val description: String, val price: Double, val rating: Double, val imageRes: String
)
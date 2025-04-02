package com.example.foodorderingapplication.models

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

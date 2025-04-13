package com.example.foodorderingapplication.model

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

data class OrderItem(val id: Int, val foodName: String, val date: String, val status: String)

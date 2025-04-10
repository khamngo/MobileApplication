package com.example.foodorderingapplication.models

data class Cart(val foodId: String, val imageRes: String, val name: String, val price: Double, var quantity: Int)

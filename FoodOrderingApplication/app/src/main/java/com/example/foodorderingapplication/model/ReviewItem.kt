package com.example.foodorderingapplication.model

data class ReviewItem(
    val foodId: String = "",
    val foodName: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val rating: Int = 0,
    val reviewText: String = "",
    val date: String = ""
)
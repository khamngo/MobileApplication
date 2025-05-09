package com.example.foodorderingapplication.model

import com.google.firebase.Timestamp

data class ReviewItem(
    val reviewId: String = "",
    val foodId: String = "",
    val foodName: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val rating: Int = 0,
    val reviewText: String = "",
    val date: Timestamp = Timestamp.now(),
    val reviewer: String = "Anonymous",
)
package com.example.foodorderingapplication.model

import com.google.firebase.Timestamp

data class ReviewState(
    val foodItems: List<FoodItem> = emptyList(),
    val ratings: Map<String, Int> = emptyMap(), // foodId -> rating
    val reviewTexts: Map<String, String> = emptyMap(), // foodId -> reviewText
    val date: Timestamp? = null, // Thêm trường date
    val showError: Boolean = false,
    val submitSuccess: Boolean = false,
    val isLoading: Boolean = false
)
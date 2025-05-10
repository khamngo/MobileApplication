package com.example.foodorderingapplication.model

import com.google.firebase.Timestamp

data class ReviewState(
    val foodItems: List<FoodItem> = emptyList(),
    val ratings: Map<String, Int> = emptyMap(),
    val reviewTexts: Map<String, String> = emptyMap(),
    val date: Timestamp? = null,
    val showError: Boolean = false,
    val submitSuccess: Boolean = false,
    val isLoading: Boolean = false
)
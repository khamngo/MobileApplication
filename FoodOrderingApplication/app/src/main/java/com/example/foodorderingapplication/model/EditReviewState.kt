package com.example.foodorderingapplication.model

data class EditReviewState(
    val foodItem: FoodItem = FoodItem(),
    val rating: Int = 0,
    val reviewText: String = "",
    val showError: Boolean = false,
    val submitSuccess: Boolean = false,
    val isLoading: Boolean = false
)
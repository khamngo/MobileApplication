package com.example.foodorderingapplication.model

data class ReviewUiState(
    val rating: Int = 0,
    val reviewText: String = "",
    val showError: Boolean = false,
    val submitSuccess: Boolean = false
)

package com.example.foodorderingapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.foodorderingapplication.model.ReviewUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ReviewViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState

    fun onRatingChange(newRating: Int) {
        _uiState.value = _uiState.value.copy(rating = newRating, showError = false)
    }

    fun onReviewTextChange(newText: String) {
        _uiState.value = _uiState.value.copy(reviewText = newText, showError = false)
    }

    fun submitReview() {
        val state = _uiState.value
        if (state.rating == 0 || state.reviewText.isBlank()) {
            _uiState.value = state.copy(showError = true, submitSuccess = false)
        } else {
            // Gửi đánh giá (nếu có gọi API thì đặt ở đây)
            _uiState.value = state.copy(showError = false, submitSuccess = true)
            println("Gửi đánh giá: ${state.rating} sao - ${state.reviewText}")
        }
    }
}


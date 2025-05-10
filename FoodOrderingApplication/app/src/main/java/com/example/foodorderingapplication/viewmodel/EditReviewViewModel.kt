package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.EditReviewState
import com.example.foodorderingapplication.model.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditReviewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(EditReviewState())
    val uiState: StateFlow<EditReviewState> = _uiState.asStateFlow()

    fun fetchReview(reviewId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val doc = db.collection("reviews").document(reviewId).get().await()
                if (doc.exists()) {
                    val foodId = doc.getString("foodId") ?: ""
                    val foodDoc = db.collection("foods").document(foodId).get().await()
                    _uiState.value = _uiState.value.copy(
                        foodItem = FoodItem(
                            id = foodId,
                            name = foodDoc.getString("name") ?: "",
                            imageUrl = foodDoc.getString("imageUrl") ?: "",
                            description = foodDoc.getString("description") ?: ""
                        ),
                        rating = (doc.getLong("rating") ?: 0).toInt(),
                        reviewText = doc.getString("reviewText") ?: "",
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        showError = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    showError = true,
                    isLoading = false
                )
            }
        }
    }

    fun onRatingChange(rating: Int) {
        _uiState.value = _uiState.value.copy(
            rating = rating,
            showError = false
        )
    }

    fun onReviewTextChange(text: String) {
        _uiState.value = _uiState.value.copy(
            reviewText = text,
            showError = false
        )
    }

    fun updateReview(reviewId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
                if (_uiState.value.rating == 0 || _uiState.value.reviewText.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        showError = true,
                        isLoading = false
                    )
                    return@launch
                }
                db.collection("reviews").document(reviewId)
                    .update(
                        mapOf(
                            "rating" to _uiState.value.rating,
                            "reviewText" to _uiState.value.reviewText,
                            "timestamp" to com.google.firebase.Timestamp.now()
                        )
                    )
                    .await()
                _uiState.value = _uiState.value.copy(
                    submitSuccess = true,
                    isLoading = false,
                    showError = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    showError = true,
                    isLoading = false
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = EditReviewState()
    }
}
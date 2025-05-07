package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.FoodItem
import com.example.foodorderingapplication.model.ReviewState
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReviewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ReviewState())
    val uiState: StateFlow<ReviewState> = _uiState.asStateFlow()

    fun fetchOrderItems(orderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val orderDoc = db.collection("orders").document(orderId).get().await()
                val items = (orderDoc.get("items") as? List<Map<String, Any>>)?.map {
                    val foodId = it["foodId"] as? String ?: ""
                    val foodDoc = db.collection("foods").document(foodId).get().await()
                    FoodItem(
                        id = foodId,
                        name = foodDoc.getString("name") ?: "",
                        imageUrl = foodDoc.getString("imageUrl") ?: "",
                        description = foodDoc.getString("description") ?: ""
                    )
                } ?: emptyList()
                _uiState.value = _uiState.value.copy(
                    foodItems = items,
                    isLoading = false,
                    ratings = items.associate { item -> item.id to 5 },
                    reviewTexts = items.associate { item -> item.id to "" }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    showError = true,
                    isLoading = false
                )
            }
        }
    }

    fun onRatingChange(foodId: String, rating: Int) {
        _uiState.value = _uiState.value.copy(
            ratings = _uiState.value.ratings + (foodId to rating),
            showError = false
        )
    }

    fun onReviewTextChange(foodId: String, text: String) {
        _uiState.value = _uiState.value.copy(
            reviewTexts = _uiState.value.reviewTexts + (foodId to text),
            showError = false
        )
    }

    fun submitReview(orderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
                val batch = db.batch()
                _uiState.value.foodItems.forEach { foodItem ->
                    val rating = _uiState.value.ratings[foodItem.id] ?: 0
                    val reviewText = _uiState.value.reviewTexts[foodItem.id] ?: ""
                    if (rating > 0 && reviewText.isNotBlank()) {
                        val reviewId = db.collection("reviews").document().id
                        val reviewData = mapOf(
                            "userId" to userId,
                            "foodId" to foodItem.id,
                            "orderId" to orderId,
                            "rating" to rating,
                            "reviewText" to reviewText,
                            "timestamp" to Timestamp.now()
                        )
                        batch.set(db.collection("reviews").document(reviewId), reviewData)
                    }
                }
                batch.commit().await()
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
        _uiState.value = ReviewState()
    }

    fun checkReviewedItems(orderId: String): StateFlow<List<String>> {
        val result = MutableStateFlow<List<String>>(emptyList())
        val userId = auth.currentUser?.uid ?: return result
        db.collection("reviews")
            .whereEqualTo("userId", userId)
            .whereEqualTo("orderId", orderId)
            .addSnapshotListener { snapshot, _ ->
                result.value = snapshot?.documents?.mapNotNull { it.getString("foodId") } ?: emptyList()
            }
        return result
    }
}

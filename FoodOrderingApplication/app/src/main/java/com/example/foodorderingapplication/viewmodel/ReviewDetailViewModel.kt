package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.ReviewItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val reviewListViewModel = ReviewListViewModel() // Tạm thời để gọi hàm cập nhật

    private val _reviews = MutableStateFlow<List<ReviewItem>>(emptyList())
    val reviews: StateFlow<List<ReviewItem>> = _reviews.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun fetchReviews(foodId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Lấy thông tin món ăn trước
                val foodDoc = db.collection("foods").document(foodId).get().await()
                if (!foodDoc.exists()) {
                    _errorMessage.value = "Food not found"
                    _isLoading.value = false
                    return@launch
                }
                val foodName = foodDoc.getString("name") ?: "Unknown"
                val imageUrl = foodDoc.getString("imageUrl") ?: ""
                val description = foodDoc.getString("description") ?: ""

                // Lấy danh sách đánh giá
                val snapshot = db.collection("reviews")
                    .whereEqualTo("foodId", foodId)
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
                val reviewsList = snapshot.documents.mapNotNull { doc ->
                    try {
                        val userId = doc.getString("userId") ?: ""
                        val userProfile = db.collection("users").document(userId).collection("profile").document("info").get().await()
                        val reviewer = userProfile.getString("username") ?: "Anonymous"
                        val timestamp = doc.getTimestamp("timestamp")
                        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        ReviewItem(
                            reviewId = doc.id,
                            foodId = foodId,
                            foodName = foodName,
                            imageUrl = imageUrl,
                            description = description,
                            rating = (doc.getLong("rating") ?: 0).toInt(),
                            reviewText = doc.getString("reviewText") ?: "",
                            date = timestamp?.toDate()?.let { dateFormat.format(it) } ?: "",
                            reviewer = reviewer
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                _reviews.value = reviewsList

                // Cập nhật rating và reviewCount lên Firestore
                val averageRating = if (reviewsList.isNotEmpty()) reviewsList.map { it.rating }.average().toDouble() else 0.0
                reviewListViewModel.updateFoodRatingAndCount(foodId, averageRating, reviewsList.size)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error fetching reviews"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
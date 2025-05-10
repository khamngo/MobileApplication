package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.FoodItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

class ReviewListViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val foodItems: StateFlow<List<FoodItem>> = _foodItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    init {
        fetchFoodItems()
    }

    private fun fetchFoodItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val foodSnapshot = db.collection("foods").get().await()
                val foodItems = foodSnapshot.documents.mapNotNull { doc ->
                    try {
                        val foodId = doc.id
                        val reviewSnapshot = db.collection("reviews")
                            .whereEqualTo("foodId", foodId)
                            .get()
                            .await()
                        val reviews = reviewSnapshot.documents
                        val reviewCount = reviews.size
                        val averageRating = if (reviews.isNotEmpty()) {
                            reviews.mapNotNull { it.getLong("rating")?.toDouble() }.average()
                        } else {
                            0.0
                        }
                        FoodItem(
                            id = foodId,
                            name = doc.getString("name") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            description = doc.getString("description") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            rating = averageRating,
                            reviewCount = reviewCount,
                            tags = doc.get("tags") as? List<String> ?: emptyList()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                _foodItems.value = foodItems
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error fetching food items"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm cập nhật rating và reviewCount lên Firestore
    fun updateFoodRatingAndCount(foodId: String, rating: Double, reviewCount: Int) {
        viewModelScope.launch {
            try {
                db.collection("foods").document(foodId)
                    .update(
                        mapOf(
                            "rating" to rating,
                            "reviewCount" to reviewCount
                        )
                    )
                    .await()
                fetchFoodItems() // Làm mới danh sách sau khi cập nhật
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error updating food data"
            }
        }
    }
}
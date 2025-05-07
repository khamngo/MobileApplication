package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.ReviewItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class MyReviewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _reviews = MutableStateFlow<List<ReviewItem>>(emptyList())
    val reviews: StateFlow<List<ReviewItem>> = _reviews.asStateFlow()

    init {
        fetchReviews()
    }

    internal fun fetchReviews() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("reviews")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                viewModelScope.launch {
                    if (e != null) {
                        return@launch
                    }
                    if (snapshot != null) {
                        val reviewsList = snapshot.documents.mapNotNull { doc ->
                            try {
                                val foodId = doc.getString("foodId") ?: ""
                                val foodDoc = db.collection("foods").document(foodId).get().await()
                                val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                ReviewItem(
                                    reviewId = doc.id,
                                    foodId = foodId,
                                    foodName = foodDoc.getString("name") ?: "",
                                    imageUrl = foodDoc.getString("imageUrl") ?: "",
                                    description = foodDoc.getString("description") ?: "",
                                    rating = (doc.getLong("rating") ?: 0).toInt(),
                                    reviewText = doc.getString("reviewText") ?: "",
                                    date = dateFormat.format(timestamp.toDate())
                                )
                            } catch (ex: Exception) {
                                null
                            }
                        }
                        _reviews.value = reviewsList
                    }
                }
            }
    }

    fun deleteReview(reviewId: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("reviews")
            .document(reviewId)
            .delete()
            .addOnSuccessListener {
                // Xoá review khỏi danh sách hiện tại
                _reviews.value = _reviews.value.filterNot { it.reviewId == reviewId }
            }
    }

}
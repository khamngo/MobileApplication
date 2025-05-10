package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.FoodItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoriteViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _favoriteItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val favoriteItems: StateFlow<List<FoodItem>> = _favoriteItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val favoriteFoodIds = MutableStateFlow<Set<String>>(emptySet())

    val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("favorites")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val ids = snapshot.documents.mapNotNull { it["foodId"] as? String }.toSet()
                favoriteFoodIds.value = ids
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavorite(foodItem: FoodItem) {
        val userId = currentUserId ?: return
        val favRef = db.collection("favorites")
        val currentIds = favoriteFoodIds.value

        if (currentIds.contains(foodItem.id)) {
            favRef.whereEqualTo("userId", userId)
                .whereEqualTo("foodId", foodItem.id)
                .get()
                .addOnSuccessListener { result ->
                    for (doc in result.documents) {
                        favRef.document(doc.id).delete()
                    }
                    favoriteFoodIds.value = favoriteFoodIds.value - foodItem.id
                }
        } else {
            // Thêm yêu thích
            val data = mapOf(
                "userId" to userId,
                "foodId" to foodItem.id,
                "name" to foodItem.name,
                "imageUrl" to foodItem.imageUrl,
                "description" to foodItem.description,
                "timestamp" to FieldValue.serverTimestamp()
            )
            favRef.add(data).addOnSuccessListener {
                favoriteFoodIds.value = favoriteFoodIds.value + foodItem.id
            }
        }
    }

    fun fetchFavorites() {
        val userId = currentUserId ?: return
        _isLoading.value = true

        db.collection("favorites")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val items = result.mapNotNull { doc ->
                    val id = doc["foodId"] as? String ?: return@mapNotNull null
                    val name = doc["name"] as? String ?: ""
                    val imageUrl = doc["imageUrl"] as? String ?: ""
                    val description = doc["description"] as? String ?: ""
                    FoodItem(id, name, description, 0.0, 0.0, imageUrl)
                }
                _favoriteItems.value = items
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    fun removeFavorite(foodId: String) {
        val userId = currentUserId ?: return
        db.collection("favorites")
            .whereEqualTo("userId", userId)
            .whereEqualTo("foodId", foodId)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    db.collection("favorites").document(doc.id).delete()
                }
                _favoriteItems.value = _favoriteItems.value.filterNot { it.id == foodId }
                favoriteFoodIds.value = favoriteFoodIds.value - foodId
            }
    }
}



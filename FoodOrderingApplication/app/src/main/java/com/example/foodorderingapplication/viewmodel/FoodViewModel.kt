package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.foodorderingapplication.model.FoodItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _foods = MutableStateFlow<List<FoodItem>>(emptyList())
    val foods: StateFlow<List<FoodItem>> = _foods

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    init {
        fetchFoods()
    }

    private fun fetchFoods() {
        _isLoading.value = true
        _isError.value = false

        db.collection("foods")
            .addSnapshotListener { snapshot, e ->
                _isLoading.value = false // Dừng loading dù thành công hay thất bại

                if (e != null) {
                    println("Lỗi: $e")
                    _isError.value = true
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val foodItemList = snapshot.map { doc ->
                        FoodItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            description = doc.getString("description") ?: "",
                            rating = doc.getDouble("rating") ?: 0.0,
                            imageRes = doc.getString("imageUrl") ?: ""
                        )
                    }
                    _foods.value = foodItemList
                    _isError.value = false
                } else {
                    // Nếu không có dữ liệu
                    _foods.value = emptyList()
                    _isError.value = false
                }
            }
    }

    fun deleteFood(foodId: String) {
        db.collection("foods").document(foodId)
            .delete()
            .addOnSuccessListener {
                println("Deleted successfully")
            }
            .addOnFailureListener { e ->
                println("Delete failed: $e")
            }
    }

    fun refreshFoods() {
        fetchFoods()
    }
}

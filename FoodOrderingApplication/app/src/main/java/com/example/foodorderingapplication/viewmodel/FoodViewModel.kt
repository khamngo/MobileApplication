package com.example.foodorderingapplication.viewmodel

import android.util.Log
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

    private val _exploreFoods = MutableStateFlow<List<FoodItem>>(emptyList())
    val exploreFoods: StateFlow<List<FoodItem>> = _exploreFoods

    private val _popularFoods = MutableStateFlow<List<FoodItem>>(emptyList())
    val popularFoods: StateFlow<List<FoodItem>> = _popularFoods

    private val _bestsellerFoods = MutableStateFlow<List<FoodItem>>(emptyList())
    val bestsellerFoods: StateFlow<List<FoodItem>> = _bestsellerFoods

    private val _dealFoods = MutableStateFlow<List<FoodItem>>(emptyList())
    val dealFoods: StateFlow<List<FoodItem>> = _dealFoods

    init {
        fetchFoods(null) // Lấy tất cả món ăn
        fetchFoods("popular")
        fetchFoods("bestseller")
        fetchFoods("deal")
    }

    fun fetchFoods(tag: String?) {
        val query = if (tag == null) {
            db.collection("foods")
        } else {
            db.collection("foods").whereArrayContains("tags", tag)
        }

        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FoodViewModel", "Lỗi khi lấy dữ liệu (tag: $tag): $e")
                return@addSnapshotListener
            }
            if (snapshot == null || snapshot.isEmpty) {
                Log.d("FoodViewModel", "Không có dữ liệu cho tag: $tag")
                when (tag) {
                    null -> _exploreFoods.value = emptyList()
                    "popular" -> _popularFoods.value = emptyList()
                    "bestseller" -> _bestsellerFoods.value = emptyList()
                    "deal" -> _dealFoods.value = emptyList()
                }
                return@addSnapshotListener
            }

            val foods = snapshot.map { doc ->
                val tags = doc.get("tags") as? List<String> ?: emptyList()
                Log.d("FOOD_TAG_LOG", "Food: ${doc.getString("name")}, Tags: $tags, Doc: ${doc.data}")
                FoodItem(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    price = doc.getDouble("price") ?: 0.0,
                    description = doc.getString("description") ?: "",
                    rating = doc.getDouble("rating") ?: 0.0,
                    imageUrl = doc.getString("imageUrl") ?: "",
                    tags = tags
                )
            }

            Log.d("FoodViewModel", "Fetched ${foods.size} items for tag: $tag")
            when (tag) {
                null -> _exploreFoods.value = foods
                "popular" -> _popularFoods.value = foods
                "bestseller" -> _bestsellerFoods.value = foods
                "deal" -> _dealFoods.value = foods
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


    private val _selectedFood = MutableStateFlow<FoodItem?>(null)
    val selectedFood: StateFlow<FoodItem?> = _selectedFood

    fun selectFoodById(foodId: String) {
        _selectedFood.value = _foods.value.find { it.id.trim() == foodId.trim() }
    }
}

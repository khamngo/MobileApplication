package com.example.foodorderingapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.FoodItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<FoodItem>>(emptyList())
    val searchResults: StateFlow<List<FoodItem>> = _searchResults

    init {
        viewModelScope.launch {
            fetchFoods(null)
            fetchFoods("popular")
            fetchFoods("bestseller")
            fetchFoods("deal")
        }
        observeSearchQuery()
    }


    fun fetchFoods(tag: String?) {
        val query = if (tag == null) {
            db.collection("foods")
        } else {
            db.collection("foods").whereArrayContains("tags", tag)
        }

        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FoodViewModel", "Error while retrieving data (tag: $tag): $e")
                if (tag == null) _isLoading.value = false
                _isError.value = true
                return@addSnapshotListener
            }

            if (snapshot == null || snapshot.isEmpty) {
                Log.d("FoodViewModel", "No data for tag: $tag")
                when (tag) {
                    null -> {
                        _exploreFoods.value = emptyList()
                        _isLoading.value = false
                    }
                    "popular" -> _popularFoods.value = emptyList()
                    "bestseller" -> _bestsellerFoods.value = emptyList()
                    "deal" -> _dealFoods.value = emptyList()
                }
                return@addSnapshotListener
            }

            val foods = snapshot.map { doc ->
                val tags = doc.get("tags") as? List<String> ?: emptyList()
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

            when (tag) {
                null -> {
                    _searchResults.value = foods
                    _exploreFoods.value = foods
                    _isLoading.value = false
                }
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

    fun onSearchTextChange(query: String) {
        _searchQuery.value = query
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _searchResults.value = _exploreFoods.value
                    } else {
                        _searchResults.value = _exploreFoods.value.filter { food ->
                            food.name.contains(query, ignoreCase = true) ||
                                    food.description.contains(query, ignoreCase = true)
                        }
                    }
                }
        }
    }
}

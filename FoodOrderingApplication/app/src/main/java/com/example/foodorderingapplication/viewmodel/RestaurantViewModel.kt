package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.RestaurantItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RestaurantViewModel : ViewModel() {

    private val _restaurantItems = MutableStateFlow<List<RestaurantItem>>(emptyList())
    val restaurantItems: StateFlow<List<RestaurantItem>> = _restaurantItems

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorRestaurant = MutableStateFlow<String?>(null)
    val errorRestaurant: StateFlow<String?> = _errorRestaurant

    init {
        fetchRestaurants()
    }

    private fun fetchRestaurants() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val snapshot = Firebase.firestore.collection("restaurant").get().await()
                val list = snapshot.documents.mapNotNull {
                    it.toObject(RestaurantItem::class.java)
                }
                _restaurantItems.value = list
            } catch (e: Exception) {
                _errorRestaurant.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}


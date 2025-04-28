package com.example.foodorderingapplication.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.FoodItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class FoodDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _foodDetail = MutableStateFlow<FoodItem?>(null)
    val foodDetail: StateFlow<FoodItem?> = _foodDetail

    private val _selectedPortion = MutableStateFlow("6")
    val selectedPortion: StateFlow<String> = _selectedPortion

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    private val _selectedDrink = MutableStateFlow("Pepsi")
    val selectedDrink: StateFlow<String> = _selectedDrink

    private val _instructions = MutableStateFlow("")
    val instructions: StateFlow<String> = _instructions

    val portionPrices: StateFlow<List<Pair<String, Double>>> = _foodDetail.map { food ->
        val basePrice = food?.price ?: 0.0
        listOf(
            "6" to basePrice,
            "8" to basePrice + 2.0,
            "10" to basePrice + 4.0
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val drinkPrices: StateFlow<List<Pair<String, Double>>> = MutableStateFlow(
        listOf(
            "Pepsi" to 0.0,
            "Coca Cola" to 0.5,
            "Fanta" to 0.5
        )
    )

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal

    // Combine để tính subtotal
    init {
        viewModelScope.launch {
            combine(foodDetail, _selectedPortion, _quantity, _selectedDrink) { food, portion, qty, drink ->
                val basePrice = food?.price ?: 0.0
                val portionPrice = when (portion) {
                    "6" -> basePrice
                    "8" -> basePrice + 2.0
                    "10" -> basePrice + 4.0
                    else -> 0.0
                }
                val drinkPrice = when (drink) {
                    "Coca Cola", "Fanta" -> 0.5
                    else -> 0.0
                }
                (portionPrice * qty) + drinkPrice
            }.collect { result ->
                _subtotal.value = result
            }
        }
    }

    fun fetchFoodById(foodId: String) {
        db.collection("foods").document(foodId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fetchedFood = document.toObject(FoodItem::class.java)
                    _foodDetail.value = fetchedFood?.copy(id = document.id)
                } else {
                    _foodDetail.value = null
                }
            }
            .addOnFailureListener {
                _foodDetail.value = null
            }
    }

    fun updatePortion(portion: String) {
        _selectedPortion.value = portion
    }

    fun updateQuantity(qty: Int) {
        _quantity.value = qty
    }

    fun updateDrink(drink: String) {
        _selectedDrink.value = drink
    }

    fun updateInstructions(note: String) {
        _instructions.value = note
    }
}


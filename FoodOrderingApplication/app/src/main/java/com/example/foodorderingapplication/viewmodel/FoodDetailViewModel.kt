package com.example.foodorderingapplication.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.foodorderingapplication.model.FoodItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.jvm.java

class FoodDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _foodDetail = MutableStateFlow<FoodItem?>(null)
    val foodDetail: StateFlow<FoodItem?> = _foodDetail

    var selectedPortion = mutableStateOf("6")
        private set
    var quantity = mutableIntStateOf(1)
        private set
    var selectedDrink = mutableStateOf("Pepsi")
        private set
    var instructions = mutableStateOf("")
        private set

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
        selectedPortion.value = portion
    }

    fun updateQuantity(qty: Int) {
        quantity.intValue = qty
    }

    fun updateDrink(drink: String) {
        selectedDrink.value = drink
    }

    fun updateInstructions(note: String) {
        instructions.value = note
    }
}

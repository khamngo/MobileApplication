package com.example.foodorderingapplication.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.FoodItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AddFoodViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _foodName = MutableStateFlow("")
    val foodName: StateFlow<String> = _foodName.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price.asStateFlow()

    private val _imageUrl = MutableStateFlow("")
    val imageUrl: StateFlow<String> = _imageUrl.asStateFlow()

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags: StateFlow<List<String>> = _tags.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun updateField(field: String, value: String) {
        when (field) {
            "foodName" -> _foodName.value = value
            "description" -> _description.value = value
            "price" -> _price.value = value
            "imageUrl" -> _imageUrl.value = value
        }
    }

    fun updateTags(newTags: List<String>) {
        _tags.value = newTags
    }

    fun uploadImageToFirebase(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fileName = "food_images/${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child(fileName)
                storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await()
                _imageUrl.value = downloadUrl.toString()
                _errorMessage.value = "Image uploaded successfully"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error uploading image"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addFood() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val priceValue = _price.value.toDoubleOrNull()
                if (priceValue == null) {
                    _errorMessage.value = "Invalid price format"
                    return@launch
                }

                if (_foodName.value.isBlank()) {
                    _errorMessage.value = "Food name cannot be empty"
                    return@launch
                }

                if (_description.value.isBlank()) {
                    _errorMessage.value = "Description cannot be empty"
                    return@launch
                }

                if (_imageUrl.value.isBlank()) {
                    _errorMessage.value = "Image URL cannot be empty"
                    return@launch
                }

                val foodId = UUID.randomUUID().toString()
                val newFood = FoodItem(
                    id = foodId,
                    name = _foodName.value,
                    description = _description.value,
                    price = priceValue,
                    imageUrl = _imageUrl.value,
                    tags = _tags.value,
                    rating = 0.0,
                    reviewCount = 0
                )

                db.collection("foods").document(foodId)
                    .set(newFood)
                    .await()

                _errorMessage.value = "Food added successfully"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error adding food"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
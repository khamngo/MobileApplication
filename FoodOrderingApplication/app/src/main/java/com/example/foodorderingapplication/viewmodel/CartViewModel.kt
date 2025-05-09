package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.round

class CartViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _cartItemItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItemItems: StateFlow<List<CartItem>> get() = _cartItemItems

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> get() = _total

    init {
        fetchCartItems()
    }

    private fun fetchCartItems() {
        if (userId.isEmpty()) return

        db.collection("carts").document(userId)
            .collection("items")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Lỗi khi lấy giỏ hàng: $e")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.map { doc ->
                        CartItem(
                            foodId = doc.id,
                            name = doc.getString("name") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            quantity = (doc.getLong("quantity") ?: 0L).toInt(),
                            imageUrl = doc.getString("imageUrl") ?: "",
                            portion = doc.getString("portion") ?: "",
                            drink = doc.getString("drink") ?: "",
                            instructions = doc.getString("instructions") ?: ""
                        )
                    }
                    _cartItemItems.value = items
                    calculateTotal(items)
                }
            }
    }

    fun addToCart(cartItem: CartItem) {
        if (userId.isEmpty()) return

        val cartItemData = hashMapOf(
            "name" to cartItem.name,
            "price" to cartItem.price,
            "subtotal" to cartItem.subtotal,
            "quantity" to cartItem.quantity,
            "imageUrl" to cartItem.imageUrl,
            "portion" to cartItem.portion,
            "drink" to cartItem.drink,
            "instructions" to cartItem.instructions
        )

        db.collection("carts").document(userId)
            .collection("items").document(cartItem.foodId)
            .set(cartItemData)
            .addOnSuccessListener { println("Đã thêm vào giỏ hàng!") }
            .addOnFailureListener { e -> println("Lỗi khi thêm vào giỏ hàng: $e") }
    }

    fun increaseQuantity(foodId: String) {
        val item = _cartItemItems.value.find { it.foodId == foodId } ?: return
        val newQuantity = item.quantity + 1
        updateQuantity(foodId, newQuantity)
    }

    fun decreaseQuantity(foodId: String) {
        val item = _cartItemItems.value.find { it.foodId == foodId } ?: return
        val newQuantity = item.quantity - 1
        if (newQuantity <= 0) removeFromCart(foodId)
        else updateQuantity(foodId, newQuantity)
    }

    private fun updateQuantity(foodId: String, quantity: Int) {
        if (userId.isEmpty()) return

        db.collection("carts").document(userId)
            .collection("items").document(foodId)
            .update("quantity", quantity)
            .addOnSuccessListener { println("Đã cập nhật số lượng") }
            .addOnFailureListener { e -> println("Lỗi khi cập nhật số lượng: $e") }
    }

    fun removeFromCart(foodId: String) {
        if (userId.isEmpty()) return

        db.collection("carts").document(userId)
            .collection("items").document(foodId)
            .delete()
            .addOnSuccessListener { println("Đã xóa món khỏi giỏ hàng!") }
            .addOnFailureListener { e -> println("Lỗi khi xóa món: $e") }
    }

    private fun calculateTotal(items: List<CartItem>) {
        val totalAmount = items.sumOf { item ->

            val drinkPrice = when (item.drink) {
                "Coca Cola", "Fanta" -> 0.5
                else -> 0.0
            }

            (item.price * item.quantity) + drinkPrice
        }.roundTo(2)

        _total.value = totalAmount
    }

    fun Double.roundTo(digits: Int): Double {
        val factor = 10.0.pow(digits)
        return round(this * factor) / factor
    }

    fun updateInstructions(foodId: String, instructions: String) {
        if (userId.isEmpty()) return

        db.collection("carts").document(userId)
            .collection("items").document(foodId)
            .update("instructions", instructions)
            .addOnSuccessListener { println("Đã cập nhật instructions") }
            .addOnFailureListener { e -> println("Lỗi khi cập nhật instructions: $e") }
    }
}

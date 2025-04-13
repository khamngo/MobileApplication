package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.CartItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // LiveData để lưu danh sách các món trong giỏ hàng
    private val _cartItemItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItemItems: StateFlow<List<CartItem>> get() = _cartItemItems

    // LiveData để lưu tổng tiền
    private val _total = MutableStateFlow<Double>(0.0)
    val total: StateFlow<Double> get() = _total

    // Khởi tạo: Lấy dữ liệu giỏ hàng từ Firestore khi ViewModel được tạo
    init {
        fetchCartItems()
    }

    // Hàm lấy danh sách món trong giỏ hàng từ Firestore
    private fun fetchCartItems(userId: String = "user_id_example") { // Thay userId bằng giá trị thực tế
        viewModelScope.launch {
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
                                quantity = doc.getString("quantity")?.toInt() ?: 0,
                                imageRes = doc.getString("imageUrl") ?: ""
                            )
                        }
                        _cartItemItems.value = items
                        updateCartTotal(userId) // Cập nhật tổng tiền khi danh sách thay đổi
                    }
                }
        }
    }

    // Hàm thêm món vào giỏ hàng
    fun addToCart(userId: String, cartItem: CartItem) {
        val cartItemData = hashMapOf(
            "name" to cartItem.name,
            "price" to cartItem.price,
            "quantity" to cartItem.quantity,
            "imageUrl" to cartItem.imageRes
        )

        viewModelScope.launch {
            db.collection("carts").document(userId)
                .collection("items").document(cartItem.foodId)
                .set(cartItemData)
                .addOnSuccessListener {
                    println("Đã thêm vào giỏ hàng!")
                }
                .addOnFailureListener { e ->
                    println("Lỗi khi thêm vào giỏ hàng: $e")
                }
        }
    }

    // Hàm cập nhật tổng tiền
    private fun updateCartTotal(userId: String) {
        db.collection("carts").document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener { result ->
                var total = 0.0
                for (document in result) {
                    val price = document.getDouble("price") ?: 0.0
                    val quantity = document.getLong("quantity")?.toInt() ?: 0
                    total += price * quantity
                }
                _total.value = total
                // Cập nhật tổng tiền lên Firestore (tùy chọn)
                db.collection("carts").document(userId)
                    .update("total", total)
                    .addOnFailureListener { e ->
                        println("Lỗi khi cập nhật tổng tiền: $e")
                    }
            }
            .addOnFailureListener { e ->
                println("Lỗi khi tính tổng tiền: $e")
            }
    }

    // Hàm xóa món khỏi giỏ hàng (tùy chọn)
    fun removeFromCart(userId: String, dishId: String) {
        viewModelScope.launch {
            db.collection("carts").document(userId)
                .collection("items").document(dishId)
                .delete()
                .addOnSuccessListener {
                    println("Đã xóa món khỏi giỏ hàng!")
                }
                .addOnFailureListener { e ->
                    println("Lỗi khi xóa món: $e")
                }
        }
    }
}
package com.example.foodorderingapplication.model

data class UserItem(
    val name: String,
    val phone: String,
    val address: String,
    val email: String,
    val photoUrl: String,
    val birthDate: String
)

//fun saveUserInfo(userId: String, user: User) {
//    val userData = hashMapOf(
//        "name" to user.name,
//        "phone" to user.phone,
//        "address" to user.address,
//        "email" to user.email
//    )
//
//    db.collection("users").document(userId)
//        .set(userData)
//        .addOnSuccessListener {
//            println("Thông tin người dùng đã được lưu!")
//        }
//        .addOnFailureListener { e ->
//            println("Lỗi khi lưu thông tin: $e")
//        }
//}
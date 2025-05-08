package com.example.foodorderingapplication.model

import com.google.firebase.Timestamp

data class UserItem(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String? = null,
    val avatarUrl: String? = null,
    val createdAt: Timestamp? = null,
    val role: String = "user",
    val provider: String = "",
)

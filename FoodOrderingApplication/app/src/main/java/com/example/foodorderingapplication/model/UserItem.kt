package com.example.foodorderingapplication.model

data class UserItem(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String? = null,
    val avatarUrl: String? = null
)

data class UserProfile(
    val role: String = "user",
    val email: String = "",
    val username: String = ""
)
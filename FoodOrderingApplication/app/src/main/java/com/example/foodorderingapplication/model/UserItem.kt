package com.example.foodorderingapplication.model

data class UserItem(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String? = null,
    val avatarUrl: String? = null
)

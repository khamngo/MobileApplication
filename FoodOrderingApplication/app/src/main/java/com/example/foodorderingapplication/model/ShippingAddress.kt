package com.example.foodorderingapplication.model

data class ShippingAddress(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val province: String = "",
    val district : String = "",
    val ward: String = "",
    val street: String = "",
    val isDefault: Boolean = false
)

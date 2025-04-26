package com.example.foodorderingapplication.model

data class FieldErrors(
    val firstNameError: Boolean = false,
    val lastNameError: Boolean = false,
    val phoneNumberError: Boolean = false,
    val provinceError: Boolean = false,
    val streetError: Boolean = false,
    val districtError: Boolean = false,
    val wardError: Boolean = false,
    val phoneNumberInvalid: Boolean = false // Lỗi riêng cho định dạng số điện thoại
)
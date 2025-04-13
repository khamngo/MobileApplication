package com.example.foodorderingapplication.model

import androidx.compose.ui.graphics.Color

data class NotificationItem(
    val id: String,
    val message: String,
    val time: String,
    val dotColor: Color,
    val isRead: Boolean,
)

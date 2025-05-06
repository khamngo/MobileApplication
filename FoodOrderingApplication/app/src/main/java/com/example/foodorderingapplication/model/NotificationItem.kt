package com.example.foodorderingapplication.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val orderId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val isRead: Boolean = false,
    val type: String = "",
    val time: String = "",
    val dotColor: Color = Color.Blue
)

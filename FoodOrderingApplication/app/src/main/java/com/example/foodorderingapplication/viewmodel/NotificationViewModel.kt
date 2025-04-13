package com.example.foodorderingapplication.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.foodorderingapplication.model.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.graphics.toColorInt

class NotificationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        db.collection("notifications")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        NotificationItem(
                            id = doc.id,
                            message = doc.getString("message") ?: "",
                            time = doc.getString("time") ?: "",
                            dotColor = Color((doc.getString("dotColor") ?: "#888888").toColorInt()),
                            isRead = doc.getBoolean("isRead") ?: false
                        )
                    }
                    _notifications.value = list
                }
            }
    }

    fun markAsRead(notificationId: String) {
        db.collection("notifications").document(notificationId)
            .update("isRead", true)
    }
}

package com.example.foodorderingapplication.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.foodorderingapplication.model.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .collection("notifications")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                viewModelScope.launch {
                    if (e != null) {
                        // Xử lý lỗi
                        return@launch
                    }
                    if (snapshot != null) {
                        val notificationsList = snapshot.documents.mapNotNull { doc ->
                            try {
                                val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                                val timeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                val dotColor = when (doc.getString("type")) {
                                    "order_cancelled" -> Color.Red
                                    "order_accepted" -> Color.Green
                                    "order_created" -> Color.Blue
                                    else -> Color.Gray
                                }
                                NotificationItem(
                                    id = doc.id,
                                    title = doc.getString("title") ?: "",
                                    message = doc.getString("message") ?: "",
                                    orderId = doc.getString("orderId") ?: "",
                                    timestamp = timestamp,
                                    isRead = doc.getBoolean("isRead") ?: false,
                                    type = doc.getString("type") ?: "",
                                    time = timeFormat.format(timestamp.toDate()),
                                    dotColor = dotColor
                                )
                            } catch (ex: Exception) {
                                null
                            }
                        }
                        _notifications.value = notificationsList
                    }
                }
            }
    }

    fun markAsRead(notificationId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("notifications").document(notificationId)
                    .update("isRead", true)
                    .await()
            } catch (e: Exception) {
                // Xử lý lỗi
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("notifications").document(notificationId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                // Xử lý lỗi
            }
        }
    }
}

package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.UserItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _userList = MutableStateFlow<List<UserItem>>(emptyList())
    val userList: StateFlow<List<UserItem>> = _userList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Lấy danh sách tất cả user IDs từ collection "users"
                val userDocs = db.collection("users").get().await()
                if (userDocs.isEmpty) {
                    _errorMessage.value = "No users found in Firestore"
                    _userList.value = emptyList()
                    return@launch
                }

                val users = mutableListOf<UserItem>()
                for (doc in userDocs.documents) {
                    val userId = doc.id
                    val profileDoc = db.collection("users")
                        .document(userId)
                        .collection("profile")
                        .document("info")
                        .get()
                        .await()

                    if (profileDoc.exists()) {
                        val user = profileDoc.toObject(UserItem::class.java)?.copy(uid = userId)
                        if (user != null) {
                            users.add(user)
                        }
                    }
                }

                if (users.isEmpty()) {
                    _errorMessage.value = "No user profiles found in Firestore"
                } else {
                    _errorMessage.value = ""
                }
                _userList.value = users
            } catch (e: Exception) {
                _errorMessage.value = when (e) {
                    is com.google.firebase.auth.FirebaseAuthException -> "Authentication error: ${e.message}"
                    is com.google.firebase.firestore.FirebaseFirestoreException -> "Firestore error: ${e.message}"
                    else -> e.message ?: "Error fetching users"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser(user: UserItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                db.collection("users")
                    .document(user.uid)
                    .collection("profile")
                    .document("info")
                    .set(user)
                    .await()

                fetchUsers() // Refresh danh sách sau khi cập nhật
                _errorMessage.value = "User updated successfully"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error updating user"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
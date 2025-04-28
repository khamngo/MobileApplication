package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.UserItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyAccountViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow(UserItem())
    val user: StateFlow<UserItem> = _user

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            _user.value = UserItem(
                uid = it.uid,
                username = it.displayName ?: "",
                email = it.email ?: "",
                phone = it.phoneNumber,
                avatarUrl = it.photoUrl?.toString()
            )
        }
    }

    fun updateUsername(newName: String) {
        _user.value = _user.value.copy(username = newName)
    }

    fun updateEmail(newEmail: String) {
        _user.value = _user.value.copy(email = newEmail)
    }

    fun updatePhone(newPhone: String) {
        _user.value = _user.value.copy(phone = newPhone)
    }

    fun saveChanges(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = Firebase.auth.currentUser
        viewModelScope.launch {
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(_user.value.username)
                .build()

            user?.updateProfile(profileUpdates)
                ?.addOnSuccessListener { onSuccess() }
                ?.addOnFailureListener { e -> onFailure(e.message ?: "Lỗi không xác định") }
        }
    }

    fun logout() {
        auth.signOut()  // Firebase sign-out
    }
}
package com.example.foodorderingapplication.viewmodel

import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.UserItem
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyAccountViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow(UserItem())
    val user: StateFlow<UserItem> = _user.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var tempUsername: String = ""
    private var tempEmail: String = ""
    private var tempPhone: String? = null

    private var profileListener: ListenerRegistration? = null

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            _user.value = UserItem(
                uid = user.uid,
                username = user.displayName ?: "",
                email = user.email ?: "",
                phone = user.phoneNumber,
                avatarUrl = user.photoUrl?.toString()
            )

            profileListener = db.collection("users").document(user.uid)
                .collection("profile").document("info")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Error fetching user profile: ${e.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val profile = snapshot.data
                        _user.value = UserItem(
                            uid = user.uid,
                            username = user.displayName ?: "",
                            email = user.email ?: "",
                            phone = profile?.get("phone") as? String ?: user.phoneNumber,
                            avatarUrl = user.photoUrl?.toString()
                        )
                        tempUsername = user.displayName ?: ""
                        tempEmail = user.email ?: ""
                        tempPhone = profile?.get("phone") as? String ?: user.phoneNumber
                    }
                }
        } ?: run {
            _user.value = UserItem()
        }
    }

    fun updateUsername(newUsername: String) {
        _user.value = _user.value.copy(username = newUsername)
        tempUsername = newUsername
    }

    fun updateEmail(newEmail: String) {
        _user.value = _user.value.copy(email = newEmail)
        tempEmail = newEmail
    }

    fun updatePhone(newPhone: String) {
        _user.value = _user.value.copy(phone = newPhone)
        tempPhone = newPhone
    }

    fun updateUserProfile(password: String, confirmPassword: String, currentPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = auth.currentUser ?: throw IllegalArgumentException("User not logged in")

                when {
                    tempUsername.isBlank() -> throw IllegalArgumentException("Username cannot be empty")
                    tempEmail.isBlank() -> throw IllegalArgumentException("Email cannot be empty")
                    !Patterns.EMAIL_ADDRESS.matcher(tempEmail).matches() -> throw IllegalArgumentException("Invalid email format")
                    tempPhone?.isBlank() == true -> throw IllegalArgumentException("Phone cannot be empty")
                    tempPhone?.let { !Patterns.PHONE.matcher(it).matches() } == true -> throw IllegalArgumentException("Invalid phone number format")
                    password.isNotEmpty() && password.length < 6 -> throw IllegalArgumentException("Password must be at least 6 characters")
                    password != confirmPassword -> throw IllegalArgumentException("Confirm Password does not match")
                    currentPassword.isBlank() -> throw IllegalArgumentException("Current password is required")
                }

                // Re-authenticate
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                user.reauthenticate(credential).await()

                // Update email if changed
                if (tempEmail != user.email) {
                    user.updateEmail(tempEmail).await()
                }

                // Update password if entered
                if (password.isNotEmpty()) {
                    user.updatePassword(password).await()
                }

                // Update displayName if changed
                if (tempUsername != user.displayName) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(tempUsername)
                        .build()
                    user.updateProfile(profileUpdates).await()
                }

                // Update Firestore
                val userMap = mapOf(
                    "username" to tempUsername,
                    "email" to tempEmail,
                    "phone" to tempPhone,
                    "role" to "user"
                )
                db.collection("users").document(user.uid)
                    .collection("profile").document("info")
                    .update(userMap)
                    .await()

                _errorMessage.value = "Profile updated successfully"
            } catch (e: Exception) {
                _errorMessage.value = when {
                    e.message?.contains("reauthenticate") == true -> "Incorrect current password"
                    else -> e.message ?: "Error updating profile"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = ""
    }

    override fun onCleared() {
        profileListener?.remove()
        super.onCleared()
    }

    fun logout() {
        auth.signOut()
    }
}

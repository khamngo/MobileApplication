package com.example.foodorderingapplication.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.UserItem
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class MyAccountViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage
    private val currentUser: FirebaseUser? get() = auth.currentUser

    private val _user = MutableStateFlow(UserItem())
    val user: StateFlow<UserItem> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    init {
        loadUserData()
    }

    fun uploadAvatarImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                val fileName = "avatar_${userId}_${System.currentTimeMillis()}.jpg"
                val storageRef = storage.reference.child("avatars/$fileName")

                // Upload
                storageRef.putFile(uri).await()

                // Get download URL
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // Update Firestore
                db.collection("users").document(userId)
                    .collection("profile").document("info")
                    .update("avatarUrl", downloadUrl)
                    .await()

                // Update state
                _user.value = _user.value.copy(avatarUrl = downloadUrl)

                _errorMessage.value = "Avatar updated"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error uploading avatar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    internal fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                currentUser?.let { user ->
                    val doc = db.collection("users").document(user.uid).collection("profile").document("info").get().await()
                    val userData = doc.toObject(UserItem::class.java) ?: UserItem()
                    _user.value = userData.copy(
                        email = user.email ?: "",
                        provider = user.providerData.find { it.providerId != "firebase" }?.providerId ?: "password"
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error loading user data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUsername(username: String) {
        viewModelScope.launch {
            _user.value = _user.value.copy(username = username)
        }
    }

    fun updatePhone(phone: String) {
        viewModelScope.launch {
            _user.value = _user.value.copy(phone = phone)
        }
    }

    fun updateUserProfile(newPassword: String, confirmPassword: String, currentPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                currentUser?.let { user ->
                    val isEmailPasswordUser = user.providerData.any { it.providerId == EmailAuthProvider.PROVIDER_ID }
                    if (!isEmailPasswordUser) {
                        _errorMessage.value = "Password change not supported for Google accounts"
                        return@launch
                    }

                    // Kiểm tra đầu vào
                    if (newPassword.isNotEmpty()) {
                        if (newPassword.length < 6) {
                            _errorMessage.value = "Password must be at least 6 characters"
                            return@launch
                        }
                        if (newPassword != confirmPassword) {
                            _errorMessage.value = "New passwords do not match"
                            return@launch
                        }
                        if (currentPassword.isEmpty()) {
                            _errorMessage.value = "Current password is required to change password"
                            return@launch
                        }

                        // Re-authenticate
                        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                        user.reauthenticate(credential).await()

                        // Update password
                        user.updatePassword(newPassword).await()
                    }

                    // Cập nhật Firestore
                    db.collection("users").document(user.uid)
                        .collection("profile").document("info")
                        .set(_user.value)
                        .await()

                    loadUserData()
                    _errorMessage.value = "Profile updated successfully"
                }
            } catch (e: Exception) {
                _errorMessage.value = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid current password"
                    is FirebaseAuthWeakPasswordException -> "Weak password"
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

    fun logout() {
        auth.signOut()
    }
}

package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _createAccountSuccess = MutableStateFlow(false)
    val createAccountSuccess: StateFlow<Boolean> = _createAccountSuccess.asStateFlow()

    private val _signInSuccess = MutableStateFlow(false)
    val signInSuccess: StateFlow<Boolean> = _signInSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo.asStateFlow()

    init {
        checkUserAndRole()
    }

    private fun checkUserAndRole() {
        val currentUser = auth.currentUser

        viewModelScope.launch {
            delay(2000)

            if (currentUser != null) {
                try {
                    val snapshot = db.collection("users")
                        .document(currentUser.uid)
                        .collection("profile").document("info")
                        .get()
                        .await()

                    val role = snapshot.getString("role")
                    when (role) {
                        "admin" -> _navigateTo.value = "admin_home"
                        "user" -> _navigateTo.value = "user_home"
                        else -> _navigateTo.value = "login"
                    }
                } catch (e: Exception) {
                    _navigateTo.value = "login"
                }
            } else {
                _navigateTo.value = "login"
            }
        }
    }

    fun createAccount(
        username: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        role: String
    ) {
        viewModelScope.launch {
            try {
                // Validate input
                when {
                    username.isBlank() -> throw IllegalArgumentException("Username cannot be empty")
                    email.isBlank() -> throw IllegalArgumentException("Email cannot be empty")
                    phone.isBlank() -> throw IllegalArgumentException("Phone cannot be empty")
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> throw IllegalArgumentException("Invalid email format")
                    password.isBlank() -> throw IllegalArgumentException("Password cannot be empty")
                    password.length < 6 -> throw IllegalArgumentException("Password must be at least 6 characters")
                    confirmPassword != password -> throw IllegalArgumentException("Confirm Password does not match")
                    role !in listOf("user", "admin") -> throw IllegalArgumentException("Invalid role")
                }

                // Tạo tài khoản bằng Firebase Authentication
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid ?: throw Exception("User creation failed")

                // Lưu profile vào Firestore
                val profile = UserProfile(
                    username = username,
                    email = email,
                    role = role
                )
                db.collection("users").document(userId)
                    .collection("profile").document("info")
                    .set(profile)
                    .await()

                _createAccountSuccess.value = true
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error creating account"
                _createAccountSuccess.value = false
            }
        }
    }

    fun resetCreateAccountState() {
        _createAccountSuccess.value = false
        _errorMessage.value = ""
    }

    // Đăng nhập bằng Google
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user ?: throw Exception("User not found")

                // Lưu hoặc lấy profile
                val userProfile = getOrCreateUserProfile(user.uid, user.email, user.displayName)
                _userRole.value = userProfile.role
                _signInSuccess.value = true
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = "Google Sign-In Failed: ${e.message}"
                _signInSuccess.value = false
            }
        }
    }

    // Lấy hoặc tạo profile người dùng
    private suspend fun getOrCreateUserProfile(userId: String, email: String?, displayName: String?): UserProfile {
        val profileRef = db.collection("users").document(userId).collection("profile").document("info")
        val snapshot = profileRef.get().await()

        return if (snapshot.exists()) {
            snapshot.toObject(UserProfile::class.java) ?: UserProfile()
        } else {
            // Tạo profile mới với vai trò mặc định là user
            val newProfile = UserProfile(
                role = "user",
                email = email ?: "",
                username = displayName ?: ""
            )
            profileRef.set(newProfile).await()
            newProfile
        }
    }

    // Đặt lại trạng thái
    fun resetSignInState() {
        _signInSuccess.value = false
        _errorMessage.value = ""
        _userRole.value = null
    }
}
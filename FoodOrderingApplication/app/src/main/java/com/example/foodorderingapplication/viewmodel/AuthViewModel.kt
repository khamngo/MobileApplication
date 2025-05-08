package com.example.foodorderingapplication.viewmodel

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.UserItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _username = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _phone = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _confirmPassword = MutableStateFlow("")

    // Public StateFlow (đọc-only cho UI)
    val username: StateFlow<String> = _username.asStateFlow()
    val email: StateFlow<String> = _email.asStateFlow()
    val phone: StateFlow<String> = _phone.asStateFlow()
    val password: StateFlow<String> = _password.asStateFlow()
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    // Hàm cập nhật dữ liệu
    fun onUsernameChange(value: String) { _username.value = value }
    fun onEmailChange(value: String) { _email.value = value }
    fun onPhoneChange(value: String) { _phone.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onConfirmPasswordChange(value: String) { _confirmPassword.value = value }


    private val _createAccountSuccess = MutableStateFlow(false)
    val createAccountSuccess: StateFlow<Boolean> = _createAccountSuccess.asStateFlow()

    private val _signInSuccess = MutableStateFlow(false)
    val signInSuccess: StateFlow<Boolean> = _signInSuccess.asStateFlow()

    private val _signUpSuccess = MutableStateFlow(false)
    val signUpSuccess: StateFlow<Boolean> = _signUpSuccess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo.asStateFlow()

    init {
        checkUserAndRole()
    }

    fun signInWithEmailAndPassword(
        onSuccess: () -> Unit
    ) {
        val emailValue = _email.value.trim()
        val passwordValue = _password.value

        if (emailValue.isEmpty() || passwordValue.isEmpty()) {
            _errorMessage.value = "Please enter both email and password"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(emailValue, passwordValue).await()
                val user = authResult.user ?: throw Exception("User not found")

                // Lấy role từ Firestore
                val userProfile = getOrCreateUserProfile(user.uid, user.email, user.displayName)
                _userRole.value = userProfile.role

                _errorMessage.value = ""
                _signUpSuccess.value = true
                _signInSuccess.value = true
                onSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message ?: "Login failed"
                _signInSuccess.value = false
            }
        }
    }

    fun signUpWithEmailAndPassword(
        username: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Validate input
                when {
                    username.isBlank() -> throw IllegalArgumentException("Username cannot be empty")
                    email.isBlank() -> throw IllegalArgumentException("Email cannot be empty")
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> throw IllegalArgumentException("Invalid email format")
                    phone.isBlank() -> throw IllegalArgumentException("Phone cannot be empty")
                    !Patterns.PHONE.matcher(phone).matches() -> throw IllegalArgumentException("Invalid phone number format")
                    password.isBlank() -> throw IllegalArgumentException("Password cannot be empty")
                    password.length < 6 -> throw IllegalArgumentException("Password must be at least 6 characters")
                    confirmPassword != password -> throw IllegalArgumentException("Confirm Password does not match")
                }

                // Tạo tài khoản Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user ?: throw Exception("User creation failed")

                // Cập nhật displayName cho FirebaseUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                // Chuẩn bị dữ liệu người dùng với ngày tạo
                val userMap = mapOf(
                    "uid" to firebaseUser.uid,
                    "username" to username,
                    "email" to email,
                    "phone" to phone,
                    "role" to "user",
                    "provider" to "password",
                    "createdAt" to FieldValue.serverTimestamp()
                )

                // Lưu vào Firestore
                db.collection("users").document(firebaseUser.uid)
                    .collection("profile").document("info")
                    .set(userMap)
                    .await()

                _signUpSuccess.value = true
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error creating account"
                _signUpSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSignUpState() {
        _signUpSuccess.value = false
        _errorMessage.value = ""
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
        // Reset lỗi trước đó
        _errorMessage.value = ""

        // Kiểm tra đầu vào
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _errorMessage.value = "Please fill in all required fields"
            return
        }

        if (password != confirmPassword) {
            _errorMessage.value = "Confirm Password does not match"
            return
        }

        viewModelScope.launch {
            try {
                // Tạo tài khoản với email và password
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: throw Exception("Failed to get user ID")

                // Lưu thông tin người dùng vào Firestore
                val newUser = UserItem(
                    uid = uid,
                    username = username,
                    email = email,
                    phone = phone,
                    role = role,
                    provider = "password",
                    createdAt = Timestamp.now()
                )

                db.collection("users").document(uid)
                    .collection("profile").document("info")
                    .set(newUser)
                    .await()
                _createAccountSuccess.value = true
            } catch (e: FirebaseAuthUserCollisionException) {
                _errorMessage.value = "This email is already in use"
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Account creation failed"
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
    private suspend fun getOrCreateUserProfile(userId: String, email: String?, displayName: String?): UserItem {
        val profileRef = db.collection("users").document(userId).collection("profile").document("info")
        val snapshot = profileRef.get().await()

        return if (snapshot.exists()) {
            snapshot.toObject(UserItem::class.java) ?: UserItem()
        } else {
            // Tạo profile mới với vai trò mặc định là user
            val newProfile = UserItem(
                role = "user",
                email = email ?: "",
                username = displayName ?: "",
                provider = "google",
                uid = userId,
                createdAt = Timestamp.now()
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

    fun logout() {
        auth.signOut()
    }
}
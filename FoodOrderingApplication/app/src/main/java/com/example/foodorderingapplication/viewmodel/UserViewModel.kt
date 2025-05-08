package com.example.foodorderingapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.foodorderingapplication.model.UserItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _userList = MutableStateFlow<List<UserItem>>(emptyList())
    val userList: StateFlow<List<UserItem>> = _userList

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.map { doc ->
                    val profileRef = doc.reference.collection("profile").document("info")
                    profileRef.get().addOnSuccessListener { profileSnap ->
                        val profileData = profileSnap.data
                        val user = UserItem(
                            uid = doc.id,
                            email = doc.getString("email") ?: "",
                            username = profileData?.get("username") as? String ?: "",
                            phone = profileData?.get("phone") as? String,
                            role = (profileData?.get("role") as? String).toString(),
                            avatarUrl = doc.getString("avatarUrl")
                        )
                        _userList.value = _userList.value.toMutableList().apply {
                            removeAll { it.uid == user.uid }
                            add(user)
                        }
                    }
                }
            }
    }

    fun updateUser(user: UserItem) {
        // Cập nhật dữ liệu người dùng trong "profile/info"
        val profileData = mapOf(
            "username" to user.username,
            "phone" to user.phone,
            "role" to user.role
        )

        firestore.collection("users")
            .document(user.uid)
            .collection("profile")
            .document("info")
            .set(profileData, SetOptions.merge())
            .addOnSuccessListener {
                fetchUsers() // refresh lại danh sách sau khi update
            }
    }

    fun deleteUser(uid: String) {
        firestore.collection("users")
            .document(uid)
            .delete()
            .addOnSuccessListener {
                _userList.value = _userList.value.filter { it.uid != uid }
            }
            .addOnFailureListener {
                Log.e("UserViewModel", "Error deleting user: ${it.message}")
            }
    }
}
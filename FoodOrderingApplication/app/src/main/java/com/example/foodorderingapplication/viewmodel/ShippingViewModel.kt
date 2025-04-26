package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.FieldErrors
import com.example.foodorderingapplication.model.ShippingAddress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShippingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // StateFlow cho địa chỉ
    private val _shippingAddress = MutableStateFlow(ShippingAddress())
    val shippingAddress: StateFlow<ShippingAddress> = _shippingAddress.asStateFlow()

    // StateFlow cho lỗi của các trường
    private val _fieldErrors = MutableStateFlow(FieldErrors())
    val fieldErrors: StateFlow<FieldErrors> = _fieldErrors.asStateFlow()

    // StateFlow cho thông báo lỗi chung (nếu cần)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadShippingAddress()
    }

    // Tải địa chỉ từ Firebase
    private fun loadShippingAddress() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            db.collection("users").document(userId)
                .collection("shippingAddress").document("default")
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        _shippingAddress.value = ShippingAddress(
                            firstName = doc.getString("firstName") ?: "",
                            lastName = doc.getString("lastName") ?: "",
                            phoneNumber = doc.getString("phoneNumber") ?: "",
                            province = doc.getString("province") ?: "",
                            district = doc.getString("district") ?: "",
                            ward = doc.getString("ward") ?: "",
                            street = doc.getString("street") ?: "",
                            isDefault = doc.getBoolean("isDefault") ?: false
                        )
                    }
                }
                .addOnFailureListener { e ->
                    _errorMessage.value = "Lỗi khi tải địa chỉ: ${e.message}"
                }
        }
    }

    // Cập nhật các trường địa chỉ
    fun updateShippingField(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        province: String,
        district: String,
        ward: String,
        street: String,
        isDefault: Boolean
    ) {
        _shippingAddress.value = ShippingAddress(
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            province = province,
            district = district,
            ward = ward,
            street = street,
            isDefault = isDefault
        )
        // Xóa lỗi khi người dùng cập nhật trường
        clearFieldErrors()
    }

    // Validate và lưu địa chỉ vào Firebase
    fun saveShippingAddressToFirebase(
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Kiểm tra null/empty và validate số điện thoại
        val errors = FieldErrors(
            firstNameError = _shippingAddress.value.firstName.isBlank(),
            lastNameError = _shippingAddress.value.lastName.isBlank(),
            phoneNumberError = _shippingAddress.value.phoneNumber.isBlank(),
            provinceError = _shippingAddress.value.province.isBlank(),
            streetError = _shippingAddress.value.street.isBlank(),
            wardError = _shippingAddress.value.ward.isBlank(),
            districtError = _shippingAddress.value.district.isBlank(),
            phoneNumberInvalid = !_shippingAddress.value.phoneNumber.isBlank() && !isValidPhoneNumber(_shippingAddress.value.phoneNumber)
        )

        _fieldErrors.value = errors

        // Nếu có bất kỳ lỗi nào, không lưu
        if (errors.firstNameError || errors.lastNameError || errors.phoneNumberError ||
            errors.provinceError || errors.streetError || errors.phoneNumberInvalid ||
            errors.wardError || errors.districtError
        ) {
            _errorMessage.value = "Please check the input fields!"
            return
        }

        // Lưu vào Firebase
        val addressData = hashMapOf(
            "firstName" to _shippingAddress.value.firstName,
            "lastName" to _shippingAddress.value.lastName,
            "phoneNumber" to _shippingAddress.value.phoneNumber,
            "province" to _shippingAddress.value.province,
            "district" to _shippingAddress.value.district,
            "ward" to _shippingAddress.value.ward,
            "street" to _shippingAddress.value.street,
            "isDefault" to _shippingAddress.value.isDefault
        )

        viewModelScope.launch {
            db.collection("users").document(userId)
                .collection("shippingAddress").document("default")
                .set(addressData)
                .addOnSuccessListener {
                    _errorMessage.value = null
                    _fieldErrors.value = FieldErrors() // Xóa lỗi
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    _errorMessage.value = "Lỗi lưu địa chỉ: ${e.message}"
                    onFailure(e)
                }
        }
    }

    // Validate số điện thoại (10 chữ số, bắt đầu bằng 0)
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val regex = Regex("^0[0-9]{9}$")
        return phoneNumber.matches(regex)
    }

    // Xóa trạng thái lỗi
    private fun clearFieldErrors() {
        _fieldErrors.value = FieldErrors()
        _errorMessage.value = null
    }

    // Xóa thông báo lỗi chung
    fun clearError() {
        _errorMessage.value = null
    }
}
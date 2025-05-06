package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.FieldErrors
import com.example.foodorderingapplication.model.RestaurantItem
import com.example.foodorderingapplication.model.ShippingAddress
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    fun selectRestaurant(restaurant: RestaurantItem) {
        val current = _shippingAddress.value
        _shippingAddress.value = current.copy(restaurant = restaurant)
        clearFieldErrors()
    }

    // Tải địa chỉ từ Firebase
    private fun loadShippingAddress() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            db.collection("users").document(userId)
                .collection("shippingAddress").document("default")
                .get()
                .addOnSuccessListener { doc ->
                    val restaurantMap = doc.get("restaurant") as? Map<*, *>
                    val restaurant = if (restaurantMap != null) {
                        RestaurantItem(
                            name = restaurantMap["name"] as? String ?: "",
                            address = restaurantMap["address"] as? String ?: "",
                            phone = restaurantMap["phone"] as? String ?: "",
                            hours = restaurantMap["hours"] as? String ?: ""
                        )
                    } else RestaurantItem()

                    if (doc.exists()) {
                        _shippingAddress.value = ShippingAddress(
                            firstName = doc.getString("firstName") ?: "",
                            lastName = doc.getString("lastName") ?: "",
                            phoneNumber = doc.getString("phoneNumber") ?: "",
                            province = doc.getString("province") ?: "",
                            district = doc.getString("district") ?: "",
                            ward = doc.getString("ward") ?: "",
                            street = doc.getString("street") ?: "",
                            restaurant = restaurant,
                            isDefault = doc.getBoolean("isDefault") == true
                        )
                    }
                }
                .addOnFailureListener { e ->
                        _errorMessage.value = "Error loading address: ${e.message}"
                }
        }
    }

    fun onFirstNameChange(newFirstName: String) {
        _shippingAddress.value = _shippingAddress.value.copy(firstName = newFirstName)
        clearFieldErrors()
    }

    fun onLastNameChange(newLastName: String) {
        _shippingAddress.value = _shippingAddress.value.copy(lastName = newLastName)
        clearFieldErrors()
    }

    fun onPhoneNumberChange(newPhone: String) {
        _shippingAddress.value = _shippingAddress.value.copy(phoneNumber = newPhone)
        clearFieldErrors()
    }

    fun onProvinceChange(newProvince: String) {
        _shippingAddress.value = _shippingAddress.value.copy(province = newProvince)
        clearFieldErrors()
    }

    fun onDistrictChange(newDistrict: String) {
        _shippingAddress.value = _shippingAddress.value.copy(district = newDistrict)
        clearFieldErrors()
    }

    fun onWardChange(newWard: String) {
        _shippingAddress.value = _shippingAddress.value.copy(ward = newWard)
        clearFieldErrors()
    }

    fun onStreetChange(newStreet: String) {
        _shippingAddress.value = _shippingAddress.value.copy(street = newStreet)
        clearFieldErrors()
    }

    fun onIsDefaultChange(isDefault: Boolean) {
        _shippingAddress.value = _shippingAddress.value.copy(isDefault = isDefault)
    }

    // Validate và lưu địa chỉ vào Firebase
    fun saveShippingAddressToFirebase(
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
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
            "restaurant" to hashMapOf(
                "name" to _shippingAddress.value.restaurant.name,
                "address" to _shippingAddress.value.restaurant.address,
                "phone" to _shippingAddress.value.restaurant.phone,
                "hours" to _shippingAddress.value.restaurant.hours
            ),
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
                    _errorMessage.value = "Error loading address: ${e.message}"
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
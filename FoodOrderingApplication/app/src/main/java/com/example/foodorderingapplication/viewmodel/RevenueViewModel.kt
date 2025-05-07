package com.example.foodorderingapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapplication.model.OrderItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class RevenueViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _totalOrders = MutableStateFlow(0)
    val totalOrders: StateFlow<Int> = _totalOrders.asStateFlow()

    private val _totalRevenue = MutableStateFlow(0.0)
    val totalRevenue: StateFlow<Double> = _totalRevenue.asStateFlow()

    private val _chartData = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val chartData: StateFlow<List<Pair<String, Double>>> = _chartData.asStateFlow()

    private val _hourlyChartData = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val hourlyChartData: StateFlow<List<Pair<String, Double>>> = _hourlyChartData.asStateFlow()

    private val _statusChartData = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val statusChartData: StateFlow<List<Pair<String, Double>>> = _statusChartData.asStateFlow()

    private val _categoryRevenue = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val categoryRevenue: StateFlow<List<Pair<String, Double>>> = _categoryRevenue.asStateFlow()

    private val _bestSelling = MutableStateFlow("N/A")
    val bestSelling: StateFlow<String> = _bestSelling.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _availableStatuses = MutableStateFlow<List<String>>(emptyList())
    val availableStatuses: StateFlow<List<String>> = _availableStatuses.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    fun setSelectedStatus(status: String?) {
        _selectedStatus.value = status
        fetchRevenueData(_startDate ?: "01-04-2025", _endDate ?: "07-04-2025")
    }

    private var _startDate: String? = null
    private var _endDate: String? = null

    fun fetchRevenueData(startDate: String, endDate: String) {
        _startDate = startDate
        _endDate = endDate
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val sdfHour = SimpleDateFormat("HH:00", Locale.getDefault())
                val start = sdf.parse(startDate)?.time ?: 0L
                val end = sdf.parse(endDate)?.time ?: Long.MAX_VALUE

                var query = db.collection("orders")
                    .whereGreaterThanOrEqualTo("orderDate", com.google.firebase.Timestamp(start / 1000, 0))
                    .whereLessThanOrEqualTo("orderDate", com.google.firebase.Timestamp(end / 1000, 0))

                // Lọc theo trạng thái nếu có
                if (_selectedStatus.value != null) {
                    query = query.whereEqualTo("status", _selectedStatus.value)
                }

                val snapshot = query.get().await()

                val orders = snapshot.documents.mapNotNull { doc ->
                    try {
                        val order = doc.toObject(OrderItem::class.java)
                        order?.copy(orderId = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }

                // Lấy danh sách trạng thái có sẵn (chỉ gọi 1 lần khi không có dữ liệu)
                if (_availableStatuses.value.isEmpty()) {
                    val allStatuses = db.collection("orders")
                        .get()
                        .await()
                        .documents
                        .mapNotNull { it.getString("status") }
                        .distinct()
                    _availableStatuses.value = listOf("All") + allStatuses
                }

                _totalOrders.value = orders.size
                _totalRevenue.value = orders.sumOf { it.total }

                // Tính chartData theo ngày
                val dailyRevenue = orders.groupBy { sdf.format(it.orderDate.toDate()) }
                    .map { it.key to it.value.sumOf { order -> order.total } }
                _chartData.value = dailyRevenue.sortedBy { sdf.parse(it.first)?.time ?: 0L }

                // Tính chartData theo giờ
                val hourlyRevenue = orders.groupBy { sdfHour.format(it.orderDate.toDate()) }
                    .map { it.key to it.value.sumOf { order -> order.total } }
                _hourlyChartData.value = hourlyRevenue.sortedBy { it.first }

                // Tính chartData theo trạng thái đơn hàng
                val statusRevenue = orders.groupBy { it.status }
                    .map { it.key to it.value.sumOf { order -> order.total } }
                _statusChartData.value = statusRevenue.sortedBy { it.first }

                // Tính best-selling item
                val itemSales = orders.flatMap { it.items }
                    .groupBy { it.name }
                    .mapValues { it.value.sumOf { cartItem -> cartItem.quantity } }
                _bestSelling.value = itemSales.maxByOrNull { it.value }?.key ?: "N/A"

                // Tính doanh thu theo danh mục món ăn
                val foodCategories = mutableMapOf<String, MutableList<String>>()
                val foodRevenue = mutableMapOf<String, Double>()
                orders.flatMap { it.items }.forEach { item ->
                    val foodDoc = db.collection("foods").document(item.foodId).get().await()
                    val tags = foodDoc.get("tags") as? List<String> ?: emptyList()
                    foodCategories[item.foodId] = tags.toMutableList()
                    foodRevenue[item.foodId] = (foodRevenue[item.foodId] ?: 0.0) + (item.price * item.quantity)
                }

                val categoryRevenueMap = mutableMapOf<String, Double>()
                foodCategories.forEach { (foodId, tags) ->
                    tags.forEach { tag ->
                        categoryRevenueMap[tag] = (categoryRevenueMap[tag] ?: 0.0) + (foodRevenue[foodId] ?: 0.0)
                    }
                }
                _categoryRevenue.value = categoryRevenueMap.toList().sortedByDescending { it.second }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error fetching revenue data"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
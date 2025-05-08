package com.example.foodorderingapplication.view.admin

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.RevenueViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

@Composable
fun RevenueScreen(
    navController: NavController,
    viewModel: RevenueViewModel = viewModel()
) {
    var startDate by remember { mutableStateOf("01-05-2025") }
    var endDate by remember { mutableStateOf("30-05-2025") }
    var dateError by remember { mutableStateOf("") }

    LaunchedEffect(startDate, endDate) {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        try {
            val start = sdf.parse(startDate)?.time ?: 0L
            val end = sdf.parse(endDate)?.time ?: Long.MAX_VALUE
            if (end < start) {
                dateError = "End date cannot be before start date"
            } else {
                dateError = ""
                viewModel.fetchRevenueData(startDate, endDate)
            }
        } catch (e: Exception) {
            dateError = "Invalid date format"
        }
    }

    val totalOrders by viewModel.totalOrders.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val hourlyChartData by viewModel.hourlyChartData.collectAsState()
    val statusChartData by viewModel.statusChartData.collectAsState()
    val categoryRevenue by viewModel.categoryRevenue.collectAsState()
    val bestSelling by viewModel.bestSelling.collectAsState()
    val availableStatuses by viewModel.availableStatuses.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HeaderSection("Revenue") {
            navController.popBackStack()
        }
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            } else {
                Text("Revenue Statistics", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                RevenueSummary(totalOrders, totalRevenue, bestSelling)

                Spacer(modifier = Modifier.height(24.dp))

                RevenueDateRange(
                    startDate = startDate,
                    endDate = endDate,
                    onStartChange = { startDate = it },
                    onEndChange = { endDate = it }
                )

                if (dateError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dateError,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                StatusFilter(
                    statuses = availableStatuses,
                    selectedStatus = selectedStatus,
                    onStatusSelected = { status ->
                        viewModel.setSelectedStatus(if (status == "All") null else status)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                var selectedTab by remember { mutableIntStateOf(0) }
                val tabs = listOf("Daily", "Hourly", "Status")

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFFFFC107)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> RevenueChartSection(chartData, "Daily Revenue")
                    1 -> RevenueChartSection(hourlyChartData, "Hourly Revenue")
                    2 -> RevenueChartSection(statusChartData, "Revenue by Status")
                }

                Spacer(modifier = Modifier.height(24.dp))

                CategoryRevenueSection(categoryRevenue)
            }
        }
    }
}

@Composable
fun RevenueSummary(totalOrders: Int, totalRevenue: Double, bestSelling: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CardInfo(title = "Total Orders", value = "$totalOrders")
        CardInfo(title = "Best-selling", value = bestSelling)
        CardInfo(title = "Total Revenue", value = "${DecimalFormat("#,###").format(totalRevenue)}đ")
    }
}

@Composable
fun RevenueDateRange(
    startDate: String,
    endDate: String,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit
) {
    Text("Select date range", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DateTextField(
            label = "From",
            date = startDate,
            onDateChange = onStartChange,
            modifier = Modifier.weight(1f)
        )
        DateTextField(
            label = "To",
            date = endDate,
            onDateChange = onEndChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusFilter(
    statuses: List<String>,
    selectedStatus: String?,
    onStatusSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("Filter by Status", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedStatus ?: "All",
                onValueChange = {},
                readOnly = true,
                label = { Text("Order Status") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                statuses.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            onStatusSelected(status)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RevenueChartSection(chartData: List<Pair<String, Double>>, title: String) {
    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(8.dp))
    if (chartData.isEmpty()) {
        Text(
            text = "No data available",
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    } else {
        RevenueChart(data = chartData)
    }
}

@Composable
fun CategoryRevenueSection(categoryRevenue: List<Pair<String, Double>>) {
    Text("Revenue by Category", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(8.dp))
    if (categoryRevenue.isEmpty()) {
        Text(
            text = "No data available",
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    } else {
        LazyColumn {
            items(categoryRevenue) { (category, revenue) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(category, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(
                        "${DecimalFormat("#,###").format(revenue)}đ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC107)
                    )
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun CardInfo(title: String, value: String) {
    Card(
        modifier = Modifier
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFFFC107))
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DateTextField(
    label: String,
    date: String,
    onDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    LaunchedEffect(date) {
        try {
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            calendar.time = sdf.parse(date) ?: Date()
        } catch (_: Exception) {}
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year)
                onDateChange(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    OutlinedTextField(
        value = date,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Select Date",
                tint = Color.Gray,
                modifier = Modifier.clickable { datePickerDialog.show() }
            )
        },
        modifier = modifier
            .clickable { datePickerDialog.show() }
    )
}

@Composable
fun RevenueChart(data: List<Pair<String, Double>>) {
    val maxValue = data.maxOfOrNull { it.second } ?: 1.0
    val step = maxValue / 4

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .height(200.dp)
                .width(50.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 4 downTo 0) {
                Text(
                    text = "${(step * i).toInt()}đ",
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .drawBehind {
                        for (i in 0..4) {
                            val y = size.height * (1 - i / 4f)
                            drawLine(
                                color = Color.Gray,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    data.forEach { (_, value) ->
                        val barHeightRatio = value / maxValue
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .fillMaxHeight(barHeightRatio.toFloat())
                                .background(Color(0xFFFFC107), RoundedCornerShape(4.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                data.forEach { (label, _) ->
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(40.dp)
                    )
                }
            }
        }
    }
}

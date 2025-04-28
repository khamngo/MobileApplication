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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import com.example.foodorderingapplication.view.HeaderSection
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

@Composable
fun RevenueScreen(
    navController: NavController,
    totalOrders: Int = 120,
    totalRevenue: Double = 5020000.0,
    chartData: List<Pair<String, Double>> = listOf(
        "01-04" to 100000.0,
        "02-04" to 200000.0,
        "03-04" to 150000.0,
        "04-04" to 300000.0,
    )
) {
    var startDate by remember { mutableStateOf("01-04-2025") }
    var endDate by remember { mutableStateOf("07-04-2025") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HeaderSection("Revenue"){
            navController.popBackStack()
        }
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Revenue Statistics", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            RevenueSummary(totalOrders, totalRevenue)

            Spacer(modifier = Modifier.height(24.dp))

            RevenueDateRange(startDate, endDate, onStartChange = { startDate = it }, onEndChange = { endDate = it })

            Spacer(modifier = Modifier.height(24.dp))

            RevenueChartSection(chartData)
        }
    }
}

@Composable
fun RevenueSummary(totalOrders: Int, totalRevenue: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CardInfo(title = "Total Orders", value = "$totalOrders")
        CardInfo(title = "Best-selling", value = "Bimbap")
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
        horizontalArrangement = Arrangement.spacedBy(12.dp) // Khoảng cách giữa các phần tử
    ) {
        DateTextField(
            label = "From",
            date = startDate,
            onDateChange = onStartChange,
            modifier = Modifier.weight(1f) // Chia đều không gian
        )
        DateTextField(
            label = "To",
            date = endDate,
            onDateChange = onEndChange,
            modifier = Modifier.weight(1f) // Chia đều không gian
        )
    }
}

@Composable
fun RevenueChartSection(chartData: List<Pair<String, Double>>) {
    Text("Revenue Chart", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(8.dp))
    RevenueChart(data = chartData)
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
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp,color = Color(0xFFFFC107))
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

    // Cập nhật Calendar theo date hiện tại
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
        modifier = modifier
            .clickable {
                datePickerDialog.show()
            }
    )
}


@Composable
fun RevenueChart(data: List<Pair<String, Double>>) {
    val maxValue = data.maxOfOrNull { it.second } ?: 1.0
    val step = maxValue / 4 // Chia trục tung thành 4 đoạn

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Trục tung
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

        // Biểu đồ cột với đường kẻ ngang
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .drawBehind {
                        // Vẽ các đường kẻ ngang
                        for (i in 0..4) {
                            val y = size.height * (1 - i / 4f) // Tính vị trí y của đường ngang
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

            // Trục hoành (ngày)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                data.forEach { (date, _) ->
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(40.dp)
                    )
                }
            }
        }
    }
}


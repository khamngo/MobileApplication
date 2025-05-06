package com.example.foodorderingapplication.view.home

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.RestaurantItem
import com.example.foodorderingapplication.view.menu.RestaurantSelectionSection
import com.example.foodorderingapplication.view.menu.SearchBar
import java.util.Calendar
import android.app.DatePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.view.BottomNavBar
import com.example.foodorderingapplication.view.menu.TopBar

val restaurantItems = listOf(
    RestaurantItem(
        "KFOODS1 - LANDMARK 81",
        "720A Điện Biên Phủ, P22, Q. Bình Thạnh, TP HCM",
        "056 3167 5325",
        "8:30 AM - 10:00 PM"
    ),
    RestaurantItem(
        "KFOODS2 - BITEXCO",
        "2 Hải Triều, Quận 1, TP HCM",
        "056 3428 8245",
        "9:30 AM - 11:00 PM"
    )
)

@Composable
fun HomeScreen(navController: NavController) {
    var selectedDate by remember { mutableStateOf("") }
    var numberOfDinner by remember { mutableIntStateOf(1) }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(Color(0xFFF7F7F7))
                .verticalScroll(rememberScrollState())
        ) {
            TopBar()
            Column(modifier = Modifier
                .padding(16.dp)) {
                DateTimePicker { selectedDate = it }

                Spacer(modifier = Modifier.height(16.dp))

                DinnerCounter(numberOfDinner) { numberOfDinner = it }

                Spacer(modifier = Modifier.height(16.dp))

//                RestaurantSelectionSection(restaurantItems)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("thanks") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
                ) {
                    Text(
                        "Confirm",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DateTimePicker(onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    var selectedDateTime by remember { mutableStateOf("") } // Lưu ngày + giờ được chọn

    // Date Picker Dialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            // Khi đã chọn ngày, mở TimePicker
            TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    val formatted = "%02d/%02d/%04d %02d:%02d".format(
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear,
                        selectedHour,
                        selectedMinute
                    )
                    selectedDateTime = formatted
                    onDateSelected(formatted)
                },
                hour,
                minute,
                true // is24HourView
            ).show()
        },
        year,
        month,
        day
    )
    Text("Date and Time ? *", fontWeight = FontWeight.Bold, fontSize = 20.sp)
    OutlinedTextField(
        value = selectedDateTime,
        onValueChange = {},
        label = { Text("Date and Time") },
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(Icons.Default.DateRange, contentDescription = null)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DinnerCounter(count: Int, onCountChange: (Int) -> Unit) {
    Column {
        Text("How many dinner ? *", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 8.dp).fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {
            IconButton(onClick = { if (count > 0) onCountChange(count - 1) }) {
                Icon(Icons.Default.Remove, contentDescription = null)
            }
            Text(
                count.toString().padStart(2, '0'),
                fontSize = 18.sp,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )
            IconButton(onClick = { onCountChange(count + 1) }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NavigationGraph()
}

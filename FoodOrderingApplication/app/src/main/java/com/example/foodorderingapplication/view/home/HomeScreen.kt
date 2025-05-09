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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.view.BottomNavBar
import com.example.foodorderingapplication.view.menu.FoodItems
import com.example.foodorderingapplication.view.menu.RestaurantScreen
import com.example.foodorderingapplication.view.menu.TopBar
import com.example.foodorderingapplication.viewmodel.FoodViewModel
import com.example.foodorderingapplication.viewmodel.RestaurantViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: FoodViewModel = viewModel()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F7F7)),
            ) {
                item { TopBar() }

                if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
                    items(searchResults) { food ->
                        FoodItems(food, onClick = {
                            navController.navigate("food_detail/${food.id}")
                        })
                        HorizontalDivider(modifier = Modifier.padding(4.dp))
                    }
                } else {
                    item {
                        TableSection{
                            navController.navigate("thanks")
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

@Composable
fun TableSection(onClick:()-> Unit){
    Column(modifier = Modifier.padding(16.dp)) {
        val resViewModel: RestaurantViewModel = viewModel()
        var selectedDate by remember { mutableStateOf("") }
        var numberOfDinner by remember { mutableIntStateOf(1) }
        var restaurant by remember { mutableStateOf(RestaurantItem()) }
        val restaurantItems by resViewModel.restaurantItems.collectAsState()

        LaunchedEffect(restaurantItems) {
            if (restaurantItems.isNotEmpty()) {
                restaurant = restaurantItems.first()
            }
        }

        DateTimePicker { selectedDate = it }
        Spacer(modifier = Modifier.height(16.dp))
        DinnerCounter(numberOfDinner) { numberOfDinner = it }
        Spacer(modifier = Modifier.height(16.dp))
        RestaurantScreen(viewModel = resViewModel, selectedRestaurant = restaurant)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onClick() },
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
                true
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


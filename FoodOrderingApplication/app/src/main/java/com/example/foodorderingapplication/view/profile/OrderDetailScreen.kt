package com.example.foodorderingapplication.view.profile

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ComponentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.model.OrderItem
import com.example.foodorderingapplication.model.OrderStatus
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.admin.DeliveryStatusSection
import com.example.foodorderingapplication.viewmodel.OrderDetailViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String,
    viewModel: OrderDetailViewModel = viewModel()
) {
    val orderDetail by viewModel.orderDetail.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val hasReviewed by viewModel.hasReviewed.collectAsState()

    // Dialog xác nhận hủy
    var showCancelDialog by remember { mutableStateOf(false) }

    // State cho picker
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    // Gọi fetchOrderDetail khi khởi tạo
    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetail(orderId)
    }

    // Dialog xác nhận hủy
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Confirm Cancel") },
            text = { Text("Are you sure you want to cancel this order?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelOrder(orderId) {
                        showCancelDialog = false
                        Toast.makeText(context, "Order cancelled successfully!", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("No") }
            }
        )
    }

    // DatePicker và TimePicker
    if (showDatePicker) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Delivery Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selection)
            selectedDate = date
            showDatePicker = false
            showTimePicker = true
        }
        datePicker.addOnDismissListener { showDatePicker = false }
        datePicker.show((context as AppCompatActivity).supportFragmentManager, "DATE_PICKER")
    }

    if (showTimePicker) {
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText("Select Delivery Time")
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val amPm = if (hour >= 12) "PM" else "AM"
            val hour12 = if (hour % 12 == 0) 12 else hour % 12
            selectedTime = String.format("%02d:%02d %s", hour12, minute, amPm)
            viewModel.buyAgain(orderId, selectedDate, selectedTime) {
                selectedDate = ""
                selectedTime = ""
                showTimePicker = false
                Toast.makeText(context, "New order created!", Toast.LENGTH_SHORT).show()
            }
        }
        timePicker.addOnDismissListener { showTimePicker = false }
        timePicker.show((context as AppCompatActivity).supportFragmentManager, "TIME_PICKER")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (orderDetail == null) {
            Text(
                text = "Order not found",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 16.sp,
                color = Color.Red
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 120.dp)
            ) {
                // Header
                HeaderSection("Order Detail") {
                    navController.popBackStack()
                }

                // Thông báo trong UI
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = if (errorMessage.contains("successfully")) Color.Green else Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Status delivery progress
                DeliveryStatusSection(orderDetail!!.status)

                HorizontalDivider()

                // From / To Info
                FromToInfo(orderDetail!!)

                HorizontalDivider()

                // Order Detail
                OrderDetailContent(orderDetail!!)

                Spacer(modifier = Modifier.weight(1f))
            }

            // Subtotal & Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                OrderActionButton(
                    orderStatus = OrderStatus.valueOf(orderDetail?.status ?: "Preparing"),
                    onCancel = { showCancelDialog = true },
                    onBuyAgain = { showDatePicker = true },
                    onReview = { navController.navigate("review/$orderId") },
                    hasReviewed = hasReviewed
                )
            }
        }
    }
}

@Composable
fun FromToInfo(order: OrderItem) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(color = Color.Red)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("From", fontWeight = FontWeight.Bold, color = Color.Red)
            }
            Text(order.shippingAddress.restaurant.name)
            Text(
                order.shippingAddress.restaurant.address,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(color = Color.Green)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("To", fontWeight = FontWeight.Bold, color = Color.Green)
            }
            Text(
                "${order.shippingAddress.street}, ${order.shippingAddress.ward}, " +
                        "${order.shippingAddress.district}, ${order.shippingAddress.province}"
            )
            Text(
                "${order.shippingAddress.firstName} ${order.shippingAddress.lastName} - " +
                        order.shippingAddress.phoneNumber,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun OrderDetailContent(order: OrderItem) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val orderDate = dateFormat.format(order.orderDate.toDate())

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Order detail", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        order.items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.Bold)
                    if (item.portion.isNotEmpty()) {
                        Text("Portion: ${item.portion}")
                    }
                    if (item.drink.isNotEmpty()) {
                        Text("Drink: ${item.drink}")
                    }
                    if (item.instructions.isNotEmpty()) {
                        Text("Instructions: ${item.instructions}")
                    }
                    Text("Quantity: ${item.quantity}")
                }

                Text(
                    currencyFormat.format(item.price * item.quantity),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        OrderInfoRow("Order id:", order.orderId)
        OrderInfoRow("Order date:", orderDate)
        OrderInfoRow("Delivery date:", order.deliveryDate)
        OrderInfoRow("Delivery time:", order.deliveryTime)
        OrderInfoRow("Payment method:", order.paymentMethod)
        OrderInfoRow("Promo:", order.promo.ifEmpty { "None" })
        OrderInfoRow("Subtotal:", currencyFormat.format(order.subtotal))
        OrderInfoRow("Shipping fee:", currencyFormat.format(order.shippingFee))
        OrderInfoRow("Taxes:", currencyFormat.format(order.taxes))
        OrderInfoRow("Discount:", currencyFormat.format(order.discount))

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                currencyFormat.format(order.total),
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun OrderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value)
    }
}

@Composable
fun OrderActionButton(
    orderStatus: OrderStatus,
    onCancel: () -> Unit,
    onBuyAgain: () -> Unit,
    onReview: () -> Unit,
    hasReviewed: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (orderStatus) {
            OrderStatus.Preparing -> {
                Button(
                    onClick = onCancel,
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700)
                    )
                ) {
                    Text(
                        "Cancel Order",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            OrderStatus.Delivered -> {
                if (hasReviewed) {
                    Button(
                        onClick = onBuyAgain,
                        enabled = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700)
                        )
                    ) {
                        Text(
                            "Buy Again",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onBuyAgain,
                            enabled = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700)
                            )
                        ) {
                            Text(
                                "Buy Again",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = onReview,
                            enabled = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700)
                            )
                        ) {
                            Text(
                                "Review",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            OrderStatus.Shipped -> {
                Button(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Color(0xFFE0E0E0),
                        disabledContentColor = Color.DarkGray
                    )
                ) {
                    Text(
                        "In Transit",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            OrderStatus.Cancelled -> {
                Button(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Color(0xFFE0E0E0),
                        disabledContentColor = Color.DarkGray
                    )
                ) {
                    Text(
                        "Cancelled",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray
) {
    Divider(
        modifier = modifier,
        color = color,
        thickness = 1.dp
    )
}

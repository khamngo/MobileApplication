package com.example.foodorderingapplication.view.profile

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.model.OrderItem
import com.example.foodorderingapplication.model.OrderStatus
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.admin.DeliveryStatusSection
import com.example.foodorderingapplication.view.admin.InfoRow
import com.example.foodorderingapplication.viewmodel.OrderDetailViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetail(orderId)
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Confirm Cancel") },
            text = { Text("Are you sure you want to cancel this order?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelOrder(orderId) {
                        showCancelDialog = false
                        Toast.makeText(context, "Order cancelled successfully!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("No") }
            }
        )
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
                    onBuyAgain = {
                        val currentTime = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                        val selectedDate = dateFormat.format(currentTime.time)
                        val selectedTime = timeFormat.format(currentTime.time)

                        viewModel.buyAgain(orderId, selectedDate, selectedTime) {
                            Toast.makeText(context, "New order created!", Toast.LENGTH_SHORT).show()
                        }
                        navController.popBackStack()
                    },

                    onReview = { navController.navigate("review/$orderId") },
                    hasReviewed = hasReviewed
                )
            }
        }
    }
}

@Composable
fun FromToInfo(order: OrderItem) {
    val formattedDate = SimpleDateFormat("h a : dd-MM-yyyy", Locale.getDefault())
        .format(order.orderDate.toDate())

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = formattedDate,
            fontSize = 14.sp,
            color = Color.Gray,
            fontStyle = FontStyle.Italic
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
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

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
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

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Order detail", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        order.items.forEach { item ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(8.dp)
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

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.Bold)
                    Text("${item.price}",    fontWeight = FontWeight.Bold)
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
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Order Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            InfoRow("Order ID:", order.orderId)
            InfoRow("First Name:", order.shippingAddress.firstName)
            InfoRow("Last Name:", order.shippingAddress.lastName)
            InfoRow("Phone Number:", order.shippingAddress.phoneNumber)
            InfoRow(
                "Address:",
                "${order.shippingAddress.street}, ${order.shippingAddress.ward}, " +
                        "${order.shippingAddress.district}, ${order.shippingAddress.province}"
            )
            InfoRow("Delivery Date:", order.deliveryDate)
            InfoRow("Delivery Time:", order.deliveryTime)
            InfoRow("Promo:", order.promo.ifEmpty { "None" })
            InfoRow("Payment Method:", order.paymentMethod)
            InfoRow("Subtotal:", currencyFormat.format(order.subtotal))
            InfoRow("Shipping Fee:", currencyFormat.format(order.shippingFee))
            InfoRow("Taxes:", currencyFormat.format(order.taxes))
            InfoRow("Discount:", currencyFormat.format(order.discount))
        }

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

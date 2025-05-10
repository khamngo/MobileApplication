package com.example.foodorderingapplication.view.admin

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.model.OrderItem
import com.example.foodorderingapplication.model.OrderStatus
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.AdminOrderDetailViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String,
    viewModel: AdminOrderDetailViewModel = viewModel()
) {
    val orderDetail by viewModel.orderDetail.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetail(orderId)
    }

    // Dialog xác nhận duyệt
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm") },
            text = { Text("Are you sure you want to approve this order?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.acceptOrder(orderId) {
                            Toast.makeText(
                                context,
                                "Order approved successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Agree")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog xác nhận hủy
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Confirm Cancel") },
            text = { Text("Are you sure you want to cancel this order?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelOrder(orderId) { showCancelDialog = false }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("No") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
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

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            orderDetail?.let { order ->
                DeliveryStatusSection(order.status)
                HorizontalDivider()
                OrderItemCard(
                    order = order,
                    onAcceptClick = { showConfirmDialog = true },
                    onCancelClick = { showCancelDialog = true },
                    onBuyAgainClick = {
                        val currentTime = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                        val selectedDate = dateFormat.format(currentTime.time)
                        val selectedTime = timeFormat.format(currentTime.time)

                        viewModel.buyAgain(orderId, selectedDate, selectedTime) {
                            Toast.makeText(context, "New order created!", Toast.LENGTH_SHORT).show()
                        }
                        navController.popBackStack()
                    }
                )
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Order not found", fontSize = 16.sp, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun DeliveryStatusSection(status: String) {
    val statusIndex = when (status) {
        OrderStatus.Preparing.name -> 0
        OrderStatus.Shipped.name -> 1
        OrderStatus.Delivered.name -> 2
        OrderStatus.Cancelled.name -> -1
        else -> 0
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Delivery Status",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        AnimatedContent(
            targetState = statusIndex,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            }
        ) { targetIndex ->
            if (targetIndex == -1) {
                Text(
                    "Order Cancelled",
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = null,
                            tint = if (targetIndex >= 0) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier.scale(if (targetIndex >= 0) 1.2f else 1.0f)
                        )
                        Text("Preparing", fontSize = 12.sp)
                    }
                    HorizontalDivider(
                        modifier = Modifier.width(40.dp),
                        color = if (targetIndex >= 1) Color(0xFFFFD700) else Color.Gray
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = if (targetIndex >= 1) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier.scale(if (targetIndex >= 1) 1.2f else 1.0f)
                        )
                        Text("Shipped", fontSize = 12.sp)
                    }
                    HorizontalDivider(
                        modifier = Modifier.width(40.dp),
                        color = if (targetIndex >= 2) Color(0xFFFFD700) else Color.Gray
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = if (targetIndex >= 2) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier.scale(if (targetIndex >= 2) 1.2f else 1.0f)
                        )
                        Text("Delivered", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(
    order: OrderItem,
    onAcceptClick: () -> Unit,
    onCancelClick: () -> Unit,
    onBuyAgainClick: () -> Unit
) {
    val formattedDate = SimpleDateFormat("h a : dd-MM-yyyy", Locale.getDefault())
        .format(order.orderDate.toDate())
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Time & Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formattedDate,
                fontSize = 14.sp,
                color = Color.Gray,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = order.status,
                fontSize = 16.sp,
                color = when (order.status) {
                    "Preparing" -> Color(0xFFFFC107)
                    "Shipped" -> Color(0xFF2196F3)
                    "Delivered" -> Color(0xFF34A854)
                    "Cancelled" -> Color(0xFFFF5151)
                    else -> Color.Black
                },
                fontWeight = FontWeight.Bold
            )
        }

        // From / To Info
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

        Spacer(modifier = Modifier.height(6.dp))

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

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // List Cart Items
        Text("Order Items", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        order.items.forEach { cartItem ->
            CartItemRow(cartItem)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Order Information
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

        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

        // Total & Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total: ${currencyFormat.format(order.total)}",
                fontSize = 18.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            if (order.status == OrderStatus.Preparing.name) {
                Button(
                    onClick = onCancelClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.padding(end = 6.dp))

                Button(
                    onClick = onAcceptClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Accept", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else if (order.status == OrderStatus.Delivered.name) {
                Button(
                    onClick = onBuyAgainClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Buy Again", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        AsyncImage(
            model = cartItem.imageUrl,
            contentDescription = cartItem.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cartItem.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = currencyFormat.format(cartItem.price),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Quantity: ${cartItem.quantity}",
                fontSize = 14.sp
            )
            Text(
                text = "Portion: ${cartItem.portion}",
                fontSize = 14.sp
            )
            Text(
                text = "Drink: ${cartItem.drink}",
                fontSize = 14.sp
            )
            Text(
                text = "Note: ${cartItem.instructions}",
                fontSize = 14.sp
            )
        }

        Text(
            text = currencyFormat.format(cartItem.price * cartItem.quantity),
            fontSize = 14.sp,
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp, color = Color.Black)
    }
}

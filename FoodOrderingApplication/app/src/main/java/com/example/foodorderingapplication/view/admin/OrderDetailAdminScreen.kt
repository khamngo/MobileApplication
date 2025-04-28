package com.example.foodorderingapplication.view.admin

import android.R.attr.fontStyle
import android.R.attr.order
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.model.OrderItem
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.OrderDetailViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String,
    viewModel: OrderDetailViewModel = viewModel()
) {
    val orderDetail by viewModel.orderDetail.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(text = "Xác nhận") },
            text = { Text(text = "Bạn có chắc chắn muốn duyệt đơn hàng này không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.acceptOrder()
                        Toast.makeText(context, "Đơn hàng đã duyệt thành công!", Toast.LENGTH_SHORT)
                            .show()
                    }
                ) {
                    Text("Đồng ý")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Xác nhận hủy đơn hàng") },
            text = { Text("Bạn có chắc chắn muốn hủy đơn hàng này?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelOrder()
                        showDialog = false
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetail(orderId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        HeaderSection("Order Detail") {
            navController.popBackStack()
        }

        orderDetail?.let { order ->
            OrderItemCard(
                order = order,
                onAcceptClick = {
                    showConfirmDialog = true
                },
                onCancelClick = {
                    showDialog = true
                }
            )
        }
            ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
    }
}

@Composable
fun OrderItemCard(
    order: OrderItem,
    onAcceptClick: (OrderItem) -> Unit,
    onCancelClick: (OrderItem) -> Unit
) {
    val formattedDate = SimpleDateFormat("h a : dd-MM-yyyy", Locale.getDefault())
        .format(order.orderDate.toDate())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Time & Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = formattedDate, fontSize = 14.sp, color = Color.Gray,
                fontStyle = FontStyle.Italic
            )

            Text(
                text = order.status,
                fontSize = 16.sp,
                color = when (order.status) {
                    "Preparing" -> Color(0xFFFFC107)
                    "Completed" -> Color(0xFF34A854)
                    "Cancelled" -> Color(0xFFFF5151)
                    else -> Color.Black
                },
                fontWeight = FontWeight.Bold
            )
        }

        // List Cart Items
        order.items.forEach { cartItem ->
            CartItemRow(cartItem)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Order Information
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Information order", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            InfoRow(label = "First Name:", value = order.shippingAddress.firstName)
            InfoRow(label = "Last Name:", value = order.shippingAddress.lastName)
            InfoRow(label = "Phone Number:", value = order.shippingAddress.phoneNumber)
            InfoRow(label = "Address:", value = order.shippingAddress.province)
            InfoRow(label = "Payment Gateway:", value = order.paymentMethod)
        }

        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

        // Total & Accept button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total: $${order.total}",
                fontSize = 18.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onCancelClick(order) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (order.status) {
                        "Preparing" -> Color(0xFFFFD700)
                        "Completed", "Cancelled" -> Color.Gray
                        else -> Color.Gray
                    }
                ),
                enabled = order.status == "Preparing",
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Cancel", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.padding(end = 6.dp))

            Button(
                onClick = { onAcceptClick(order) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (order.status) {
                        "Preparing" -> Color(0xFFFFD700)
                        "Completed", "Cancelled" -> Color.Gray
                        else -> Color.Gray
                    }
                ),
                enabled = order.status == "Preparing",
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Accept", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun InfoRow(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp, color = Color.Black)
    }
}

@Composable
fun CartItemRow(cartItem: CartItem) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        AsyncImage(
            model = cartItem.imageUrl,
            contentDescription = cartItem.name,
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = cartItem.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "$${cartItem.price}",
                fontSize = 14.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold
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
    }
}





package com.example.foodorderingapplication.view.menu

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.MoMoPaymentWebView
import com.example.foodorderingapplication.view.SubtotalAndButton
import com.example.foodorderingapplication.viewmodel.CartViewModel
import com.example.foodorderingapplication.viewmodel.CheckoutViewModel
import com.example.foodorderingapplication.viewmodel.ShippingViewModel
import java.text.DecimalFormat

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = viewModel(),
    navController: NavController
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val total by viewModel.total.collectAsState()
    val selectedPromo by viewModel.selectedPromo.collectAsState()

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("refresh")
            ?.observe(navController.currentBackStackEntry!!) {
                viewModel.reloadShippingAddress()
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            // Header
            HeaderSection("Checkout", navController)

            // Shipping, Delivery, Promos
            ShippingDeliveryPromosSection(viewModel, navController)

            // Items List
            ItemsList(cartItems)

            // Order Summary
            OrderSummary(
                subtotal = subtotal,
                total = total,
                isFreeShipping = selectedPromo == "Free Shipping"
            )

            // Payment Method
            PaymentMethod(viewModel)
        }

        // Subtotal & Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
        ) {
            SubtotalAndButton(
                title = "Place Order",
                subtotal = total,
                navController = navController,
                pageName = "thank_you",
                onButtonClick = { viewModel.placeOrder() }
            )
        }
    }
}

@Composable
fun ShippingDeliveryPromosSection(viewModel: CheckoutViewModel, navController: NavController) {
    val showDialog = remember { mutableStateOf(false) }
    val selectedDate by viewModel.deliveryDate.collectAsState()
    val selectedTime by viewModel.deliveryTime.collectAsState()
    val showPromoDialog = remember { mutableStateOf(false) }
    val selectedPromo by viewModel.selectedPromo.collectAsState()
    val shippingAddress by viewModel.shippingAddress.collectAsState()
    val fullAddress = listOf(
        shippingAddress.street,
        shippingAddress.ward,
        shippingAddress.district,
        shippingAddress.province
    ).filter { it.isNotBlank() }.joinToString(", ")

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider()

        ShippingRow(
            "SHIPPING",
            fullAddress.ifEmpty { "Add shipping address" }
        ) {
            navController.navigate("add_shopping_address")
        }
        HorizontalDivider()

        ShippingRow("DELIVERY", "$selectedDate, $selectedTime") {
            showDialog.value = true
        }
        HorizontalDivider()

        ShippingRow("PROMOS", selectedPromo) {
            showPromoDialog.value = true
        }
        HorizontalDivider()

        // Hiển thị dialog chọn ngày giao hàng
        DeliveryTimeDialog(showDialog) { date, time ->
            viewModel.updateDeliveryTime(date, time)
        }

        // Hiển thị dialog chọn mã giảm giá
        PromoDialog(showPromoDialog) { promo ->
            viewModel.updatePromo(promo)
        }
    }
}

@Composable
fun ShippingRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.Bold)

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value, maxLines = 1,
                overflow = TextOverflow.Ellipsis, color = Color.Gray
            )
            IconButton(onClick = onClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = "Arrow Forward",
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun ItemsList(cartItemItems: List<CartItem>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ITEMS", fontWeight = FontWeight.Bold)
            Text("DESCRIPTION", fontWeight = FontWeight.Bold)
            Text("PRICE", fontWeight = FontWeight.Bold)
        }

        cartItemItems.forEach { cart ->
            ItemRow(cart)
        }
    }
}

@Composable
fun ItemRow(cartItem: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cartItem.imageUrl,
            contentDescription = cartItem.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(4.dp)),
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.image_error)
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(cartItem.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Quantity: ${cartItem.quantity}", fontSize = 14.sp)
        }

        Text("$${"%.2f".format(cartItem.price)}", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OrderSummary(subtotal: Double, total: Double, isFreeShipping: Boolean) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        SummaryRow("Subtotal", "$${"%.2f".format(subtotal)}")
        SummaryRow("Shipping total", if (isFreeShipping) "Free" else "$2.00")
        SummaryRow("Taxes", "$2.0")
        HorizontalDivider()
        SummaryRow(
            "Total",
            "$${"%.2f".format(total)}",
            Color.Red,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    textColor: Color = Color.Black,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = fontSize, fontWeight = fontWeight)
        Text(text = value, fontSize = fontSize, fontWeight = fontWeight, color = textColor)
    }
}

@Composable
fun PaymentMethod(viewModel: CheckoutViewModel) {
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val orderStatus by viewModel.orderStatus.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Payment Method", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        PaymentOption(
            icon = R.drawable.momo_method, // Icon MoMo
            title = "MoMo",
            selected = paymentMethod == "MoMo"
        ) {
            viewModel.updatePaymentMethod("MoMo")
        }

        PaymentOption(
            icon = R.drawable.cod_method,
            title = "Cash on Delivery",
            selected = paymentMethod == "COD"
        ) {
            viewModel.updatePaymentMethod("COD")
        }

        // Hiển thị WebView cho MoMo hoặc Bank Card
        orderStatus?.let { status ->
            if (status.startsWith("Chuyển hướng đến MoMo: ") || status.startsWith("Chuyển hướng đến thanh toán thẻ: ")) {
                val paymentUrl = status.substringAfter(": ")
                MoMoPaymentWebView(paymentUrl) { success ->
                    viewModel.updateOrderStatus(
                        orderId = status.substringAfter("orderId=").substringBefore("&"),
                        status = if (success) "completed" else "failed"
                    )
                }
            } else {
                Text(status, color = if (status.contains("Lỗi")) Color.Red else Color.Green)
            }
        }
    }
}

@Composable
fun PaymentOption(
    @DrawableRes icon: Int,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

@Composable
fun DeliveryTimeDialog(showDialog: MutableState<Boolean>, onConfirm: (String, String) -> Unit) {
    val selectedDate = remember { mutableStateOf("Today") }
    val selectedTime = remember { mutableStateOf("Now") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Change delivery time", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { showDialog.value = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date", fontWeight = FontWeight.Bold)
                        Text("Time", fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider()
                    DeliveryOption("Today", "Now", selectedDate, selectedTime)
                    HorizontalDivider()
                    DeliveryOption("05/03/2025", "16:15", selectedDate, selectedTime)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(selectedDate.value, selectedTime.value)
                        showDialog.value = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("Confirm")
                }
            }
        )
    }
}

@Composable
fun DeliveryOption(
    date: String, time: String,
    selectedDate: MutableState<String>,
    selectedTime: MutableState<String>
) {
    val isSelected = selectedDate.value == date && selectedTime.value == time

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                selectedDate.value = date
                selectedTime.value = time
            }
            .background(if (isSelected) Color(0xFFFFD500) else Color.Transparent) // Màu nền khi được chọn
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(date)
        }
        Text(time)
    }
}

@Composable
fun PromoDialog(showDialog: MutableState<Boolean>, onPromoSelected: (String) -> Unit) {
    val promoOptions = listOf(
        "Free Shipping",
        "5% off for orders above 5$",
        "10% off for orders above 10$",
        "15% off for orders above 20$"
    )
    val selectedPromo = remember { mutableStateOf(promoOptions[0]) } // Mặc định là "Free Shipping"

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Select Promo Code") },
            text = {
                Column {
                    promoOptions.forEach { promo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPromo.value = promo
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedPromo.value == promo),
                                onClick = { selectedPromo.value = promo }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(promo, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onPromoSelected(selectedPromo.value)
                    showDialog.value = false
                }) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview1() {
    NavigationGraph()
}
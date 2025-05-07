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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.SubtotalAndButton
import com.example.foodorderingapplication.viewmodel.CartViewModel
import com.example.foodorderingapplication.viewmodel.CheckoutViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    val taxes = viewModel.taxes

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle = currentBackStackEntry?.savedStateHandle

    // Listen for the result
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<Boolean?>("add_shopping_address", null)
            ?.collect { updated ->
                if (updated == true) {
                    viewModel.reloadShippingAddress()
                    // Reset để lần sau không bị gọi lại
                    savedStateHandle["add_shopping_address"] = null
                }
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
            HeaderSection("Checkout") {
                navController.popBackStack()
            }

            // Shipping, Delivery, Promos
            ShippingDeliveryPromosSection(viewModel, navController)

            Spacer(modifier = Modifier.height(6.dp))

            // Items List
            ItemsList(cartItems)

            Spacer(modifier = Modifier.height(6.dp))

            // Order Summary
            OrderSummary(
                taxes = taxes,
                subtotal = subtotal,
                total = total,
                isFreeShipping = selectedPromo == "Free Shipping"
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Payment Method
            PaymentMethod(viewModel)

            Spacer(modifier = Modifier.height(6.dp))

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

@RequiresApi(Build.VERSION_CODES.O)
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

    val subtotal by viewModel.subtotal.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
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

        DeliveryTimeDialog(showDialog) { date, time ->
            viewModel.updateDeliveryTime(date, time)
        }

        PromoDialog(showDialog = showPromoDialog, subtotal = subtotal) {
            viewModel.updatePromo(it)
        }
    }
}

@Composable
fun ShippingRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title with fixed width to avoid shifting when text length changes
        Box(
            modifier = Modifier.width(80.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(24.dp)
            ) {
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
fun ItemsList(cartItemItems: List<CartItem>, viewModel: CartViewModel = viewModel()) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ITEMS", fontWeight = FontWeight.Bold)
            Text("DESCRIPTION", fontWeight = FontWeight.Bold)
            Text("PRICE", fontWeight = FontWeight.Bold)
        }

        cartItemItems.forEach { cart ->
            ItemRow(cart)
            InstructionInput(cart, viewModel)
        }

    }
}

@Composable
fun ItemRow(cartItem: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = cartItem.imageUrl,
            contentDescription = cartItem.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(4.dp)),
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.image_error)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(0.4f)
        ) {
            Text(
                cartItem.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("Portion: ${cartItem.portion}")
            Text("Drink: ${cartItem.drink}")
            Text("Quantity: ${cartItem.quantity}", fontSize = 14.sp)
        }

        Text(
            "$${"%.2f".format(cartItem.price)}",
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun OrderSummary(taxes: Double, subtotal: Double, total: Double, isFreeShipping: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            SummaryRow("Subtotal", "$${"%.2f".format(subtotal)}")
            SummaryRow("Shipping total", if (isFreeShipping) "Free" else "$2.00")
            SummaryRow("Taxes", "$taxes")
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
            .background(Color.White)
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

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Payment Method", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            PaymentOption(
                icon = R.drawable.cod_method,
                title = "Cash on Delivery",
                selected = paymentMethod == "COD"
            ) {
                viewModel.updatePaymentMethod("COD")
            }

            PaymentOption(
                icon = R.drawable.momo_method,
                title = "MoMo",
                selected = paymentMethod == "MoMo"
            ) {
                viewModel.updatePaymentMethod("MoMo")
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
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeliveryTimeDialog(
    showDialog: MutableState<Boolean>,
    onConfirm: (String, String) -> Unit
) {
    val selectedTime = remember { mutableStateOf("") }

    val now = remember { LocalTime.now() }
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val fullDateFormat = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.ENGLISH)

    val today = remember { LocalDate.now() }
    val todayLabel = today.format(fullDateFormat).replaceFirstChar { it.uppercase() }

    val timeSlots = remember {
        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(22, 0)
        val slots = mutableListOf<String>()
        var current = startTime
        while (current <= endTime) {
            if (current.isAfter(now)) {
                slots.add(current.format(formatter))
            }
            current = current.plusMinutes(30)
        }
        slots
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select delivery time", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { showDialog.value = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Today: $todayLabel",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider()

                    LazyColumn(
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth()
                    ) {
                        items(timeSlots) { time ->
                            DeliveryTimeItem(
                                time = time,
                                selectedTime = selectedTime
                            )
                            Divider()
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(today.toString(), selectedTime.value)
                        showDialog.value = false
                    },
                    enabled = selectedTime.value.isNotBlank(),
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
fun DeliveryTimeItem(
    time: String,
    selectedTime: MutableState<String>
) {
    val isSelected = selectedTime.value == time

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedTime.value = time }
            .background(if (isSelected) Color(0xFFFFD700) else Color.Transparent)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Schedule,
            contentDescription = null,
            tint = if (isSelected) Color.White else Color.Gray
        )
        Text(
            text = time,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun PromoDialog(
    showDialog: MutableState<Boolean>,
    subtotal: Double,
    onPromoSelected: (String) -> Unit
) {
    val promoOptions = listOf(
        "Free Shipping",
        "5% off for orders above 5$",
        "10% off for orders above 10$",
        "15% off for orders above 20$"
    )

    val selectedPromo = remember { mutableStateOf(promoOptions[0]) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Select Promo Code") },
            text = {
                Column {
                    promoOptions.forEach { promo ->
                        val isEnabled = when (promo) {
                            "Free Shipping" -> true
                            "5% off for orders above 5$" -> subtotal > 5.0
                            "10% off for orders above 10$" -> subtotal > 10.0
                            "15% off for orders above 20$" -> subtotal > 20.0
                            else -> false
                        }

                        val textColor = if (isEnabled) Color.Black else Color.Gray

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = isEnabled) {
                                    selectedPromo.value = promo
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedPromo.value == promo),
                                onClick = {
                                    if (isEnabled) selectedPromo.value = promo
                                },
                                enabled = isEnabled
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(promo, fontSize = 16.sp, color = textColor)
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

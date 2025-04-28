package com.example.foodorderingapplication.view.menu

import android.R.attr.onClick
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.BottomNavItem
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.SubtotalAndButton
import com.example.foodorderingapplication.viewmodel.CartViewModel
import com.example.foodorderingapplication.viewmodel.FoodViewModel

@Composable
fun CartScreen(
    foodViewModel: FoodViewModel = viewModel(),
    viewModel: CartViewModel = viewModel(),
    navController: NavController
) {
    val cartItems by viewModel.cartItemItems.collectAsState()
    val total by viewModel.total.collectAsState()
    val foodItems by foodViewModel.popularFoods.collectAsState()
    val foods by foodViewModel.foods.collectAsState()

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
            HeaderSection("Cart") {
                navController.navigate(BottomNavItem.Menu.route)
            }

            // Cart Items
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                cartItems.forEach { item ->
                    CartItemView(item, viewModel) {
                        navController.navigate("food_detail/${item.foodId}")
                    }
                }
            }

            // Popular Section
            Text(
                text = "Popular",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp)
            ) {
                items(foodItems) { item ->
                    FoodItem(item, onClick = {
                        navController.navigate("food_detail/${item.id}")
                    })
                }
            }
        }

        if (cartItems.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
            ) {
                SubtotalAndButton("Checkout", total, navController, "checkout")
            }
        }
    }
}

@Composable
fun CartItemView(item: CartItem, viewModel: CartViewModel, onClick: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var instructionText by remember { mutableStateOf(item.instructions) }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng không?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeFromCart(item.foodId)
                    showDialog = false
                }) {
                    Text("Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(12.dp))
 {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() }
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.image_error),
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Portion: ${item.portion}")
                Text("Drink: ${item.drink}")
                Text(
                    "${item.price}đ",
                    fontSize = 16.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút tăng giảm số lượng
                Row(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .height(28.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút Giảm
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                if (item.quantity > 1) viewModel.decreaseQuantity(item.foodId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", fontSize = 20.sp, color = Color.Gray)
                    }

                    // Số lượng
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${item.quantity}", fontSize = 18.sp, color = Color.Black)
                    }

                    // Nút Tăng
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                viewModel.increaseQuantity(item.foodId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontSize = 20.sp, color = Color.Gray)
                    }
                }
            }
        }

     Spacer(modifier = Modifier.width(16.dp))

        BasicTextField(
            value = instructionText,
            onValueChange = { instructionText = it },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (isFocused && !focusState.isFocused) {
                        // Khi mất focus, cập nhật instructions
                        viewModel.updateInstructions(item.foodId, instructionText)
                    }
                    isFocused = focusState.isFocused
                },
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Khi người dùng bấm Done trên bàn phím
                    viewModel.updateInstructions(item.foodId, instructionText)
                    focusManager.clearFocus()
                }
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Note,
                            contentDescription = "Note Icon",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp)
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            if (instructionText.isEmpty()) {
                                Text(
                                    "Instructions...",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            }
        )
    }
}



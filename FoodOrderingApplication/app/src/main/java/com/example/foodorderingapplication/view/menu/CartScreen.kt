package com.example.foodorderingapplication.view.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.viewmodel.CartViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.SubtotalAndButton
import com.example.foodorderingapplication.viewmodel.FoodViewModel

@Composable
fun CartScreen(foodViewModel: FoodViewModel = viewModel(), viewModel: CartViewModel = viewModel(), navController: NavController) {
    val cartItems by viewModel.cartItemItems.collectAsState()
    val total by viewModel.total.collectAsState()
    val foodItems by foodViewModel.foods.collectAsState()

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
            HeaderSection("Cart", navController)

            // Cart Items
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                cartItems.forEach { CartItemView(it) }
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
                    .padding(16.dp)
            ) {
                items(foodItems) { item ->
                    FoodItem(item, onClick = {
                        navController.navigate("detail/1")
                    })
                }
            }
        }

        // Subtotal & Button
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

@Composable
fun CartItemView(item: CartItem) {
    val context = LocalContext.current
    val imageId = remember(item.imageRes) {
        context.resources.getIdentifier(item.imageRes, "drawable", context.packageName)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Image(
            painter = painterResource(id = imageId),
            contentDescription = item.name,
            modifier = Modifier.size(84.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Column(

                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text("Price: ${item.price}")

                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { if (item.quantity > 1) item.quantity-- },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(
                                0xFFD9D9D9
                            )
                        ),
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(28.dp)
                    )
                    {
                        Text(
                            "-",
                            fontSize = 22.sp
                        )
                    }
                    Text(
                        "${item.quantity}", fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(
                        onClick = { item.quantity++ },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFD9D9D9)
                        ),
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(28.dp)
                    ) {
                        Text("+", fontSize = 22.sp)
                    }
                }

            }

            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
            }

        }
    }
}
package com.example.foodorderingapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodorderingapplication.models.PopularItem
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.models.CartItem

@Composable
fun CartScreen() {
    val cartItems = listOf(
        CartItem(R.drawable.hobakjuk, "Hokakjuk", 12.99, 1),
        CartItem(R.drawable.tteok, "Tteok", 10.99, 1)
    )

    val popularItems = listOf(
        PopularItem(R.drawable.tteok, "Tteok", 10.99, 4.8),
        PopularItem(R.drawable.gimbap, "Gimbap", 10.99, 4.8)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD700)) // Màu vàng
                .padding(top = 16.dp)
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Arrow back",
                )
            }

            Text(
                text = "Cart",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Cart Items
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth().padding(16.dp)
        ) {
            cartItems.forEach { CartItemView(it) }
        }
        // Popular Section
        Text(
            text = "Popular",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        LazyRow(    modifier = Modifier
            .fillMaxWidth().padding(16.dp)) {
            items(popularItems) { PopularItemView(it) }
        }

        Spacer(modifier = Modifier.weight(1f))
        // Subtotal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal", fontSize = 18.sp)
            Text("$23.98", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
        }

        // Checkout Button
        Button(
            onClick = { /* Xử lý thanh toán */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), // Màu vàng
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Checkout", fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
fun CartItemView(item: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Image(
            painter = painterResource(id = item.imageRes),
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
                            fontSize = 22.sp,

                            )
                    }
                    Text(
                        "${item.quantity}",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp),
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
                        Text(
                            "+",
                            fontSize = 22.sp,

                            )
                    }
                }

            }

            IconButton(onClick = { /* Tăng số lượng */ }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
            }

        }
    }
}

@Composable
fun PopularItemView(item: PopularItem) {
    Card(
        modifier = Modifier
            .padding(end = 14.dp)
            .width(150.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp, top = 6.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(6.dp).fillMaxWidth()
            ) {
                Text(
                    text = "$${item.price}",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                )

                Row {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color.Yellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(item.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    CartScreen()
}
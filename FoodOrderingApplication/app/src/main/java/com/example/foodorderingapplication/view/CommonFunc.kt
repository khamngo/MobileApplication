package com.example.foodorderingapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.models.Food

@Composable
fun HeaderSection(title: String, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFD700)) // Màu vàng
            .padding(top = 16.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Arrow back"
            )
        }

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SubtotalAndAddToCart(
    title: String, subtotal: Double,
    navController: NavController, pageName: String
) {
    // Subtotal & Add to Cart Button
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                "$${"%.2f".format(subtotal)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        }

        Button(
            onClick = { navController.navigate(pageName) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
        ) {
            Text(
                title,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FoodItem(food: Food, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = food.imageRes),
                contentDescription = food.name,
                modifier = Modifier
                    .fillMaxHeight(0.75f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = food.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp),
                fontSize = 14.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "$${food.price}",
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
                    Text(food.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun getFoodItems(): List<Food> = listOf(
    Food(
        "Gimbap",
        "Seaweed rice roll filled with a variety of delicious fillings.",
        8.99,
        4.5,
        R.drawable.tteok
    ),
    Food(
        "Bibimbap",
        "A bowl of white rice topped with vegetable, egg and sliced meat.",
        7.99,
        4.7,
        R.drawable.gimbap
    ),
    Food("Tteok", "Korean rice-caked.", 10.99, 4.3, R.drawable.hobakjuk)
)
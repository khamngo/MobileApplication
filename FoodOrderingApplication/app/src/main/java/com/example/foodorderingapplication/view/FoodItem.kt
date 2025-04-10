package com.example.foodorderingapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.models.Food


@Composable
fun FoodItem(food: Food, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageId = remember(food.imageRes) {
        context.resources.getIdentifier(food.imageRes, "drawable", context.packageName)
    }

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
                painter = painterResource(id = imageId),
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

//fun getFoodItems(): List<Food> = listOf(
//    Food(
//        "Gimbap",
//        "Seaweed rice roll filled with a variety of delicious fillings.",
//        8.99,
//        4.5,
//        R.drawable.tteok
//    ),
//    Food(
//        "Bibimbap",
//        "A bowl of white rice topped with vegetable, egg and sliced meat.",
//        7.99,
//        4.7,
//        R.drawable.gimbap
//    ),
//    Food("Tteok", "Korean rice-caked.", 10.99, 4.3, R.drawable.hobakjuk)
//)
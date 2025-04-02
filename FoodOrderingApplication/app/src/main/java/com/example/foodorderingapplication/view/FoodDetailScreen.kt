package com.example.foodorderingapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodorderingapplication.R

@Composable
fun FoodDetailScreen() {
    var selectedPortion by remember { mutableStateOf("8") }
    var quantity by remember { mutableIntStateOf(1) }
    var selectedDrink by remember { mutableStateOf("Fanta") }
    var instructions by remember { mutableStateOf("") }

    val portionPrices = mapOf("6" to 10.0, "8" to 12.0, "10" to 14.0)
    val drinkPrices = mapOf("Coca Cola" to 0.5, "Fanta" to 0.5, "Pepsi" to 0.0)

    val subtotal =
        (portionPrices[selectedPortion] ?: 0.0) * quantity + (drinkPrices[selectedDrink] ?: 0.0)

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF7F7F7))) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.bibimbap_owl),
                    contentDescription = "Food Image",
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .matchParentSize()
                    .align(Alignment.TopStart)
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Arrow back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "FavoriteBorder",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Text(
                text = "Bibimbap Bowl",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }

        // Portion Section
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Portion", "Required")
            portionPrices.forEach { (size, price) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedPortion == size,
                        onClick = { selectedPortion = size }
                    )
                    Text("$size\"", fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Text("$ $price", fontSize = 16.sp)
                }
            }

            // Quantity Section
            SectionTitle("Quantity", "")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(0.7.dp, Color.Gray, RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            )
            {
                IconButton(onClick = { if (quantity > 1) quantity-- }) {
                    Text(
                        "-",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF828282)
                    )
                }
                Text(
                    "$quantity",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFF828282),
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { quantity++ }) {
                    Text(
                        "+",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF828282)
                    )
                }
            }

            // Extra Drinks Section
            SectionTitle("Extra Drinks", "")
            drinkPrices.forEach { (drink, price) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedDrink == drink,
                        onClick = { selectedDrink = drink }
                    )
                    Text(drink, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Text(if (price == 0.0) "Free" else "$ $price", fontSize = 16.sp)
                }
            }

            // Instructions Section
            SectionTitle("Instructions", "")
            Text(
                "Let us know if you have specific thing in mind",
                color = Color(0xFF828282),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    modifier = Modifier.fillMaxWidth()
                )
                if (instructions.isEmpty()) {
                    Text(
                        "e.g. less spices, no mayo etc", color = Color.Gray, fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Subtotal & Add to Cart Button
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
                onClick = { /* Thêm vào giỏ hàng */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
            ) {
                Text(
                    "Add to cart",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (subtitle.isNotEmpty()) {
            Text(
                subtitle,
                fontSize = 16.sp,
                color = Color(0xFFFFCC00),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FoodDetailPreview() {
    FoodDetailScreen()
}

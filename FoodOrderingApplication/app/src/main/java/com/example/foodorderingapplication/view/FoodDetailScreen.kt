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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foodorderingapplication.R

@Composable
fun FoodDetailScreen(navController: NavHostController, foodId: Int) {
    var selectedPortion by remember { mutableStateOf("8") }
    var quantity by remember { mutableIntStateOf(1) }
    var selectedDrink by remember { mutableStateOf("Fanta") }
    var instructions by remember { mutableStateOf("") }

    val portionPrices = mapOf("6" to 10.0, "8" to 12.0, "10" to 14.0).toList()
    val drinkPrices = mapOf("Coca Cola" to 0.5, "Fanta" to 0.5, "Pepsi" to 0.0).toList()

    val subtotal =
        (portionPrices.find { it.first == selectedPortion }?.second ?: 0.0) * quantity +
                (drinkPrices.find { it.first == selectedDrink }?.second ?: 0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.bibimbap_owl),
                contentDescription = "Food Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .matchParentSize()
                    .align(Alignment.TopStart)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
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
            PortionSelection(portionPrices, selectedPortion) { selectedPortion = it }
            QuantitySelector(quantity) { quantity = it }
            ExtraDrinksSelection(drinkPrices, selectedDrink) { selectedDrink = it }
            InstructionsInput(instructions) { instructions = it }
        }

        // Subtotal & Add to Cart Button
        SubtotalAndAddToCart("Add to cart", subtotal = subtotal, navController = navController, "cart")
    }
}

@Composable
fun PortionSelection(
    portionPrices: List<Pair<String, Double>>,
    selectedPortion: String,
    onPortionSelected: (String) -> Unit
) {
    Column {
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
                    onClick = { onPortionSelected(size) }
                )
                Text("$size\"", fontSize = 16.sp, modifier = Modifier.weight(1f))
                Text("$ $price", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun QuantitySelector(quantity: Int, onQuantityChange: (Int) -> Unit) {
    Column {
        SectionTitle("Quantity", "")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(0.7.dp, Color.Gray, RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) {
                Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF828282))
            }
            Text(
                "$quantity",
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFF828282),
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onQuantityChange(quantity + 1) }) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF828282))
            }
        }
    }
}

@Composable
fun ExtraDrinksSelection(
    drinkPrices: List<Pair<String, Double>>,
    selectedDrink: String,
    onDrinkSelected: (String) -> Unit
) {
    Column {
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
                    onClick = { onDrinkSelected(drink) }
                )
                Text(drink, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Text(if (price == 0.0) "Free" else "$ $price", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun InstructionsInput(instructions: String, onInstructionsChange: (String) -> Unit) {
    Column {
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
                onValueChange = onInstructionsChange,
                modifier = Modifier.fillMaxWidth()
            )
            if (instructions.isEmpty()) {
                Text(
                    "e.g. less spices, no mayo etc", color = Color.Gray, fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Center)
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
                color = Color(0xFFFFD500),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

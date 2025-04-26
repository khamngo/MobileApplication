package com.example.foodorderingapplication.view.menu

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.view.SubtotalAndButton
import com.example.foodorderingapplication.viewmodel.CartViewModel
import com.example.foodorderingapplication.viewmodel.FoodDetailViewModel
import com.example.foodorderingapplication.viewmodel.FoodViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FoodDetailScreen(
    navController: NavHostController,
    foodId: String?,
    viewModel: FoodDetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val foodDetail by viewModel.foodDetail.collectAsState()

    val portionPrices = mapOf("6" to 10.0, "8" to 12.0, "10" to 14.0).toList()
    val drinkPrices = mapOf("Pepsi" to 0.0, "Coca Cola" to 0.5, "Fanta" to 0.5).toList()

    val selectedPortion = viewModel.selectedPortion.value
    val quantity = viewModel.quantity.intValue
    val selectedDrink = viewModel.selectedDrink.value
    val instructions = viewModel.instructions.value

    val subtotal =
        (portionPrices.find { it.first == selectedPortion }?.second ?: 0.0) * quantity +
                (drinkPrices.find { it.first == selectedDrink }?.second ?: 0.0)

    LaunchedEffect(foodId) {
        foodId?.let { viewModel.fetchFoodById(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        foodDetail?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 120.dp)
            ) {
                FoodHeaderSection(
                    imageUrl = it.imageUrl,
                    name = it.name,
                    navController = navController
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    PortionSelection(portionPrices, selectedPortion) { viewModel.updatePortion(it) }
                    QuantitySelector(quantity) { viewModel.updateQuantity(it) }
                    ExtraDrinksSelection(drinkPrices, selectedDrink) { viewModel.updateDrink(it) }
                    InstructionsInput(instructions) { viewModel.updateInstructions(it) }
                }
            }
        } ?: run {
            Text("Không tìm thấy món ăn!", modifier = Modifier.padding(16.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
        ) {
            SubtotalAndButton(
                title = "Add to cart",
                subtotal = subtotal,
                navController = navController,
                pageName = "cart",
                onButtonClick = {
                    foodDetail?.let {
                        cartViewModel.addToCart(
                            cartItem = CartItem(
                                foodId = it.id,
                                name = it.name,
                                imageUrl = it.imageUrl,
                                price = subtotal,
                                quantity = quantity,
                                portion = selectedPortion,
                                drink = selectedDrink,
                                instructions = instructions
                            )
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun FoodHeaderSection(
    imageUrl: String,
    name: String,
    navController: NavHostController
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp)),
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.image_error)
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
            IconButton(onClick = { /* TODO: handle favorite */ }) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "FavoriteBorder",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
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

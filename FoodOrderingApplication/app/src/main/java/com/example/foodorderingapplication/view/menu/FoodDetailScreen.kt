package com.example.foodorderingapplication.view.menu

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.CartItem
import com.example.foodorderingapplication.model.FoodItem
import com.example.foodorderingapplication.model.ReviewItem
import com.example.foodorderingapplication.view.SubtotalAndButton
import com.example.foodorderingapplication.view.admin.ReviewItem
import com.example.foodorderingapplication.viewmodel.CartViewModel
import com.example.foodorderingapplication.viewmodel.FavoriteViewModel
import com.example.foodorderingapplication.viewmodel.FoodDetailViewModel
import com.example.foodorderingapplication.viewmodel.ReviewDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.take

@Composable
fun FoodDetailScreen(
    navController: NavHostController,
    foodId: String,
    viewModel: FoodDetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    reviewViewModel: ReviewDetailViewModel = viewModel()
) {
    val foodDetail by viewModel.foodDetail.collectAsState()
    val portionPrices by viewModel.portionPrices.collectAsState()
    val drinkPrices by viewModel.drinkPrices.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val selectedPortion by viewModel.selectedPortion.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val selectedDrink by viewModel.selectedDrink.collectAsState()
    val instructions by viewModel.instructions.collectAsState()
    val reviews by reviewViewModel.reviews.collectAsState()

    LaunchedEffect(foodId) {
        foodId.let { viewModel.fetchFoodById(it) }
        reviewViewModel.fetchReviews(foodId)
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
                    foodItem = it,
                    imageUrl = it.imageUrl,
                    name = it.name,
                    navController = navController
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = it.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it.description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    PortionSelection(portionPrices, selectedPortion) { viewModel.updatePortion(it) }
                    QuantitySelector(quantity) { viewModel.updateQuantity(it) }
                    ExtraDrinksSelection(drinkPrices, selectedDrink) { viewModel.updateDrink(it) }
                    InstructionsInput(instructions) { viewModel.updateInstructions(it) }

                    // Thêm phần đánh giá
                    ReviewSection(
                        reviews = reviews,
                        onViewAllClick = { navController.navigate("review_all/${foodId}") }
                    )
                }
            }
        } ?: run {
            Text("No dish found!", modifier = Modifier.padding(16.dp))
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
                                price = when (selectedPortion) {
                                    "6" -> it.price
                                    "8" -> it.price + 2.0
                                    "10" -> it.price + 4.0
                                    else -> 0.0
                                },
                                subtotal = subtotal,
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
fun ReviewSection(
    reviews: List<ReviewItem>,
    onViewAllClick: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reviews",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hiển thị tối đa 2 đánh giá
        reviews.take(2).forEach { review ->
            ReviewItem(reviewItem = review)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ReviewItem(reviewItem: ReviewItem) {
    val formattedDate = SimpleDateFormat("h a : dd-MM-yyyy", Locale.getDefault())
        .format(reviewItem.date.toDate())

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(reviewItem.reviewer, fontWeight = FontWeight.SemiBold)

            Text(
                text = formattedDate,
                fontStyle = FontStyle.Italic,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = if (index < reviewItem.rating) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = reviewItem.reviewText,
            fontSize = 14.sp
        )
    }
}

@Composable
fun FoodHeaderSection(
    foodItem: FoodItem,
    imageUrl: String,
    name: String,
    navController: NavHostController,
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    val favoriteIds by favoriteViewModel.favoriteFoodIds.collectAsState()

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(240.dp)
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
            IconButton(onClick = { favoriteViewModel.toggleFavorite(foodItem) }) {
                Icon(
                    imageVector = if (favoriteIds.contains(foodItem.id)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "FavoriteBorder",
                    tint = Color.Red,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

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

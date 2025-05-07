package com.example.foodorderingapplication.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.model.FoodItem
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.FavoriteViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun FavoriteScreen(
    navController: NavHostController,
    viewModel: FavoriteViewModel = viewModel()
) {
    val favoriteItems by viewModel.favoriteItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF7F7F7))) {

        HeaderSection("My Favorites") {
            navController.popBackStack()
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (favoriteItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Không có món ăn yêu thích", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(favoriteItems) { item ->
                    FavoriteFoodItem(
                        foodItem = item,
                        onRemoveFavorite = {
                            viewModel.removeFavorite(item.id)
                        },
                        onClick = {
                            navController.navigate("food_detail/${item.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteFoodItem(
    foodItem: FoodItem,
    onRemoveFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)) {
            AsyncImage(
                model = foodItem.imageUrl,
                contentDescription = foodItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(foodItem.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(foodItem.description, maxLines = 2, fontSize = 12.sp, color = Color.Gray)
            }

            IconButton(onClick = onRemoveFavorite) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Remove Favorite",
                    tint = Color.Red
                )
            }
        }
    }
}

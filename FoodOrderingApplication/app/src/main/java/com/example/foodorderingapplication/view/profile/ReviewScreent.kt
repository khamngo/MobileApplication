package com.example.foodorderingapplication.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.model.FoodItem
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.ReviewViewModel

@Composable
fun ReviewScreen(
    navController: NavController,
    orderId: String,
    viewModel: ReviewViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.fetchOrderItems(orderId)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        HeaderSection("Review Order") {
            viewModel.resetState()
            navController.popBackStack()
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (state.foodItems.isEmpty()) {
                    Text(
                        text = "No items to review",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn {
                        items(state.foodItems) { foodItem ->
                            ProductRatingLayout(
                                foodItem = foodItem,
                                rating = state.ratings[foodItem.id] ?: 0,
                                onRatingChange = { viewModel.onRatingChange(foodItem.id, it) },
                                reviewText = state.reviewTexts[foodItem.id] ?: "",
                                onReviewChange = { viewModel.onReviewTextChange(foodItem.id, it) }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }

                    if (state.showError) {
                        Text(
                            text = "Vui lòng chọn số sao và nhập đánh giá cho ít nhất một món!",
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.submitReview(orderId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !state.isLoading
                    ) {
                        Text("Submit Review", fontSize = 16.sp, color = Color.White)
                    }

                    if (state.submitSuccess) {
                        Text(
                            text = "Cảm ơn bạn đã đánh giá!",
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductRatingLayout(
    foodItem: FoodItem,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    reviewText: String,
    onReviewChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Food Item Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = foodItem.imageUrl,
                contentDescription = foodItem.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    text = foodItem.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = foodItem.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rating label
        Text(
            text = "Rating",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating Stars
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star $i",
                    tint = if (i <= rating) Color(0xFFFFD700) else Color.LightGray,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onRatingChange(i) }
                        .padding(2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Write review label
        Text(
            text = "Write your review",
            fontSize = 15.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Review text input
        OutlinedTextField(
            value = reviewText,
            onValueChange = onReviewChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Share your experience...") }
        )
    }
}

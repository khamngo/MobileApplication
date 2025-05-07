package com.example.foodorderingapplication.view.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.model.FoodItem
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.EditReviewViewModel
import kotlinx.coroutines.delay

@Composable
fun EditReviewScreen(
    navController: NavController,
    reviewId: String,
    viewModel: EditReviewViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
val context = LocalContext.current
    LaunchedEffect(reviewId) {
        viewModel.fetchReview(reviewId)
    }


    LaunchedEffect(state.submitSuccess) {
        if (state.submitSuccess) {
            Toast.makeText(context, "Đánh giá đã được cập nhật!", Toast.LENGTH_SHORT).show()
            delay(500)

            // Truyền cờ refresh về màn trước
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("shouldRefresh", true)

            viewModel.resetState()
            navController.popBackStack()
        }
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        HeaderSection("Edit Review") {
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
                if (state.foodItem.id.isEmpty()) {
                    Text(
                        text = "Review not found",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    ProductRatingLayout(
                        foodItem = state.foodItem,
                        rating = state.rating,
                        onRatingChange = { viewModel.onRatingChange(it) },
                        reviewText = state.reviewText,
                        onReviewChange = { viewModel.onReviewTextChange(it) }
                    )

                    if (state.showError) {
                        Text(
                            text = "Vui lòng chọn số sao và nhập đánh giá!",
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            viewModel.updateReview(reviewId)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !state.isLoading
                    ) {
                        Text("Update Review", fontSize = 16.sp, color = Color.White)
                    }

//                    if (state.submitSuccess) {
//                        Text(
//                            text = "Đánh giá đã được cập nhật!",
//                            color = Color(0xFF4CAF50),
//                            modifier = Modifier.padding(top = 8.dp)
//                        )
//                    }
                }
            }
        }
    }
}

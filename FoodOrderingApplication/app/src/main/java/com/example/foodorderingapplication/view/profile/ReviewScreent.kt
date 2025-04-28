package com.example.foodorderingapplication.view.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.ReviewViewModel

@Composable
fun ReviewScreen(
    navController: NavController,
    viewModel: ReviewViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection("Bibimbap Bowl"){
            navController.popBackStack()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            HeaderReviewSection()

            Spacer(modifier = Modifier.height(12.dp))

            ProductRatingLayout(
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
                onClick = { viewModel.submitReview() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp)
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

@Composable
fun ProductRatingLayout(
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

        // 🔶 Rating label
        Text(
            text = "Rating",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ⭐ Rating Stars
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

        // 🔶 Write review label
        Text(
            text = "Write your review",
            fontSize = 15.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 📝 Review text input with rounded border
        OutlinedTextField(
            value = reviewText,
            onValueChange = onReviewChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Share your experience...") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview1() {
    NavigationGraph()
}
package com.example.foodorderingapplication.view.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.FoodDetailViewModel
import com.example.foodorderingapplication.viewmodel.ReviewDetailViewModel

@Composable
fun ReviewAllScreen(
    navController: NavHostController,
    foodId: String?,
    reviewsViewModel: ReviewDetailViewModel = viewModel()
) {
    val reviews by reviewsViewModel.reviews.collectAsState()

    LaunchedEffect(foodId) {
        foodId?.let { reviewsViewModel.fetchReviews(it) }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HeaderSection("All reviews") {
                navController.popBackStack()
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(reviews) { review ->
                    ReviewItem(reviewItem = review)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
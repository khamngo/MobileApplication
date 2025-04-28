package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.view.HeaderSection

@Composable
fun ReviewListScreen(navController: NavController) {
    val foodList = listOf(
        FoodReviewItem(
            imageUrl = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fdamndelicious.net%2F2019%2F04%2F21%2Fkorean-beef-bulgogi%2F&psig=AOvVaw1JXLg6RcJOEF0bQbIgcQZj&ust=1744340751413000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCLjMju-9zIwDFQAAAAAdAAAAABAE",
            title = "Bulgogi Beef",
            description = "Thinly sliced beef marinated in a sauce...",
            price = "$7.99",
            reviewCount = 5
        ),
        FoodReviewItem(
            imageUrl = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fdamndelicious.net%2F2019%2F04%2F21%2Fkorean-beef-bulgogi%2F&psig=AOvVaw1JXLg6RcJOEF0bQbIgcQZj&ust=1744340751413000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCLjMju-9zIwDFQAAAAAdAAAAABAEhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fdamndelicious.net%2F2019%2F04%2F21%2Fkorean-beef-bulgogi%2F&psig=AOvVaw1JXLg6RcJOEF0bQbIgcQZj&ust=1744340751413000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCLjMju-9zIwDFQAAAAAdAAAAABAEhttps://www.google.com/url?sa=i&url=https%3A%2F%2Fdamndelicious.net%2F2019%2F04%2F21%2Fkorean-beef-bulgogi%2F&psig=AOvVaw1JXLg6RcJOEF0bQbIgcQZj&ust=1744340751413000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCLjMju-9zIwDFQAAAAAdAAAAABAE",
            title = "Bibimbap",
            description = "A bowl of white rice topped with vegetable,...",
            price = "$7.99",
            reviewCount = 1
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        HeaderSection("Reviews"){
            navController.popBackStack()
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(foodList) { item ->
                FoodReviewItemCard(item){
                    navController.navigate("review/1")
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun FoodReviewItemCard(item: FoodReviewItem, onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(item.title, fontWeight = FontWeight.Bold)
            Text(item.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                text = item.price,
                color = Color.Red,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .background(Color(0xFFFFD700), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .align(Alignment.CenterVertically)
                .size(80.dp, 30.dp)
                .clickable {onClick }
        ) {
            Text(
                text = "${item.reviewCount} review${if (item.reviewCount > 1) "s" else ""}",
                color = Color.Red,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

data class FoodReviewItem(
    val imageUrl: String,
    val title: String,
    val description: String,
    val price: String,
    val reviewCount: Int
)


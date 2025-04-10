package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.view.HeaderSection
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.foodorderingapplication.models.Review



@Composable
fun ReviewDetailScreen(
    navController: NavController,
    reviewId: String,
    foodName: String = "Bibimbap Bowl",
    imageUrl: String = "bibimap",
    onReplyClick: (Review) -> Unit = {}
) {
    val reviews = listOf(
        Review("H******y", "03-03-2025", 4, "Bibimbap rất ngon!..."),
        Review("T*******n", "04-03-2025", 5, "Mình rất thích món này..."),
        Review("L****a", "05-03-2025", 3, "Ổn nhưng hơi cay.")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection("Review Detail", navController)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Image + Title
                AsyncImage(
                    model = imageUrl,
                    contentDescription = foodName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(foodName, fontWeight = FontWeight.Bold, fontSize = 20.sp)

                Spacer(modifier = Modifier.height(16.dp))
            }

            items(reviews) { review ->
                ReviewItem(review = review, onReplyClick = { onReplyClick(review) })
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review, onReplyClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(review.reviewer, fontWeight = FontWeight.SemiBold)
                Text("Review detail", fontSize = 14.sp)
            }
            Text(
                text = "11 AM : ${review.date}",
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
                    tint = if (index < review.rating) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = review.content,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onReplyClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Reply", color = Color.White)
        }
    }
}


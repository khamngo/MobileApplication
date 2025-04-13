package com.example.foodorderingapplication.view.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.model.ReviewItem
import com.example.foodorderingapplication.view.HeaderSection


@Composable
fun MyReviewScreen(
    navController: NavController,
) {
    val reviewItems = listOf(
        ReviewItem("H******y", "03-03-2025", 4, "Bibimbap rất ngon!..."),
        ReviewItem("T*******n", "04-03-2025", 5, "Mình rất thích món này..."),
        ReviewItem("L****a", "05-03-2025", 3, "Ổn nhưng hơi cay.")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection("My Reivews", navController)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(reviewItems) { review ->
                HeaderReviewSection()

                Spacer(modifier = Modifier.height(16.dp))

                ReviewItem(reviewItem = review)

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}

@Composable
fun HeaderReviewSection() {
    var foodName = "Bibimbap Bowl"
    var imageUrl = "bibimap"
    var description =
        "Bibimbap là món cơm trộn Hàn Quốc đầy màu sắc, kết hợp cơm nóng, rau củ, thịt, trứng và sốt gochujang đậm đà"

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
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

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(foodName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Row {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "4.5",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(description, fontSize = 16.sp)
    }
}

@Composable
fun ReviewItem(reviewItem: ReviewItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("My Ratings", fontSize = 16.sp)

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

        Spacer(modifier = Modifier.height(16.dp))

        ReviewCard()

    }
}

@Composable
fun ReviewCard(
    title: String = "My review",
    content: String = "Bibimbap rất ngon! Cơm dẻo, rau tươi, thịt bò đậm đà, sốt gochujang cay vừa phải. Món ăn được đóng gói cẩn thận, giao nhanh và còn nóng. Chỉ tiếc là trứng hơi chín quá, mình thích lòng đào hơn. Nhưng nhìn chung rất hài lòng, sẽ đặt lại!"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 🔶 Tiêu đề “My review”
            Text(
                text = title,
                color = Color(0xFFF1C40F), // Màu vàng
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🔶 Nội dung review
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Bibimbap rất ngon! ")
                    }
                    append("Cơm dẻo, rau tươi, thịt bò đậm đà, sốt gochujang cay vừa phải. Món ăn được đóng gói cẩn thận, giao nhanh và còn nóng. Chỉ tiếc là trứng hơi chín quá, mình thích lòng đào hơn. Nhưng nhìn chung rất hài lòng, sẽ đặt lại!")
                },
                color = Color.Black,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}

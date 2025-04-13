package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.view.HeaderSection

@Composable
fun AddFoodScreen(navController: NavController){
    var foodName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var promos by remember { mutableStateOf("") }
    var promosCode by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Popular") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            HeaderSection("Add Food", navController)

            CustomTextField(
                value = foodName, onValueChange = { foodName = it }, label = "Food Name"
            )
            CustomTextField(
                value = description, onValueChange = { description = it }, label = "Description"
            )
            CustomTextField(value = price, onValueChange = { price = it }, label = "Price")
            CustomTextField(value = promos, onValueChange = { promos = it }, label = "Promos")
            CustomTextField(
                value = promosCode, onValueChange = { promosCode = it }, label = "Promos Code"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    CustomTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = "Link image",
                    )

                    IconButton(onClick = {}, modifier = Modifier.align(Alignment.TopEnd)) {
                        Icon(
                            Icons.Default.AddCircleOutline,
                            contentDescription = "Add Image",
                            Modifier.size(32.dp)
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.hobakjuk),
                    contentDescription = "Sample Image",
                    modifier = Modifier
                        .size(240.dp, 180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // ⬇️ Hiển thị ảnh nếu có URL
//            if (imageUrl.isNotBlank()) {
//                AsyncImage(
//                    model = imageUrl,
//                    contentDescription = "Food Image",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .size(240.dp, 180.dp)
//                        .clip(RoundedCornerShape(12.dp))
//                )
//            }

            Column (  horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()       .padding(16.dp)){
                SelectTagDropdown(
                    selectedOption = selectedTag,
                    onOptionSelected = { selectedTag = it }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White) .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Button(
                onClick = {
                    // TODO: Lưu thông tin vào viewModel
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Edit", fontSize = 16.sp, color = Color.White)
            }
        }

    }
}
@Preview(showBackground = true)
@Composable
fun Preview3() {
    NavigationGraph()
}


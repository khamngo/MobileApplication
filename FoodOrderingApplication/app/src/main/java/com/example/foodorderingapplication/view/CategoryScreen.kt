package com.example.foodorderingapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.models.Food
import com.example.foodorderingapplication.ui.theme.MograFont
import com.example.foodorderingapplication.viewmodel.FoodViewModel

@Composable
fun CategoryScreen(viewModel: FoodViewModel = viewModel(), navController: NavController, name: String) {
    var selectedTabIndex by remember { mutableIntStateOf(if (name == "popular") 0 else 1) }
    val foods by viewModel.foods.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD700)) // Màu vàng
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Arrow back",
                        tint = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_title),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(0.dp)
                    )
                    Text(
                        text = "KFoods",
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        fontFamily = MograFont,
                        color = Color.White
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                indicator = { tabPositions ->
                    // Vẽ đường line vàng ngắt quãng
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(1.5.dp)
                            .background(Color(0xFFFFD700)),
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },

                    ) {
                    Text(
                        "Popular",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (selectedTabIndex == 0) Color(0xFFFFD700) else Color.Black
                    )
                }
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                ) {
                    Text(
                        "Deals",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (selectedTabIndex == 1) Color(0xFFFFD700) else Color.Black
                    )
                }
            }
        }

        LazyColumn(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (selectedTabIndex == 0) {
                item { Text("Popular", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                items(foods) { food ->
                    FoodItems(food, onClick = { navController.navigate("detail/${food.id}") })
                }
            } else {
                item { Text("Deal", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                items(foods) { food ->
                    FoodItems(food, onClick = { navController.navigate("detail/${food.id}") })
                }
            }
        }
    }
}

@Composable
fun FoodItems(food: Food, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageId = remember(food.imageRes) {
        context.resources.getIdentifier(food.imageRes, "drawable", context.packageName)
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
       .clickable{onClick()}
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "Image",
            modifier = Modifier.size(80.dp), contentScale = ContentScale.Crop
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
               .fillMaxWidth()
        ) {
            Text(food.name, fontWeight = FontWeight.Bold)
            Text(food.description, fontSize = 14.sp, color = Color.Gray)
            Text("$${food.price}", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    NavigationGraph()
}
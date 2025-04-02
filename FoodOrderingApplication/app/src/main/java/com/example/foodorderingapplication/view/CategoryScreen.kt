package com.example.foodorderingapplication.view

import android.R.attr.text
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.models.Food
import com.example.foodorderingapplication.ui.theme.MograFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

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
            Box(
                modifier = Modifier.fillMaxWidth(),

            ) {
                IconButton(onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)) {
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

        Row( modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                indicator = { tabPositions ->
                    // Vẽ đường line vàng ngắt quãng
                    TabRowDefaults.Indicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(1.5.dp) // Độ dày của line
                            .background(Color(0xFFFFD700)) // Màu vàng
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
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                Text("Popular", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            items(getPopularItems()) { food ->
                FoodItem2(food)
            }
            item {
                Text("Deals", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            items(getDealsItems()) { food ->
                FoodItem2(food)
            }
        }
    }
}

@Composable
fun FoodItem2(food: Food) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = food.imageRes),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(food.name, fontWeight = FontWeight.Bold)
            Text(food.description, fontSize = 14.sp, color = Color.Gray)
            Text("$${food.price}", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

fun getPopularItems() = listOf(
    Food(
        "Gimbap",
        "Seaweed rice roll filled with a variety of delicious fillings.",
        8.99,
        4.5,
        R.drawable.gimbap
    ),
    Food(
        "Bibimbap",
        "A bowl of white rice topped with vegetable, egg and sliced meat.",
        7.99,
        4.7,
        R.drawable.bibimbap_owl
    ),
    Food("Tteok", "Korean rice-caked.", 10.99, 4.3, R.drawable.tteok)
)

fun getDealsItems() = listOf(
    Food("Hobakjuk", "Pumpkin-porridge.", 12.99, 4.6, R.drawable.hobakjuk),
    Food(
        "Bulgogi Beef",
        "Thinly sliced beef marinated in a mix of soy sauce.",
        7.99,
        4.8,
        R.drawable.korean_bulgogi_beef
    ),
    Food(
        "Kongguksu",
        "A seasonal Korean noodle dish served in a cold soy milk broth.",
        3.99,
        4.1,
        R.drawable.kongguksu
    )
)

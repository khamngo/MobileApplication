package com.example.foodorderingapplication.view.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.FoodItem
import com.example.foodorderingapplication.ui.theme.MograFont
import com.example.foodorderingapplication.view.BottomNavBar
import com.example.foodorderingapplication.viewmodel.CartViewModel
import com.example.foodorderingapplication.viewmodel.FoodViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MenuScreen(navController: NavController) {
    Scaffold(bottomBar = { BottomNavBar(navController) }, content = { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F7F7))

            ) {
                item { TopBar() }
                item { BannerSlider() }
                item { CategorySection(navController) }
                item { FoodListSection(navController = navController) }

            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            ) {
                DraggableCartIcon(navController = navController)
            }
        }
    }
    )
}

@Composable
fun TopBar() {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFD700)) // Màu vàng
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
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

        SearchBar()
    }
}

@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }
    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        placeholder = { Text(text = "Search", color = Color.Gray, fontSize = 18.sp) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun BannerSlider() {
    val bannerImages = listOf(
        R.drawable.banner_image,
        R.drawable.banner_image,
        R.drawable.banner_image,
        R.drawable.banner_image,
        R.drawable.banner_image
    )

    val bannerTitles = listOf(
        "Hot Deals 🔥",
        "Fresh Food 🍏",
        "Fast Delivery 🚀",
        "Best Offers 🎉",
        "New Menu 🍽"
    )

    val pagerState = rememberPagerState(initialPage = 0) {
        bannerImages.size
    }
    val coroutineScope = rememberCoroutineScope()

    // Tự động chuyển slide mỗi 5 giây
    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000) // Chờ 5 giây
            coroutineScope.launch {
                val nextPage = (pagerState.currentPage + 1) % bannerImages.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        ) { page ->
            Image(
                painter = painterResource(id = bannerImages[page]),
                contentDescription = "Banner ${page + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }

        // Text bên trái giữa banner
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 18.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                text = bannerTitles[pagerState.currentPage],
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Indicator ở dưới banner
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            bannerImages.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (pagerState.currentPage == index) Color.Yellow else Color.Gray,
                            shape = CircleShape
                        )
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun CategorySection(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        CategoryItem("Popular", Icons.Default.Star, navController, "popular")
        Spacer(modifier = Modifier.size(16.dp))
        CategoryItem("Deal", Icons.Outlined.LocalOffer, navController, "deal")
    }
}

@Composable
fun CategoryItem(text: String, icon: ImageVector, navController: NavController, name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD700))
                .clickable { navController.navigate("category/${name}") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun FoodListSection(
    viewModel: FoodViewModel = viewModel(),
    navController: NavController,
) {
    val foods by viewModel.foods.collectAsState()

    Column(modifier = Modifier.padding(16.dp))
    {
        Text("Best Seller", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(foods) { food ->
                FoodItem(food, onClick = {
                    navController.navigate("detail/${food.id}")
                })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Explore More", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            foods.forEach { food ->
                LargeFoodItem(food, onClick = {
                    navController.navigate("detail/${food.id}")
                })
            }
        }
    }
}

@Composable
fun FoodItem(foodItem: FoodItem, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageId = remember(foodItem.imageRes) {
        context.resources.getIdentifier(foodItem.imageRes, "drawable", context.packageName)
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = foodItem.name,
                modifier = Modifier
                    .fillMaxHeight(0.75f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = foodItem.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp),
                fontSize = 14.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "$${foodItem.price}",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                )

                Row {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color.Yellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(foodItem.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LargeFoodItem(foodItem: FoodItem, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageId = remember(foodItem.imageRes) {
        context.resources.getIdentifier(foodItem.imageRes, "drawable", context.packageName)
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = foodItem.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClick() },
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = "$${foodItem.price}",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Tên món ăn & Đánh giá
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = foodItem.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = foodItem.rating.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }

}

@Composable
fun DraggableCartIcon(viewModel: CartViewModel = viewModel(), navController: NavController) {
    val cartItems by viewModel.cartItemItems.collectAsState()

    val density = LocalDensity.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        offsetX = with(density) { -10.dp.toPx() }
        offsetY = with(density) { -10.dp.toPx() }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD700))
                .align(Alignment.Center)
                .clickable { navController.navigate("cart") }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_shopping_cart),
                contentDescription = "Cart Icon",
                modifier = Modifier
                    .size(35.dp),
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${cartItems.size}",
                color = Color.White,
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NavigationGraph()
}
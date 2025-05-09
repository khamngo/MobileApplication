package com.example.foodorderingapplication.view.admin

import android.R.attr.onClick
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.FoodItem
import com.example.foodorderingapplication.view.BottomNavBar
import com.example.foodorderingapplication.model.BottomNavItem
import com.example.foodorderingapplication.view.menu.FoodItems
import com.example.foodorderingapplication.view.menu.TopBar
import com.example.foodorderingapplication.viewmodel.FoodViewModel
import kotlin.math.roundToInt

@Composable
fun HomeAdminScreen(navController: NavController, viewModel: FoodViewModel = viewModel()) {
    val foodList by viewModel.exploreFoods.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFoodItemToDelete by remember { mutableStateOf<FoodItem?>(null) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController, bottomNavItems) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                item { TopBar() }
                if (searchResults.isNotEmpty()) {
                    items(searchResults) { food ->
                        FoodCard(
                            foodItem = food,
                            onEdit = { navController.navigate("edit_food/${food.id}") },
                            onDelete = {
                                selectedFoodItemToDelete = food
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }

            DraggableAddIcon(navController = navController)

            if (showDeleteDialog && selectedFoodItemToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Confirmation") },
                    text = { Text("Are you sure you want to delete \"${selectedFoodItemToDelete?.name}\"?") },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedFoodItemToDelete?.let { food ->
                                viewModel.deleteFood(food.id)
                            }
                            showDeleteDialog = false
                        }) {
                            Text("Delete", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }

}

@SuppressLint("DefaultLocale")
@Composable
fun FoodCard(foodItem: FoodItem, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = foodItem.imageUrl,
                contentDescription = foodItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.image_error)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = foodItem.name, fontWeight = FontWeight.Bold)
                Text(
                    text = "$${String.format("%.2f", foodItem.price)}",
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = "Category: ${foodItem.tags}")
                Text(text = "Description: ${foodItem.description}")
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFFFFD700))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun DraggableAddIcon(navController: NavController) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val iconSize = 70.dp

    LaunchedEffect(Unit) {
        offsetX = with(density) { (screenWidth - iconSize - 10.dp).toPx() }
        offsetY =
            with(density) { (screenHeight - iconSize - 80.dp).toPx() } // chừa space cho bottom bar nếu có
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
                .clickable { navController.navigate("add_food") }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val iconPxSize = with(density) { iconSize.toPx() }

                        val screenWidthPx = with(density) { screenWidth.toPx() }
                        val screenHeightPx = with(density) { screenHeight.toPx() }
                        val bottomBarHeightPx = with(density) { 80.dp.toPx() }

                        val maxX = screenWidthPx - iconPxSize
                        val maxY = screenHeightPx - iconPxSize - bottomBarHeightPx

                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, maxX)
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, maxY)
                    }

                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircleOutline,
                contentDescription = "Cart Icon",
                modifier = Modifier
                    .size(35.dp),
                tint = Color.White
            )
        }
    }
}
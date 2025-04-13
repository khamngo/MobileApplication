package com.example.foodorderingapplication.view.notification

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.model.NotificationItem
import com.example.foodorderingapplication.viewmodel.NotificationViewModel
import androidx.compose.runtime.getValue
import com.example.foodorderingapplication.view.BottomNavBar

@Composable
fun NotificationScreen(navController: NavController, viewModel: NotificationViewModel = viewModel()) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "Notifications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                LazyColumn {
                    items(notifications) { item ->
                        NotificationRow(
                            item = item,
                            onClick = {
                                if (!it.isRead) {
                                    viewModel.markAsRead(it.id)
                                }
                            }
                        )
                        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    }
                }
            }
        }
    )
}

@Composable
fun NotificationRow(
    item: NotificationItem,
    onClick: (NotificationItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(item) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(color = item.dotColor)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = item.message,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = if (item.isRead) Color.Gray else Color.Black
        )

        Text(
            text = item.time,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting2Preview() {
    NavigationGraph()
}

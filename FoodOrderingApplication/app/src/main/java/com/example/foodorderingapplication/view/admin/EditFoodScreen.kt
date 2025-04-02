package com.example.foodorderingapplication.view.admin

import android.R.attr.password
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.view.AddShippingScreen

@Composable
fun EditFoodScreen(){
    var foodName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var promos by remember { mutableStateOf("") }
    var promosCode by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD700)) // Màu vàng
                .padding(top = 16.dp)
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Arrow back",
                )
            }

            Text(
                text = "Edit Food",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomTextField(
                value = foodName,
                onValueChange = { foodName = it },
                label = "Food Name"
            )

            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description"
            )

            CustomTextField(
                value = price,
                onValueChange = { price = it },
                label = "Price"
            )

            CustomTextField(
                value = promos,
                onValueChange = { promos = it },
                label = "Promos"
            )

            CustomTextField(
                value = promosCode,
                onValueChange = { promosCode = it },
                label = "Promos Code"
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* Xử lý thanh toán */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), // Màu vàng
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Edit", fontSize = 16.sp, color = Color.White)
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun EditFoodPreview() {
    EditFoodScreen()
}
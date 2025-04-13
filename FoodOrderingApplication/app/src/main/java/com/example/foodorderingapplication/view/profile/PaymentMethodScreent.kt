package com.example.foodorderingapplication.view.profile

import android.R.attr.password
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.view.admin.CustomTextField

@Composable
fun PaymentMethodScreen(navController: NavController) {
    var cardName by remember { mutableStateOf("Ngo Minh Kham") }
    var cardNumber by remember { mutableStateOf("1234 3574 2468 3468") }
    var expiryDate by remember { mutableStateOf("12/2025") }
    var cvc by remember { mutableStateOf("456") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        HeaderSection("Payment Mehthod", navController)

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            BankCardItem(
                bankName = "Vietcombank",
                cardHolder = "Ngo Minh Kham",
                cardNumberLast4 = "3468",
                cardType = "VISA",
                expiryDate = "12/28"
            )

            CustomTextField(
                value = cardName,
                onValueChange = { cardName = it },
                label = "Card Name"
            )

            CustomTextField(
                value = cardNumber,
                onValueChange = { cardNumber = it },
                label = "Card Number"
            )

            CustomTextField(
                value = expiryDate,
                onValueChange = { expiryDate = it },
                label = "Expiry Date"
            )

            CustomTextField(
                value = cvc,
                onValueChange = { cvc = it },
                label = "CVC"
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* Xử lý thanh toán */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), // Màu vàng
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Update", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun BankCardItem(
    bankName: String,
    cardHolder: String,
    cardNumberLast4: String,
    cardType: String,
    expiryDate: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF6A85B6), Color(0xFFFFE29F)),
                        start = Offset(0f, 0f),
                        end = Offset(500f, 500f)
                    )
                )
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // 🔶 Tên ngân hàng
                Text(
                    text = bankName.uppercase(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 🔶 Chip icon + số cuối thẻ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
//                        painter = painterResource(id = R.drawable.ic_chip),
                        contentDescription = "Chip Icon",
                        tint = Color.Yellow,
                        modifier = Modifier.size(36.dp)
                    )

                    Text(
                        text = "**** $cardNumberLast4",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 🔶 Tên chủ thẻ + ngày hết hạn + loại thẻ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Card Holder",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = cardHolder.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Expiry",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = expiryDate,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 🔶 Loại thẻ (Visa/Mastercard)
                Text(
                    text = cardType.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}


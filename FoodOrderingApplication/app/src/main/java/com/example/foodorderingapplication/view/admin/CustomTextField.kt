package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.math.LinearTransformation.vertical

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = label,
//            fontSize = 18.sp,
//            color = Color.Black,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )

    OutlinedTextField(
        value = value,
        label = { Text(text = label) },
        placeholder = {
            Text(label)
        }, singleLine = true,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Yellow,  // Background color when focused
            unfocusedContainerColor = Color.White, // Background color when not focused
            focusedIndicatorColor = Color.White, // Outline color when focused
            unfocusedIndicatorColor = Color.Gray // Outline color when not focused

        )
    )
//    }
}


package com.example.foodorderingapplication.view.home

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodorderingapplication.viewmodel.AuthViewModel


@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val username by authViewModel.username.collectAsState()
    val email by authViewModel.email.collectAsState()
    val phone by authViewModel.phone.collectAsState()
    val password by authViewModel.password.collectAsState()
    val confirmPassword by authViewModel.confirmPassword.collectAsState()

    val signUpSuccess by authViewModel.signUpSuccess.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    // Xử lý điều hướng khi đăng ký thành công
    LaunchedEffect(signUpSuccess) {
        if (signUpSuccess) {
            onSignInClick() // Điều hướng về SignInScreen
            authViewModel.resetSignUpState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Back arrow + "Sign Up"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onBackClick() }
                .padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Up", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Create Your Account", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        CustomOutlinedTextField(
            value = username,
            onValueChange = authViewModel::onUsernameChange,
            label = "Username",
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage.contains("Username")
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomOutlinedTextField(
            value = email,
            onValueChange = authViewModel::onEmailChange,
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Email,
            isError = errorMessage.contains("Email")
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomOutlinedTextField(
            value = phone,
            onValueChange = authViewModel::onPhoneChange,
            label = "Phone",
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Phone,
            isError = errorMessage.contains("Phone")
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomOutlinedTextField(
            value = password,
            onValueChange = authViewModel::onPasswordChange,
            label = "Password",
            modifier = Modifier.fillMaxWidth(),
            isPassword = true,
            isError = errorMessage.contains("Password")
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomOutlinedTextField(
            value = confirmPassword,
            onValueChange = authViewModel::onConfirmPasswordChange,
            label = "Confirm Password",
            modifier = Modifier.fillMaxWidth(),
            isPassword = true,
            isError = errorMessage.contains("Confirm Password")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị lỗi
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Text(
            buildAnnotatedString {
                append("By clicking continue, you agree to our ")
                withStyle(style = SpanStyle(color = Color(0xFFFFC107), fontWeight = FontWeight.SemiBold)) {
                    append("Terms of Service")
                }
                append(" and ")
                withStyle(style = SpanStyle(color = Color(0xFFFFC107), fontWeight = FontWeight.SemiBold)) {
                    append("Privacy Policy")
                }
            },
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                authViewModel.signUpWithEmailAndPassword(
                    username = username,
                    email = email,
                    phone = phone,
                    password = password,
                    confirmPassword = confirmPassword
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Sign Up", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val annotatedText = buildAnnotatedString {
                append("Already have an account? ")
                pushStringAnnotation(tag = "SIGN_IN", annotation = "sign_in")
                withStyle(style = SpanStyle(color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)) {
                    append("Sign In")
                }
                pop()
            }

            ClickableText(
                text = annotatedText,
                onClick = { offset ->
                    annotatedText.getStringAnnotations("SIGN_IN", offset, offset)
                        .firstOrNull()?.let {
                            onSignInClick()
                        }
                },
                style = TextStyle(fontSize = 14.sp, color = Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        isError = isError
    )
}
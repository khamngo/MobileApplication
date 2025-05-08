package com.example.foodorderingapplication.view.home

import android.R.attr.password
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.AdminActivity
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.BottomNavItem
import com.example.foodorderingapplication.auth.createGoogleSignInClient
import com.example.foodorderingapplication.ui.theme.MograFont
import com.example.foodorderingapplication.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun SignInScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = context as? Activity
    val signInSuccess by authViewModel.signInSuccess.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val isLoading by authViewModel.isLoading.collectAsState()

    // Google Sign-In
    val googleSignInClient = remember { createGoogleSignInClient(context) }

    val googleSignInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            authViewModel.signInWithGoogle(account.idToken ?: throw Exception("No ID token"))
        } catch (e: ApiException) {
            authViewModel.resetSignInState()
            Toast.makeText(context, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(signInSuccess, userRole) {
        if (signInSuccess) {
            when (userRole) {
                "admin" -> {
                    val intent = Intent(context, AdminActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                }
                "user" -> {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    // Hiển thị lỗi
    if (errorMessage.isNotEmpty()) {
        LaunchedEffect(errorMessage) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // UI
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F7F7))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_logo),
                    contentDescription = "Background Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_title),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(0.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "KFoods",
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                         fontFamily = MograFont,
                        color = Color(0xFFFA1B31)
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Sign in", fontSize = 36.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = email,
                        onValueChange = authViewModel::onEmailChange,
                        label = { Text("Username or Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = authViewModel::onPasswordChange,
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu")
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    )

                    Button(
                        onClick = {
                            authViewModel.signInWithEmailAndPassword{
                                navController.navigate(BottomNavItem.Home.route)
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = White,
                                modifier = Modifier.size(24.dp)
                            )
                        }else {
                            Text(
                                "Sign In",
                                fontSize = 20.sp,
                                color = White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GoogleSignInButton {
                        activity?.let {
                            googleSignInClient.signOut().addOnCompleteListener {
                                val signInIntent = googleSignInClient.signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpPrompt(
                        onSignUpClick = {
                            navController.navigate("sign_up")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleSignInButton(onGoogleSignIn: () -> Unit) {
    Button(
        onClick = { onGoogleSignIn() },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5EDFF)),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google Icon",
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "SIGN IN WITH GOOGLE",
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SignUpPrompt(
    onSignUpClick: () -> Unit
) {
    val annotatedText = buildAnnotatedString {
        append("Don’t have an account? ")

        // Thêm annotation để xác định vùng click
        pushStringAnnotation(tag = "SIGN_UP", annotation = "sign_up")
        withStyle(
            style = SpanStyle(
                color = Color(0xFFFFC107), // Màu vàng như hình
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Sign Up")
        }
        pop()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "SIGN_UP", start = offset, end = offset)
                    .firstOrNull()?.let {
                        onSignUpClick()
                    }
            },
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            )
        )
    }
}



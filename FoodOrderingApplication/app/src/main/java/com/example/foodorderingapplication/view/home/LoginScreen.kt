package com.example.foodorderingapplication.view.home

import android.R.attr.fontWeight
import android.app.Activity
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderingapplication.NavigationGraph
import com.example.foodorderingapplication.R
import com.example.foodorderingapplication.model.BottomNavItem
import com.example.foodorderingapplication.auth.createGoogleSignInClient
import com.example.foodorderingapplication.ui.theme.MograFont
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.common.io.Files.append
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity

    val auth = FirebaseAuth.getInstance()
    val googleSignInClient = remember { createGoogleSignInClient(context) }

    val googleSignInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navController.navigate(BottomNavItem.Home.route)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    // Sử dụng rememberSaveable để giữ trạng thái khi xoay màn hình
    val usernameState = rememberSaveable { mutableStateOf("") }
    val passwordState = rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var loginStatus by rememberSaveable { mutableStateOf<String?>(null) } // Thêm trạng thái cho thông báo đăng nhập

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues).background(Color(0xFFF7F7F7))
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
                    contentScale = ContentScale.Crop,
                )

                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
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
                    Text("Sign in", fontSize = 36.sp, fontWeight = FontWeight.Bold )
                    // Ô nhập username
                    OutlinedTextField(
                        value = usernameState.value,
                        onValueChange = { usernameState.value = it },
                        label = { Text("Username or Email ") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp),
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedBorderColor = White,
//                            unfocusedBorderColor = White,
//                            cursorColor = White,
//                            focusedLabelColor = White,
//                            unfocusedLabelColor = White,
//                            focusedTextColor = White,
//                            unfocusedTextColor = White
//                        )
                    )

                    // Ô nhập password
                    OutlinedTextField(
                        value = passwordState.value,
                        onValueChange = { passwordState.value = it },
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff

                            val description =
                                if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = description,
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedBorderColor = White,
//                            unfocusedBorderColor = White,
//                            cursorColor = White,
//                            focusedLabelColor = White,
//                            unfocusedLabelColor = White,
//                            focusedTextColor = White,
//                            unfocusedTextColor = White
//                        )
                    )

                    Button(
                        onClick = { navController.navigate("") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
                    ) {
                        Text(
                            "Sign In",
                            fontSize = 20.sp,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
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
                            navController.navigate("sign_up_screen")
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
                        onSignUpClick() // Điều hướng khi người dùng click vào "Sign Up"
                    }
            },
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            )
        )
    }
}



package com.example.foodorderingapplication.view.admin

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.EditFoodViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditFoodScreen(navController: NavController, foodId: String) {
    val viewModel: EditFoodViewModel = viewModel()
    val context = LocalContext.current

    // Launcher để chọn ảnh từ thư viện
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadImageToFirebase(it) }
    }

    // Uri tạm để lưu ảnh chụp
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    LaunchedEffect(Unit) {
        photoUri = createImageFile(context)
    }

    // Launcher để chụp ảnh
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { viewModel.uploadImageToFirebase(it) }
        }
    }

    // Load data when screen initializes
    LaunchedEffect(foodId) {
        viewModel.loadFoodData(foodId)
    }
    // Collect state directly from ViewModel
    val foodName by viewModel.foodName.collectAsState()
    val description by viewModel.description.collectAsState()
    val price by viewModel.price.collectAsState()
    val imageUrl by viewModel.imageUrl.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            HeaderSection("Edit Food") {
                navController.popBackStack()
            }

            CustomTextField(
                value = foodName,
                onValueChange = { viewModel.updateField("foodName", it) },
                label = "Food Name"
            )
            CustomTextField(
                value = description,
                onValueChange = { viewModel.updateField("description", it) },
                label = "Description"
            )
            CustomTextField(
                value = price,
                onValueChange = { viewModel.updateField("price", it) },
                label = "Price"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    CustomTextField(
                        value = imageUrl,
                        onValueChange = { viewModel.updateField("imageUrl", it) },
                        label = "Link image",
                    )

                    Row(modifier = Modifier.align(Alignment.TopEnd)) {
                        IconButton(
                            onClick = { pickImageLauncher.launch("image/*") },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.AddCircleOutline,
                                contentDescription = "Pick Image",
                                Modifier.size(32.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                photoUri?.let { uri ->
                                    takePictureLauncher.launch(uri)
                                }
                            },
                            enabled = photoUri != null
                        )  {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Take Photo",
                                Modifier.size(32.dp)
                            )
                        }
                    }
                }

                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Food Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(240.dp, 180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Select Tags", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                MultiSelectTags(
                    selectedTags = tags,
                    onTagsChanged = { viewModel.updateTags(it) }
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.updateFood(foodId)
                    if (viewModel.errorMessage.value.isEmpty()) {
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Edit", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

// Hàm tạo file tạm để lưu ảnh chụp
private fun createImageFile(context: Context): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = context.externalCacheDir
    val file = File.createTempFile(imageFileName, ".jpg", storageDir)
    return Uri.fromFile(file)
}

@Composable
fun MultiSelectTags(
    selectedTags: List<String>,
    onTagsChanged: (List<String>) -> Unit
) {
    val availableTags = listOf("Popular", "Deal", "Bestseller", "Explore")
    var selected by remember { mutableStateOf(selectedTags.toSet()) }

    LaunchedEffect(selected) {
        onTagsChanged(selected.toList())
    }

    Column {
        availableTags.forEach { tag ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selected.contains(tag),
                    onCheckedChange = { isChecked ->
                        selected = if (isChecked) selected + tag else selected - tag
                    }
                )
                Text(
                    text = tag,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}


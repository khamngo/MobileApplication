package com.example.foodorderingapplication.view.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodorderingapplication.model.UserItem
import com.example.foodorderingapplication.view.HeaderSection
import com.example.foodorderingapplication.viewmodel.UserViewModel
import androidx.compose.material3.AlertDialog

@Composable
fun UserManagementScreen(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val userList by userViewModel.userList.collectAsState()
    var editingUser by remember { mutableStateOf<UserItem?>(null) }

    Scaffold(
    ) { padding ->
        HeaderSection("User Management") {
            navController.popBackStack()
        }

        LazyColumn(modifier = Modifier.padding(padding)) {
            items(userList) { user ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Username: ${user.username}")
                        Text("Email: ${user.email}")
                        Text("Role: ${user.role}")
                        Row {
                            Spacer(modifier = Modifier.weight(1f))
                            Button(onClick = { editingUser = user }) {
                                Text("Edit")
                            }
                        }
                    }
                }
            }
        }
    }

    editingUser?.let { user ->
        EditUserDialog(
            user = user,
            onDismiss = { editingUser = null },
            onSave = {
                userViewModel.updateUser(it)
                editingUser = null
            }
        )
    }
}

@Composable
fun EditUserDialog(
    user: UserItem,
    onDismiss: () -> Unit,
    onSave: (UserItem) -> Unit
) {
    var username by remember { mutableStateOf(user.username) }
    var phone by remember { mutableStateOf(user.phone ?: "") }
    var role by remember { mutableStateOf(user.role) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit User") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = role == "user", onClick = { role = "user" })
                    Text("User")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = role == "admin", onClick = { role = "admin" })
                    Text("Admin")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(user.copy(username = username, phone = phone, role = role))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

package com.example.poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetGoalsScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    var minGoal by remember { mutableStateOf(user?.minMonthlyGoal?.toString() ?: "0") }
    var maxGoal by remember { mutableStateOf(user?.maxMonthlyGoal?.toString() ?: "0") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Monthly Goals") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = minGoal,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) minGoal = it },
                label = { Text("Minimum Monthly Goal (R)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxGoal,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) maxGoal = it },
                label = { Text("Maximum Monthly Goal (R)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val min = minGoal.toDoubleOrNull() ?: 0.0
                    val max = maxGoal.toDoubleOrNull() ?: 0.0
                    user?.let {
                        val updatedUser = it.copy(minMonthlyGoal = min, maxMonthlyGoal = max)
                        viewModel.updateUser(updatedUser)
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Goals")
            }
        }
    }
}

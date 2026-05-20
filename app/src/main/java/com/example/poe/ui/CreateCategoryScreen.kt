package com.example.poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Screen that allows users to create custom categories for their transactions.
 * users can specify whether the category is for Income or Expenses.
 * This supports the "Custom Categories" requirement of the project.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCategoryScreen(
    financeViewModel: FinanceViewModel,
    isIncome: Boolean,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    // Local state for the category name input field
    var categoryName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isIncome) "Create Income Category" else "Create Expense Category") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // TextField for entering the new category name
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Button to save the category to the database via the ViewModel
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        financeViewModel.addCategory(categoryName, isIncome)
                        onSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = categoryName.isNotBlank()
            ) {
                Text("Save Category")
            }
        }
    }
}

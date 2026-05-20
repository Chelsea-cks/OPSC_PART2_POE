package com.example.poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Menu screen for expense-related actions.
 * provides navigation options to log a new expense, track existing expenses,
 * or create a custom category specifically for expenses.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseOptionsScreen(
    onBack: () -> Unit,
    onNavigateToLogExpense: () -> Unit,
    onNavigateToTrackExpense: () -> Unit,
    onNavigateToCreateCategory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expenses") },
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
            // Navigate to the LogTransactionScreen configured for expenses
            Button(onClick = onNavigateToLogExpense, modifier = Modifier.fillMaxWidth()) {
                Text("Log an expense")
            }
            // Navigate to the TrackTransactionScreen to view/filter expense lists
            Button(onClick = onNavigateToTrackExpense, modifier = Modifier.fillMaxWidth()) {
                Text("Track an expense")
            }
            // Navigate to the CreateCategoryScreen for new expense categories
            Button(onClick = onNavigateToCreateCategory, modifier = Modifier.fillMaxWidth()) {
                Text("Create a new category")
            }
        }
    }
}

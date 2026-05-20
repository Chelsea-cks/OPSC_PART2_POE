package com.example.poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Menu screen for income-related actions.
 * Provides navigation options to log a new income entry or view the history of logged income.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeOptionsScreen(
    onBack: () -> Unit,
    onNavigateToLogIncome: () -> Unit,
    onNavigateToViewIncomeList: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Income") },
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
            // Button to navigate to the LogTransactionScreen configured for income
            Button(
                onClick = onNavigateToLogIncome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Income")
            }

            // Button to navigate to the TrackTransactionScreen to view all logged income
            Button(
                onClick = onNavigateToViewIncomeList,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Income List")
            }
        }
    }
}

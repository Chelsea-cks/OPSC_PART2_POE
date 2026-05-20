package com.example.poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poe.util.AppLogger
import java.util.Locale

/**
 * The main landing screen after a successful login.
 * Displays the user's welcome message, current balance, and monthly goals.
 * Provides navigation buttons to log income/expenses and view transaction history.
 */
@Composable
fun DashboardScreen(
    viewModel: AuthViewModel,
    financeViewModel: FinanceViewModel,
    onLogout: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToIncome: () -> Unit,
    onNavigateToAllTransactions: () -> Unit,
    onNavigateToSetGoals: () -> Unit
) {
    // Collect the current user and balance states from their respective ViewModels
    val user by viewModel.currentUser.collectAsState()
    val balance by financeViewModel.getTotalBalance().collectAsState(initial = 0.0)

    // Log composition event for debugging
    AppLogger.d("DashboardScreen composed for user: ${user?.username ?: "Unknown"}", "UI")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header row with welcome message and logout button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome, ${user?.username ?: "User"}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onLogout) {
                Text("Logout", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display of monthly goals (Min and Max) as set by the user
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Min Goal: R ${String.format("%.2f", user?.minMonthlyGoal ?: 0.0)}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Max Goal: R ${String.format("%.2f", user?.maxMonthlyGoal ?: 0.0)}", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Card showing the total balance of the user
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Total Balance", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "R ${String.format(Locale.getDefault(), "%.2f", balance)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Primary action buttons for logging income and expenses
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onNavigateToIncome,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green for Income
            ) {
                Text("Income (+)")
            }
            Button(
                onClick = onNavigateToExpenses,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)) // Red for Expense
            ) {
                Text("Expense (-)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Secondary actions for viewing transaction lists and setting goals
        OutlinedButton(
            onClick = onNavigateToAllTransactions,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View All Transactions")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation to the "Set Goals" screen
        TextButton(
            onClick = onNavigateToSetGoals,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set Monthly Goals")
        }
    }
}

package com.example.poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.poe.data.Transaction

/**
 * Screen that displays a comprehensive list of all transactions (income and expense).
 * Provides a search bar to filter transactions by their description.
 * Allows users to view attached photos for any transaction in a dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
    financeViewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    // State for the search query entered by the user
    var searchQuery by remember { mutableStateOf("") }
    
    // Observed list of transactions from the ViewModel, filtered by search query
    val transactions by financeViewModel.getTransactions(searchQuery).collectAsState(initial = emptyList())
    
    // State to track which transaction's image is currently being viewed in a full-screen dialog
    var selectedTransactionForImage by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Transactions") },
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
                .padding(16.dp)
        ) {
            // Search input field for filtering the transaction list
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search transactions") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable list of transactions
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onImageClick = { selectedTransactionForImage = it }
                    )
                }
            }
        }

        // Dialog to show the full-size image attached to a transaction
        selectedTransactionForImage?.let { transaction ->
            Dialog(onDismissRequest = { selectedTransactionForImage = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    AsyncImage(
                        model = transaction.imageUri,
                        contentDescription = "Transaction Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

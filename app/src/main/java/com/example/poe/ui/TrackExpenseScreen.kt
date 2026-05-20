package com.example.poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.poe.data.Transaction
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackExpenseScreen(
    financeViewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(0L) }
    var endDate by remember { mutableLongStateOf(Long.MAX_VALUE) }
    var selectedTransactionForImage by remember { mutableStateOf<Transaction?>(null) }
    
    val transactions by financeViewModel.getTransactions(searchQuery).collectAsState(initial = emptyList())
    
    val filteredTransactions = transactions.filter { 
        it.isExpense && it.date in startDate..endDate 
    }

    var showDatePickerRange by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    if (showDatePickerRange) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerRange = false },
            confirmButton = {
                TextButton(onClick = {
                    startDate = dateRangePickerState.selectedStartDateMillis ?: 0L
                    endDate = dateRangePickerState.selectedEndDateMillis ?: Long.MAX_VALUE
                    showDatePickerRange = false
                }) { Text("OK") }
            }
        ) {
            DateRangePicker(state = dateRangePickerState, modifier = Modifier.weight(1f))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Expenses") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePickerRange = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Period")
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by description") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            if (startDate != 0L || endDate != Long.MAX_VALUE) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Text(
                    text = "Period: ${sdf.format(Date(startDate))} - ${if (endDate == Long.MAX_VALUE) "Now" else sdf.format(Date(endDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TextButton(onClick = { 
                    startDate = 0L
                    endDate = Long.MAX_VALUE
                }) {
                    Text("Clear Filter")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTransactions) { transaction ->
                    TransactionItem(transaction, onImageClick = { selectedTransactionForImage = it })
                }
            }
        }

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

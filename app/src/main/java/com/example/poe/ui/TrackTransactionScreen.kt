package com.example.poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.poe.data.CategoryTotal
import com.example.poe.data.Transaction
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackTransactionScreen(
    financeViewModel: FinanceViewModel,
    isExpense: Boolean,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val transactions by financeViewModel.getTransactions(searchQuery).collectAsState(initial = emptyList())
    val filteredExpenses by financeViewModel.getFilteredExpenses().collectAsState(initial = emptyList())
    val categoryTotals by financeViewModel.getCategoryTotals().collectAsState(initial = emptyList())
    
    val startDate by financeViewModel.startDate.collectAsState()
    val endDate by financeViewModel.endDate.collectAsState()

    var showDateRangePicker by remember { mutableStateOf(false) }
    var selectedTransactionForImage by remember { mutableStateOf<Transaction?>(null) }
    var currentTab by remember { mutableIntStateOf(0) }

    val dateRangePickerState = rememberDateRangePickerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isExpense) "Track Expenses" else "Track Income") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isExpense) {
                        IconButton(onClick = { showDateRangePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date Range")
                        }
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
            if (isExpense) {
                TabRow(selectedTabIndex = currentTab) {
                    Tab(selected = currentTab == 0, onClick = { currentTab = 0 }) {
                        Text(text = "Search", modifier = Modifier.padding(16.dp))
                    }
                    Tab(selected = currentTab == 1, onClick = { currentTab = 1 }) {
                        Text(text = "Period List", modifier = Modifier.padding(16.dp))
                    }
                    Tab(selected = currentTab == 2, onClick = { currentTab = 2 }) {
                        Text(text = "Category Totals", modifier = Modifier.padding(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (currentTab) {
                0 -> {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search by description") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(transactions.filter { it.isExpense == isExpense }) { transaction ->
                            TransactionItem(transaction, onImageClick = { selectedTransactionForImage = it })
                        }
                    }
                }
                1 -> {
                    if (startDate == null || endDate == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Please select a date range using the icon in the top bar")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredExpenses) { transaction ->
                                TransactionItem(transaction, onImageClick = { selectedTransactionForImage = it })
                            }
                        }
                    }
                }
                2 -> {
                    if (startDate == null || endDate == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Please select a date range using the icon in the top bar")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categoryTotals) { total ->
                                CategoryTotalItem(total)
                            }
                        }
                    }
                }
            }
        }

        if (showDateRangePicker) {
            DatePickerDialog(
                onDismissRequest = { showDateRangePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        financeViewModel.setDateRange(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                        showDateRangePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDateRangePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier.height(400.dp)
                )
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

@Composable
fun TransactionItem(transaction: Transaction, onImageClick: (Transaction) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.description, fontWeight = FontWeight.Bold)
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(transaction.date)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            if (transaction.imageUri != null) {
                IconButton(onClick = { onImageClick(transaction) }) {
                    Icon(Icons.Default.Photo, contentDescription = "View Photo", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Text(
                text = "${if (transaction.isExpense) "-" else "+"}R ${String.format(Locale.getDefault(), "%.2f", transaction.amount)}",
                color = if (transaction.isExpense) MaterialTheme.colorScheme.error else Color(0xFF4CAF50),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CategoryTotalItem(total: CategoryTotal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = total.categoryName, fontWeight = FontWeight.SemiBold)
            Text(
                text = "R ${String.format(Locale.getDefault(), "%.2f", total.totalAmount)}",
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

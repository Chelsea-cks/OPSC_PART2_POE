package com.example.poe.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.poe.data.Category
import java.text.SimpleDateFormat
import java.util.*

/**
 * screen for logging a new financial transaction (Income or Expense).
 * provides fields for amount, category, date, time range, and description.
 * users can also attach an image (e.g., a receipt) using the system's Photo Picker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogTransactionScreen(
    financeViewModel: FinanceViewModel,
    isExpense: Boolean,
    accountType: String,
    preSelectedCategoryName: String? = null,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    // Local state for UI components
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endTime by remember { mutableLongStateOf(System.currentTimeMillis() + 3600000) } // Default 1 hour later
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Observed categories for the dropdown menu
    val categories by financeViewModel.getCategories(!isExpense).collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }
    
    // Dialog state management
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
    var showDatePicker by remember { mutableStateOf(false) }
    
    val startTimePickerState = rememberTimePickerState()
    var showStartTimePicker by remember { mutableStateOf(false) }

    val endTimePickerState = rememberTimePickerState()
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Launcher for the Android Photo Picker (requirement for photo storage)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    // Automatically select a category if passed via navigation
    LaunchedEffect(categories) {
        if (selectedCategory == null && preSelectedCategoryName != null) {
            selectedCategory = categories.find { it.name.equals(preSelectedCategoryName, ignoreCase = true) }
        }
    }

    // --- Date and Time Picker Dialogs ---
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis ?: selectedDate
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showStartTimePicker) {
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = selectedDate
                    cal.set(Calendar.HOUR_OF_DAY, startTimePickerState.hour)
                    cal.set(Calendar.MINUTE, startTimePickerState.minute)
                    startTime = cal.timeInMillis
                    showStartTimePicker = false
                }) { Text("OK") }
            },
            text = { TimePicker(state = startTimePickerState) }
        )
    }

    if (showEndTimePicker) {
        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = selectedDate
                    cal.set(Calendar.HOUR_OF_DAY, endTimePickerState.hour)
                    cal.set(Calendar.MINUTE, endTimePickerState.minute)
                    endTime = cal.timeInMillis
                    showEndTimePicker = false
                }) { Text("OK") }
            },
            text = { TimePicker(state = endTimePickerState) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isExpense) "Log Expense" else "Log Income") },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            // Category Selection Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Select Category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Date selection trigger
            OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))}")
            }

            // Time range selection triggers
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showStartTimePicker = true }, modifier = Modifier.weight(1f)) {
                    Text("Start: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(startTime))}")
                }
                OutlinedButton(onClick = { showEndTimePicker = true }, modifier = Modifier.weight(1f)) {
                    Text("End: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(endTime))}")
                }
            }

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            // Photo Attachment Trigger (Requirement for photo storage)
            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (imageUri == null) "Attach Image (Optional)" else "Image Attached")
            }

            // Final Submit Button
            Button(
                onClick = {
                    val finalAmount = amount.toDoubleOrNull() ?: 0.0
                    val catId = selectedCategory?.id ?: 0
                    if (catId != 0 && finalAmount > 0) {
                        financeViewModel.addTransaction(
                            categoryId = catId,
                            amount = finalAmount,
                            description = description,
                            date = selectedDate,
                            startTime = startTime,
                            endTime = endTime,
                            isExpense = isExpense,
                            imageUri = imageUri?.toString()
                        )
                        onSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotEmpty() && selectedCategory != null
            ) {
                Text("Submit")
            }
        }
    }
}

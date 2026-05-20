package com.example.poe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poe.data.*
import com.example.poe.util.AppLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel that manages financial data including transactions, categories, and balances.
 * handles the business logic for filtering transactions by date range and calculating totals.
 * acts as the primary data source for all finance-related screens.
 */
class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    // Current user context
    private val _userId = MutableStateFlow<Int?>(null)
    
    // State for user-selectable date range used in reports
    private val _startDate = MutableStateFlow<Long?>(null)
    private val _endDate = MutableStateFlow<Long?>(null)

    val startDate: StateFlow<Long?> = _startDate
    val endDate: StateFlow<Long?> = _endDate

    /**
     * Initializes the ViewModel with the current user's ID.
     * Also triggers the prepopulation of default categories if they don't exist.
     */
    fun setUserId(id: Int) {
        _userId.value = id
        prepopulateCategories(id)
    }

    /**
     * updates the period filter for reports and expense lists.
     */
    fun setDateRange(start: Long?, end: Long?) {
        _startDate.value = start
        _endDate.value = end
    }

    /**
     * ensures every user has a base set of categories to choose from upon first login.
     */
    private fun prepopulateCategories(userId: Int) {
        viewModelScope.launch {
            AppLogger.i("Prepopulating default categories for user ID: $userId", "Finance")
            repository.getCategories(userId, true).first().let { incomeCats ->
                if (incomeCats.isEmpty()) {
                    val defaultIncome = listOf("Salary", "Deposit", "Gift", "Tips", "Other")
                    defaultIncome.forEach { name ->
                        repository.insertCategory(Category(userId = userId, name = name, isIncome = true))
                    }
                }
            }
            repository.getCategories(userId, false).first().let { expenseCats ->
                if (expenseCats.isEmpty()) {
                    val defaultExpenses = listOf("Food", "Transport", "Rent", "Entertainment", "Utilities", "Other")
                    defaultExpenses.forEach { name ->
                        repository.insertCategory(Category(userId = userId, name = name, isIncome = false))
                    }
                }
            }
            AppLogger.i("Default categories prepopulated successfully", "Finance")
        }
    }

    /**
     * returns a flow of transactions filtered by a text search query.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTransactions(query: String = ""): Flow<List<Transaction>> {
        return _userId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.searchTransactions(id, query)
        }
    }

    /**
     * returns expenses filtered by the user-selected date range.
     * meets the "user selectable period" requirement for expense lists.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFilteredExpenses(): Flow<List<Transaction>> {
        return combine(_userId, _startDate, _endDate) { id, start, end ->
            Triple(id, start, end)
        }.flatMapLatest { (id, start, end) ->
            if (id == null || start == null || end == null) flowOf(emptyList())
            else repository.getExpensesByDateRange(id, start, end)
        }
    }

    /**
     * returns the sum of money spent on each category within the selected period.
     * meets the "total amount per category" requirement.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCategoryTotals(): Flow<List<CategoryTotal>> {
        return combine(_userId, _startDate, _endDate) { id, start, end ->
            Triple(id, start, end)
        }.flatMapLatest { (id, start, end) ->
            if (id == null || start == null || end == null) flowOf(emptyList())
            else repository.getCategoryTotalsByDateRange(id, start, end)
        }
    }

    /**
     * calculates the current overall balance (Total Income - Total Expenses).
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTotalBalance(): Flow<Double> {
        return _userId.flatMapLatest { id ->
            if (id == null) flowOf(0.0)
            else {
                combine(
                    repository.getTotalIncome(id),
                    repository.getTotalExpense(id)
                ) { income, expense ->
                    (income ?: 0.0) - (expense ?: 0.0)
                }
            }
        }
    }

    /**
     * retrieves categories filtered by type.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCategories(isIncome: Boolean): Flow<List<Category>> {
        return _userId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getCategories(id, isIncome)
        }
    }

    /**
     * logs a new financial transaction to the database.
     */
    fun addTransaction(
        categoryId: Int,
        amount: Double,
        description: String,
        date: Long,
        startTime: Long,
        endTime: Long,
        isExpense: Boolean,
        imageUri: String? = null
    ) {
        val id = _userId.value ?: return
        viewModelScope.launch {
            AppLogger.i("Inserting new ${if (isExpense) "expense" else "income"} transaction for user $id", "Finance")
            repository.insertTransaction(
                Transaction(
                    userId = id,
                    categoryId = categoryId,
                    amount = amount,
                    description = description,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    isExpense = isExpense,
                    imageUri = imageUri
                )
            )
            AppLogger.i("Transaction inserted successfully", "Finance")
        }
    }

    /**
     * adds a custom category defined by the user.
     */
    fun addCategory(name: String, isIncome: Boolean) {
        val id = _userId.value ?: return
        viewModelScope.launch {
            AppLogger.i("Creating new ${if (isIncome) "income" else "expense"} category: $name for user $id", "Finance")
            repository.insertCategory(
                Category(userId = id, name = name, isIncome = isIncome)
            )
            AppLogger.i("Category created successfully", "Finance")
        }
    }
}

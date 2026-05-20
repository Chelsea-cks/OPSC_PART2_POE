package com.example.poe.data

import kotlinx.coroutines.flow.Flow
import com.example.poe.util.AppLogger

/**
 * Repository class that abstracts access to the Finance data (Transactions and Categories).
 * serves as the mediator between the ViewModel and the Room Data Access Objects (DAOs).
 * Handles logging of database operations for transparency.
 */
class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    // --- Transaction Operations ---

    /**
     * Persists a new transaction in the database.
     */
    suspend fun insertTransaction(transaction: Transaction) {
        AppLogger.d("Inserting transaction (categoryId=${transaction.categoryId}, amount=${transaction.amount})", "DB")
        transactionDao.insertTransaction(transaction)
    }

    /**
     * Retrieves all transactions for a user, reactive via Flow.
     */
    fun getAllTransactions(userId: Int): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsForUser(userId)
    }

    /**
     * Searches transactions based on a description query.
     */
    fun searchTransactions(userId: Int, query: String): Flow<List<Transaction>> {
        return transactionDao.searchTransactions(userId, query)
    }

    /**
     * Calculates the total income for a user across all accounts.
     */
    fun getTotalIncome(userId: Int): Flow<Double?> {
        return transactionDao.getTotalIncome(userId)
    }

    /**
     * Calculates the total expenses for a user across all accounts.
     */
    fun getTotalExpense(userId: Int): Flow<Double?> {
        return transactionDao.getTotalExpense(userId)
    }

    /**
     * Retrieves transactions within a specific date range for the expense list requirement.
     */
    fun getExpensesByDateRange(userId: Int, startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionDao.getExpensesByDateRange(userId, startDate, endDate)
    }

    /**
     * Retrieves category-wise totals within a date range for the reporting requirement.
     */
    fun getCategoryTotalsByDateRange(userId: Int, startDate: Long, endDate: Long): Flow<List<CategoryTotal>> {
        return transactionDao.getCategoryTotalsByDateRange(userId, startDate, endDate)
    }

    // --- Category Operations ---

    /**
     * Inserts a new custom or default category.
     */
    suspend fun insertCategory(category: Category) {
        AppLogger.d("Inserting category: ${category.name} (isIncome=${category.isIncome})", "DB")
        categoryDao.insertCategory(category)
    }

    /**
     * Retrieves the list of categories filtered by type (Income/Expense).
     */
    fun getCategories(userId: Int, isIncome: Boolean): Flow<List<Category>> {
        return categoryDao.getCategoriesForUser(userId, isIncome)
    }

    fun calculateBudgetProgress(
        totalSpent:Double,
        minGoal:Double,
        maxGoal: Double
    ):String {
        return when{
            totalSpent<minGoal -> "Below minimum goal"
            totalSpent<maxGoal -> "Exceeded maximum goal"
            else -> "Within budget goals"
        }
    }

    suspend fun checkAndUnlockBadges(
        totalTransactions: Int,
        stayedWithinBudget: Boolean,
        badgeDao: BadgeDao
    ){
        if (totalTransactions >= 10){
            badgeDao.unlockBadge(1)
        }
        if(totalTransactions>=10){
            badgeDao.unlockBadge(1)
        }
        if(stayedWithinBudget){
            badgeDao.unlockBadge(2)
        }
    }
}

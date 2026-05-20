package com.example.poe.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the Transaction entity.
 * Contains SQL queries to handle all transaction-related data operations,
 * including filtering by date range and aggregating totals by category.
 */
@Dao
interface TransactionDao {
    /**
     * Inserts a new transaction or replaces an existing one if a conflict occurs.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    /**
     * Retrieves all transactions for a specific user, ordered from newest to oldest.
     */
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getAllTransactionsForUser(userId: Int): Flow<List<Transaction>>

    /**
     * Searches for transactions for a user based on a description string.
     */
    @Query("""
        SELECT * FROM transactions 
        WHERE userId = :userId 
        AND (description LIKE '%' || :search || '%' OR :search = '')
        ORDER BY date DESC
    """)
    fun searchTransactions(userId: Int, search: String): Flow<List<Transaction>>

    /**
     * Calculates the sum of all income transactions for a user.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND isExpense = 0")
    fun getTotalIncome(userId: Int): Flow<Double?>

    /**
     * Calculates the sum of all expense transactions for a user.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND isExpense = 1")
    fun getTotalExpense(userId: Int): Flow<Double?>

    /**
     * Retrieves a list of expenses within a specific date range.
     * Meets the "user selectable period" requirement.
     */
    @Query("SELECT * FROM transactions WHERE userId = :userId AND isExpense = 1 AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(userId: Int, startDate: Long, endDate: Long): Flow<List<Transaction>>

    /**
     * Aggregates spending totals by category for a specific time period.
     * meets the "total amount per category" requirement.
     */
    @Query("""
        SELECT c.name as categoryName, SUM(t.amount) as totalAmount 
        FROM transactions t 
        INNER JOIN categories c ON t.categoryId = c.id 
        WHERE t.userId = :userId AND t.isExpense = 1 AND t.date BETWEEN :startDate AND :endDate 
        GROUP BY t.categoryId
    """)
    fun getCategoryTotalsByDateRange(userId: Int, startDate: Long, endDate: Long): Flow<List<CategoryTotal>>

    /**
     * Calculates total income for a specific account type (e.g., Savings, Checking).
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND isExpense = 0 AND accountType = :accountType")
    fun getTotalIncomeForAccount(userId: Int, accountType: String): Flow<Double?>

    /**
     * Calculates total expenses for a specific account type.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND isExpense = 1 AND accountType = :accountType")
    fun getTotalExpenseForAccount(userId: Int, accountType: String): Flow<Double?>
}

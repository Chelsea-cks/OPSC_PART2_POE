package com.example.poe.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the Category entity.
 * Provides methods for performing CRUD operations on the categories table in the Room database.
 */
@Dao
interface CategoryDao {
    /**
     * Inserts a new category or replaces an existing one if there's a conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    /**
     * Retrieves all categories for a specific user and type (income/expense), sorted alphabetically.
     * returns a reactive Flow that updates whenever the database changes.
     */
    @Query("SELECT * FROM categories WHERE userId = :userId AND isIncome = :isIncome ORDER BY name ASC")
    fun getCategoriesForUser(userId: Int, isIncome: Boolean): Flow<List<Category>>

    /**
     * Retrieves all categories associated with a user, regardless of type.
     */
    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
    fun getAllCategoriesForUser(userId: Int): Flow<List<Category>>
}

package com.example.poe.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object (DAO) for the User entity.
 * Defines the database operations for user management including registration,
 * authentication (login), and profile updates.
 */
@Dao
interface UserDao {
    /**
     * Inserts a new user into the database. 
     * Uses ABORT strategy to prevent duplicate usernames if unique constraints are violated.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    /**
     * Retrieves a user by their unique username.
     * Primarily used during the login process to verify credentials.
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    /**
     * Updates an existing user's information, such as their monthly financial goals.
     */
    @Update
    suspend fun updateUser(user: User)
}

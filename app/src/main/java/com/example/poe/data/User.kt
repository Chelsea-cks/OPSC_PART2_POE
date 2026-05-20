package com.example.poe.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a User entity in the Room database.
 * Stores user profile information, credentials, and monthly financial goals.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val email: String,
    val phone: String,
    val minMonthlyGoal: Double = 0.0,
    val maxMonthlyGoal: Double = 0.0
)

package com.example.poe.data

import androidx.room.Entity
import androidx.room.PrimaryKey

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

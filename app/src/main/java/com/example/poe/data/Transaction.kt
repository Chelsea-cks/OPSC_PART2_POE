package com.example.poe.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Data class representing a financial transaction (Income or Expense).
 * includes details like amount, date, time range, and an optional image URI.
 * uses foreign keys to link to the [User] who owns the transaction and the [Category] it belongs to.
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val categoryId: Int,
    val amount: Double,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val date: Long,
    val imageUri: String? = null,
    val isExpense: Boolean,
    val accountType: String = "General"
)

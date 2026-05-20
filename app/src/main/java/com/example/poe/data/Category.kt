package com.example.poe.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Data class representing a financial category (e.g., "Food", "Salary").
 * Categories are used to classify transactions.
 * Linked to a [User] through a foreign key.
 */
@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val name: String,
    val isIncome: Boolean // To distinguish between income categories and expense categories
)

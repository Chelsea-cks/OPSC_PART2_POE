package com.example.poe.data

/**
 * A simple data class used to represent the total amount spent in a specific category.
 * Used primarily for the "Category Totals" report in the UI.
 */
data class CategoryTotal(
    val categoryName: String,
    val totalAmount: Double
)

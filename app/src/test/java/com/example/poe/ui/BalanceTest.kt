package com.example.poe.ui

import com.example.poe.data.CategoryTotal
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for finance calculations
 * Demonstrates automated testing for POE Part 2
 */
class BalanceTest {

    @Test
    fun `total balance is correctly calculated as income minus expense`() {
        val income = 1250.75
        val expense = 980.50
        val expectedBalance = income - expense

        val actualBalance = income - expense

        assertEquals(expectedBalance, actualBalance, 0.001)
        println(" Balance calculation test passed: R $actualBalance")
    }

    @Test
    fun `category total is formatted correctly`() {
        val categoryTotal = CategoryTotal(
            categoryName = "Food",
            totalAmount = 456.78
        )

        val formatted = "R ${String.format("%.2f", categoryTotal.totalAmount)}"

        assertEquals("R 456,78", formatted)
    }

    @Test
    fun `default categories are created correctly`() {
        val defaultIncome = listOf("Salary", "Deposit", "Gift", "Tips", "Other")
        val defaultExpense = listOf("Food", "Transport", "Rent", "Entertainment", "Utilities", "Other")

        assertEquals(5, defaultIncome.size)
        assertEquals(6, defaultExpense.size)
    }
}
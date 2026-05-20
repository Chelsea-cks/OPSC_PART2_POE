package com.example.poe

import com.example.poe.data.FinanceRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class FinanceLogicTest{
    @Test
    fun budgetProgress_returnsCorrectMessage(){
        val result = BudgetUtils.calculateBudgetProgress(
            totalSpent = 1500.0,
            minGoal = 1000.0,
            maxGoal = 2000.0
        )
        assertEquals("Within budget goals",result)
    }
}
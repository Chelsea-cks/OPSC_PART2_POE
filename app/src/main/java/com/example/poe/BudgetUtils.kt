package com.example.poe

object BudgetUtils {
    fun calculateBudgetProgress(
        totalSpent: Double,
        minGoal: Double,
        maxGoal: Double,
    ): String {
        return when {
            totalSpent < minGoal -> "Below minimum goal"
            totalSpent > maxGoal -> "Exceeded maximum goal"
            else -> "Within budget goals"
        }
    }
}

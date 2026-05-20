package com.example.poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BudgetProgressScreen(
    spent:Float,
    maxGoal: Float
){
    val progress = spent/maxGoal

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Budget Usage")
        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.height(8.dp)
        )
        Text(text = "R$spent/R$maxGoal")
    }
    }

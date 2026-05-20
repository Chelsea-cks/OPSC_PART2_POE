package com.example.poe.ui

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

@Composable
fun SpendingGraphScreen() {
    val entries = listOf(
        BarEntry(1f, 1200f),
        BarEntry(2f, 800f),
        BarEntry(3f, 1500f),
        BarEntry(4f, 950f)
    )

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {context->
            val chart = BarChart(context)
            val dataSet = BarDataSet(entries,"Category Spending")
            dataSet.color = Color.BLUE
            val data = BarData(dataSet)
            chart.data = data

            chart.description.text = "Monthly Spending"
            chart.animateY(1000)

            chart
        }
    )
}


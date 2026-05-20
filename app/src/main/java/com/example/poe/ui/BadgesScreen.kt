package com.example.poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.poe.data.Badge

@Composable
fun BadgesScreen(badges: List<Badge>) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    )
    {
        items(badges) { badge ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            {Text(text = badge.title)
            Text(text = badge.description)

            if(badge.achieved){
                Text(text = "Unlocked")
            }else{
                Text(text="Locked")
            }}
        }
    }
}
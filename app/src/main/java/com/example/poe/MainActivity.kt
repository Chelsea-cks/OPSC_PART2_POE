package com.example.poe
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


class MainActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            var darkMode by remember {
                mutableStateOf(false)
            }
            MaterialTheme(
                colorScheme =
                    if (darkMode)
                        darkColorScheme()
                    else
                        lightColorScheme()
            ) {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                                onClick = {
                                    darkMode = !darkMode
                                }
                                ){
                            Icon(
                                imageVector =
                                    if (darkMode)
                                        Icons.Default.LightMode
                                    else
                                        Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Text(
                            text = "Finance Tracker App"
                        )
                    }
                }
            }
        }
    }
}

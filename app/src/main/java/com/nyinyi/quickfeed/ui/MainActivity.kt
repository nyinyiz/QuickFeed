package com.nyinyi.quickfeed.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.nyinyi.quickfeed.ui.navigation.SetUpNavGraph
import com.nyinyi.quickfeed.ui.theme.QuickFeedTheme

class MainActivity : ComponentActivity() {
    var darkTheme = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickFeedTheme(
                darkTheme = darkTheme.value,
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    SetUpNavGraph(
                        navController = navController,
                        onToggleTheme = { darkTheme.value = darkTheme.value.not() },
                    )
                }
            }
        }
    }
}

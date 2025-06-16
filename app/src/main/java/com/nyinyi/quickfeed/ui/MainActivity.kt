package com.nyinyi.quickfeed.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.nyinyi.quickfeed.provider.ThemePreferenceManager
import com.nyinyi.quickfeed.ui.navigation.SetUpNavGraph
import com.nyinyi.quickfeed.ui.theme.QuickFeedTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themePreferenceManager: ThemePreferenceManager
    var darkTheme = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        darkTheme.value = themePreferenceManager.getDarkModeStatus()

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
                        onToggleTheme = {
                            val newThemeValue = !darkTheme.value
                            darkTheme.value = newThemeValue
                            themePreferenceManager.saveDarkModeStatus(newThemeValue)
                        },
                    )
                }
            }
        }
    }
}

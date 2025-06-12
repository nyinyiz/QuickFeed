package com.nyinyi.quickfeed.ui.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen() {
    LoginContent()
}

@Composable
fun LoginContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "Login Screen")
    }
}

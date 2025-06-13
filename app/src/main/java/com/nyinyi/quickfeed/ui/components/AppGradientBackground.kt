package com.nyinyi.quickfeed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AppGradientBackground(
    modifier: Modifier = Modifier,
    topColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
    bottomColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    topColor,
                                    bottomColor,
                                    bottomColor,
                                ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY,
                        ),
                ),
    ) {
        content()
    }
}

@Composable
fun DefaultAppGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val topColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
    val bottomColor = MaterialTheme.colorScheme.surface

    AppGradientBackground(
        modifier = modifier,
        topColor = topColor,
        bottomColor = bottomColor,
        content = content,
    )
}

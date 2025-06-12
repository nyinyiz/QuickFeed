package com.nyinyi.quickfeed.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun ShimmeringText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    shimmerColors: List<Color> =
        listOf(
            Color.White.copy(alpha = 0.0f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 1.0f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.0f),
        ),
    shimmerDurationMillis: Int = 2000,
) {
    val textLayoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim =
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = shimmerDurationMillis,
                            easing = LinearEasing,
                        ),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "shimmerTranslate",
        )

    val brush =
        remember(translateAnim.value) {
            val textWidth =
                textLayoutResult.value
                    ?.size
                    ?.width
                    ?.toFloat() ?: 0f
            if (textWidth == 0f) {
                Brush.linearGradient(shimmerColors)
            } else {
                val gradientWidth = textWidth / 2
                val startX = (translateAnim.value * (textWidth + gradientWidth)) - gradientWidth
                val endX = startX + gradientWidth

                Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(startX, 0f),
                    end = Offset(endX, 0f),
                    tileMode = TileMode.Clamp,
                )
            }
        }

    Text(
        text = text,
        style =
            style.copy(
                brush = brush,
                shadow =
                    Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = Offset(x = 2f, y = 2f),
                        blurRadius = 1f,
                    ),
            ),
        modifier = modifier,
        onTextLayout = {
            textLayoutResult.value = it
        },
        color = MaterialTheme.colorScheme.onPrimary,
    )
}

@Preview(showBackground = true)
@Composable
fun ShimmeringTextPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ShimmeringText(
                text = "Shine On Me",
                style =
                    TextStyle(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Blue,
                    ),
                shimmerColors =
                    listOf(
                        Color.Blue.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.8f),
                        Color.Cyan.copy(alpha = 1.0f),
                        Color.White.copy(alpha = 0.8f),
                        Color.Blue.copy(alpha = 0.5f),
                    ),
            )
        }
    }
}

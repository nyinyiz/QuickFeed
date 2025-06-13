package com.nyinyi.quickfeed.ui.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nyinyi.quickfeed.R
import com.nyinyi.quickfeed.ui.components.AppGradientBackground
import com.nyinyi.quickfeed.ui.components.ParticleAnimationBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val logoScale = remember { Animatable(0.5f) }
    val logoAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec =
                    tween(
                        durationMillis = 800,
                        delayMillis = 200,
                        easing = FastOutSlowInEasing,
                    ),
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec =
                    spring(
                        dampingRatio = 0.4f,
                    ),
            )
        }
        delay(1500)
        onAnimationFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent,
    ) {
        AppGradientBackground {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                ParticleAnimationBackground(
                    particleBaseColor = MaterialTheme.colorScheme.secondary,
                    particleCount = 25,
                )

                AsyncImage(
                    model = R.mipmap.ic_launcher_round,
                    contentDescription = stringResource(R.string.app_name),
                    modifier =
                        Modifier
                            .size(100.dp)
                            .scale(logoScale.value)
                            .alpha(logoAlpha.value),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen(onAnimationFinished = {})
    }
}

package com.nyinyi.quickfeed.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Stable
data class Particle(
    var x: Float,
    var y: Float,
    var radiusInPx: Float,
    var color: Color,
    var alpha: Float,
    var xSpeed: Float,
    var ySpeed: Float,
    var rotation: Float = 0f,
    var rotationSpeed: Float = (Random.nextFloat() - 0.5f) * 2,
)

@Composable
fun ParticleAnimationBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 20,
    particleMinRadius: Dp = 2.dp,
    particleMaxRadius: Dp = 6.dp,
    particleBaseColor: Color = MaterialTheme.colorScheme.secondary,
    particleGenerator: (
        density: Float,
        color: Color,
        screenWidthPx: Float,
        screenHeightPx: Float,
    ) -> Particle = { density, color, width, height ->
        Particle(
            x = Random.nextFloat() * width,
            y = Random.nextFloat() * height,
            radiusInPx = (Random.nextFloat() * (particleMaxRadius.value - particleMinRadius.value) + particleMinRadius.value) * density,
            color = color.copy(alpha = Random.nextFloat() * 0.5f + 0.2f),
            alpha = Random.nextFloat() * 0.7f + 0.3f,
            xSpeed = (Random.nextFloat() - 0.5f) * 1.5f,
            ySpeed = (Random.nextFloat() - 0.5f) * 1.5f,
        )
    },
    content: @Composable BoxScope.() -> Unit = {},
) {
    val particles = remember { mutableStateListOf<Particle>() }
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition(label = "particle_infinite_transition")
    val particleAnimationTrigger by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "particle_animation_trigger",
    )

    LaunchedEffect(
        key1 = particleCount,
        key2 = particleBaseColor,
    ) {
        particles.clear()
        val initialScreenWidthPx = (300.dp).value
        val initialScreenHeightPx = (400.dp).value

        repeat(particleCount) {
            particles.add(
                particleGenerator(
                    density.density,
                    particleBaseColor,
                    initialScreenWidthPx,
                    initialScreenHeightPx,
                ),
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEachIndexed { index, particle ->
            if (particleAnimationTrigger > 0f) {
                particle.x += particle.xSpeed
                particle.y += particle.ySpeed
                particle.alpha = (particle.alpha - 0.002f).coerceAtLeast(0f)
                particle.rotation += particle.rotationSpeed

                if (particle.x < -particle.radiusInPx ||
                    particle.x > canvasWidth + particle.radiusInPx ||
                    particle.y < -particle.radiusInPx ||
                    particle.y > canvasHeight + particle.radiusInPx ||
                    particle.alpha <= 0f
                ) {
                    particles[index] =
                        particleGenerator(
                            density.density,
                            particleBaseColor,
                            canvasWidth,
                            canvasHeight,
                        ).copy(
                            xSpeed = (Random.nextFloat() - 0.5f) * 1.5f,
                            ySpeed = (Random.nextFloat() - 0.5f) * 1.5f,
                        )
                }
            }

            drawCircle(
                color = particle.color,
                radius = particle.radiusInPx,
                center = Offset(particle.x, particle.y),
                alpha = particle.alpha,
            )
        }
    }
}

package com.nyinyi.quickfeed.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun ShadowImageCard(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: Dp = 8.dp,
    aspectRatio: Float? = null,
    height: Dp? = 220.dp,
    contentScale: ContentScale = ContentScale.Crop,
    errorContent: (@Composable () -> Unit)? = null,
    overlayContent: (@Composable BoxScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    contentDescription: String = "Image",
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
) {
    val cardModifier =
        modifier
            .let { mod ->
                when {
                    aspectRatio != null -> mod.aspectRatio(aspectRatio)
                    height != null -> mod.height(height)
                    else -> mod
                }
            }

    Card(
        modifier =
            cardModifier.clickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() },
            ),
        shape = shape,
        colors =
            CardDefaults.cardColors(
                containerColor = containerColor,
            ),
        border =
            BorderStroke(
                width = 1.dp,
                brush =
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                borderColor.copy(alpha = 0.4f),
                                borderColor.copy(alpha = 0.1f),
                            ),
                    ),
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = elevation,
                pressedElevation = elevation / 4,
            ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(imageUrl)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .networkCachePolicy(CachePolicy.ENABLED)
                            .build(),
                    contentDescription = contentDescription,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(shape),
                    contentScale = contentScale,
                )
            } else {
                errorContent?.invoke() ?: DefaultErrorContent()
            }

            overlayContent?.invoke(this)
        }
    }
}

@Composable
private fun DefaultErrorContent() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors =
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            ),
                        radius = 300f,
                    ),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = "Image placeholder",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Image unavailable",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun DefaultLoadingContent() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .shimmerEffect(),
    )
}

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val shimmerColors =
        listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation =
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "shimmer",
        )

    return this.background(
        brush =
            Brush.linearGradient(
                colors = shimmerColors,
                start = Offset.Zero,
                end = Offset(x = translateAnimation.value, y = translateAnimation.value),
            ),
    )
}

@Preview
@Composable
fun ShadowImageCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ShadowImageCard(
            imageUrl = "https://example.com/image.jpg",
            modifier = Modifier.fillMaxWidth(),
        )
        ShadowImageCard(
            imageUrl = "https://example.com/profile.jpg",
            modifier = Modifier.size(120.dp),
            aspectRatio = 1f,
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp,
        )

        ShadowImageCard(
            imageUrl = "https://example.com/banner.jpg",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(160.dp),
            overlayContent = {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.7f),
                                        ),
                                ),
                            ),
                )
                Text(
                    text = "Overlay Text",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                )
            },
        )

        ShadowImageCard(
            imageUrl = "https://example.com/avatar.jpg",
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            aspectRatio = 1f,
            elevation = 6.dp,
            onClick = { /* Handle click */ },
        )

        ShadowImageCard(
            imageUrl = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            errorContent = {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(32.dp),
                        )
                        Text(
                            text = "Custom Error",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            },
        )
    }
}

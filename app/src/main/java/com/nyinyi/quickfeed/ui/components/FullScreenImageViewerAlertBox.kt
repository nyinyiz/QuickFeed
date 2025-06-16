package com.nyinyi.quickfeed.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

/**
 * A full-screen image viewer that appears as a dialog.
 *
 * @param imageUrl The URL of the image to display. Can be null if no image is initially set.
 * @param onDismissRequest Lambda to be invoked when the dialog is dismissed (e.g., back press, tap outside).
 * @param isVisible Controls the visibility of the dialog.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FullScreenImageViewerAlertBox(
    imageUrl: String?,
    onDismissRequest: () -> Unit,
    isVisible: Boolean,
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (isVisible && imageUrl != null) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties =
                DialogProperties(
                    usePlatformDefaultWidth = false, // Allow dialog to expand beyond default width
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true,
                ),
        ) {
            // AnimatedVisibility for smooth appearance/disappearance
            AnimatedVisibility(
                visible = true, // Control via the Dialog's presence, or could be another state
                enter =
                    fadeIn(animationSpec = tween(300)) +
                        scaleIn(
                            animationSpec = tween(300),
                            initialScale = 0.8f,
                        ),
                exit =
                    fadeOut(animationSpec = tween(300)) +
                        scaleOut(
                            animationSpec = tween(300),
                            targetScale = 0.8f,
                        ),
            ) {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null, // No ripple effect on the background
                            ) {
                                onDismissRequest() // Dismiss on tap outside the image/close button
                            },
                    color = Color.Black.copy(alpha = 0.85f), // Semi-transparent background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SubcomposeAsyncImage(
                            model =
                                ImageRequest
                                    .Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                            contentDescription = "Full Screen Image",
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                            // Padding around the image
                            contentScale = ContentScale.Fit, // Fit image within bounds, maintaining aspect ratio
                        ) {
                            val state = painter.state
                            when (state) {
                                is AsyncImagePainter.State.Loading -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        )
                                    }
                                }

                                is AsyncImagePainter.State.Error -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ReportProblem,
                                            contentDescription = "Error loading image",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(128.dp),
                                        )
                                    }
                                }

                                else -> {
                                    SubcomposeAsyncImageContent()
                                }
                            }
                        }

                        // Close Button
                        IconButton(
                            onClick = onDismissRequest,
                            modifier =
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                        CircleShape,
                                    ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Image Viewer",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "FullScreenImageViewer Preview (Visible)")
@Composable
fun FullScreenImageViewerAlertBoxPreviewVisible() {
    MaterialTheme {
        var isVisible by remember { mutableStateOf(true) }
        FullScreenImageViewerAlertBox(
            // Replace with a valid image URL for preview if you have one,
            // or use a placeholder if your Coil setup handles it.
            // For a direct preview, you might need to ensure network access or use a local resource.
            imageUrl = "https://picsum.photos/seed/compose-viewer/1200/800",
            onDismissRequest = { isVisible = false },
            isVisible = isVisible,
        )
    }
}

@Preview(showBackground = true, name = "FullScreenImageViewer Preview (Not Visible)")
@Composable
fun FullScreenImageViewerAlertBoxPreviewNotVisible() {
    MaterialTheme {
        FullScreenImageViewerAlertBox(
            imageUrl = "https://picsum.photos/seed/compose-viewer/1200/800",
            onDismissRequest = { },
            isVisible = false,
        )
    }
}

@Preview(showBackground = true, name = "FullScreenImageViewer Preview (Loading)")
@Composable
fun FullScreenImageViewerAlertBoxPreviewLoading() {
    MaterialTheme {
        // Simulate loading state for preview (Coil might be too fast locally)
        val context = LocalContext.current
        val loadingModel =
            remember {
                ImageRequest
                    .Builder(context)
                    .data("https://thisshouldtakeawhile.com/image.jpg") // A non-existent or slow URL
                    .build()
            }
        var isVisible by remember { mutableStateOf(true) }

        FullScreenImageViewerAlertBox(
            imageUrl = loadingModel.data.toString(), // Pass the URL string
            onDismissRequest = { isVisible = false },
            isVisible = isVisible,
        )
    }
}

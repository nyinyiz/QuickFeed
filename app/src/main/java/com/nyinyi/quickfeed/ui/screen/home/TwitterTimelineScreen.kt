package com.nyinyi.quickfeed.ui.screen.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyinyi.domain_model.Post
import com.nyinyi.quickfeed.R
import com.nyinyi.quickfeed.ui.components.CircleProfileIcon
import com.nyinyi.quickfeed.ui.components.DefaultAppGradientBackground
import com.nyinyi.quickfeed.ui.components.FullScreenImageViewerAlertBox
import com.nyinyi.quickfeed.ui.components.SimpleCircleProfileIcon
import com.nyinyi.quickfeed.ui.theme.QuickFeedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwitterTimelineScreen(
    uiState: HomeUiState,
    onClickCreatePost: () -> Unit,
    onClickSettings: () -> Unit = {},
    onClickProfile: () -> Unit = {},
    onRefreshTimeline: () -> Unit = {},
    onClickLike: (String) -> Unit = {},
    onClickUnLike: (String) -> Unit = {},
    onDeleteTweet: (Post) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    var isImagViewerVisible by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    val scrollBehavior =
        remember {
            object {
                var isVisible by mutableStateOf(true)
                var lastScrollPosition = 0
                var scrollThreshold = 50

                fun updateVisibility(currentPosition: Int) {
                    val scrollDelta = currentPosition - lastScrollPosition

                    when {
                        scrollDelta > scrollThreshold && isVisible -> {
                            isVisible = false
                        }

                        scrollDelta < -scrollThreshold && !isVisible -> {
                            isVisible = true
                        }

                        currentPosition <= 0 -> {
                            isVisible = true
                        }
                    }

                    lastScrollPosition = currentPosition
                }
            }
        }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex * 1000 + lazyListState.firstVisibleItemScrollOffset
        }.collect { scrollPosition ->
            scrollBehavior.updateVisibility(scrollPosition)
        }
    }

    val topBarAlpha by animateFloatAsState(
        targetValue = if (scrollBehavior.isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "TopBar Alpha",
    )

    val topBarTranslationY by animateDpAsState(
        targetValue = if (scrollBehavior.isVisible) 0.dp else (-100).dp,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
        label = "TopBar Translation",
    )

    val fabVisibility by animateFloatAsState(
        targetValue = if (scrollBehavior.isVisible) 1f else 0f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
        label = "FAB Visibility",
    )
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style =
                            MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                shadow =
                                    Shadow(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        offset = Offset(x = 2f, y = 2f),
                                        blurRadius = 1f,
                                    ),
                            ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    ),
                actions = {
                    IconButton(onClick = onClickSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClickProfile) {
                        if (uiState.userProfile == null) {
                            SimpleCircleProfileIcon(
                                icon = Icons.Default.Person,
                                size = 32.dp,
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                iconTint = Color.White,
                            )
                        } else {
                            CircleProfileIcon(
                                imageUrl = uiState.userProfile.profilePictureUrl,
                                size = 32.dp,
                            )
                        }
                    }
                },
                modifier =
                    Modifier
                        .offset(y = topBarTranslationY)
                        .alpha(topBarAlpha),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClickCreatePost,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier
                        .scale(fabVisibility)
                        .alpha(fabVisibility),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        },
    ) { padding ->
        DefaultAppGradientBackground(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .pullToRefresh(
                            isRefreshing = uiState.isTimelineLoading,
                            onRefresh = onRefreshTimeline,
                            state = pullToRefreshState,
                        ),
            ) {
                if (uiState.isTimelineLoading && uiState.timelinePosts.isEmpty()) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.timelinePosts.isEmpty()) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "No posts yet. Tap the '+' button to share something!",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                } else {
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = padding,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .pullToRefresh(
                                    isRefreshing = uiState.isTimelineLoading,
                                    onRefresh = onRefreshTimeline,
                                    state = pullToRefreshState,
                                ),
                    ) {
                        itemsIndexed(
                            items = uiState.timelinePosts,
                            key = { index, post -> post.id },
                        ) { index, post ->
                            ModernTweetCard(
                                tweet = post,
                                isMyTweet = post.authorUid == uiState.userId,
                                onClickLike = { isLiked ->
                                    if (isLiked) {
                                        onClickLike(post.id)
                                    } else {
                                        onClickUnLike(post.id)
                                    }
                                },
                                onClickMedia = { imageUrl ->
                                    selectedImageUrl = imageUrl
                                    isImagViewerVisible = true
                                },
                                onDeleteTweet = {
                                    onDeleteTweet(post)
                                },
                            )
                        }
                    }
                }
            }

            FullScreenImageViewerAlertBox(
                imageUrl = selectedImageUrl,
                onDismissRequest = {
                    selectedImageUrl = null
                    isImagViewerVisible = false
                },
                isVisible = isImagViewerVisible,
            )
        }
    }
}

@Preview
@Composable
fun TwitterTimelineScreenPreviewLight() {
    QuickFeedTheme(
        darkTheme = false,
    ) {
        TwitterTimelineScreen(
            uiState = HomeUiState(),
            onClickCreatePost = {},
        )
    }
}

@Preview
@Composable
fun TwitterTimelineScreenPreviewDark() {
    QuickFeedTheme(
        darkTheme = true,
    ) {
        TwitterTimelineScreen(uiState = HomeUiState(), onClickCreatePost = {})
    }
}

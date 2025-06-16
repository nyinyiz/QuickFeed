package com.nyinyi.quickfeed.ui.screen.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nyinyi.quickfeed.R
import com.nyinyi.quickfeed.ui.components.CircleProfileIcon
import com.nyinyi.quickfeed.ui.components.DefaultAppGradientBackground
import com.nyinyi.quickfeed.ui.components.SimpleCircleProfileIcon
import com.nyinyi.quickfeed.ui.theme.QuickFeedTheme
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/**
 * A data class representing a Tweet.
 */

data class Tweet(
    val id: Long,
    val username: String,
    val handle: String,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: String = generateFakeTime(),
    val likeCount: Int = (10..1000).random(),
    val commentCount: Int = (5..200).random(),
    val retweetCount: Int = (1..100).random(),
    val isVerified: Boolean = false,
)

// Helper function to generate a random timestamp
fun generateFakeTime(): String {
    val minutesAgo = Random.nextInt(1, 59)
    return "${minutesAgo}m"
}

@Composable
fun FormattedTweetText(
    content: String,
    modifier: Modifier = Modifier,
    onHashtagClick: (String) -> Unit = {},
    onMentionClick: (String) -> Unit = {},
    onUrlClick: (String) -> Unit = {},
) {
    val annotatedString =
        buildAnnotatedString {
            append(content)

            // Regex patterns for different text types
            val urlPattern = Regex("https?://\\S+")
            val hashtagPattern = Regex("#\\w+")
            val mentionPattern = Regex("@\\w+")

            // Style and tag URLs
            urlPattern.findAll(content).forEach { match ->
                addStyle(
                    style =
                        SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                        ),
                    start = match.range.first,
                    end = match.range.last + 1,
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = match.value,
                    start = match.range.first,
                    end = match.range.last + 1,
                )
            }

            // Style and tag hashtags
            hashtagPattern.findAll(content).forEach { match ->
                addStyle(
                    style =
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        ),
                    start = match.range.first,
                    end = match.range.last + 1,
                )
                addStringAnnotation(
                    tag = "HASHTAG",
                    annotation = match.value,
                    start = match.range.first,
                    end = match.range.last + 1,
                )
            }

            // Style and tag mentions
            mentionPattern.findAll(content).forEach { match ->
                addStyle(
                    style =
                        SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    start = match.range.first,
                    end = match.range.last + 1,
                )
                addStringAnnotation(
                    tag = "MENTION",
                    annotation = match.value,
                    start = match.range.first,
                    end = match.range.last + 1,
                )
            }
        }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style =
            MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp,
            ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let { annotation ->
                when (annotation.tag) {
                    "URL" -> onUrlClick(annotation.item)
                    "HASHTAG" -> onHashtagClick(annotation.item)
                    "MENTION" -> onMentionClick(annotation.item)
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwitterTimelineScreen(
    uiState: HomeUiState,
    onClickCreatePost: () -> Unit,
    onClickSettings: () -> Unit = {},
    onClickProfile: () -> Unit = {},
) {
    val tweets by fetchAllTweetsOnce().collectAsState(emptyList())
    val lazyListState = rememberLazyListState()

    val scrollBehavior =
        remember {
            object {
                var isVisible by mutableStateOf(true)
                var lastScrollPosition = 0
                var scrollThreshold = 50 // pixels

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
            LazyColumn(
                state = lazyListState,
                contentPadding = padding,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(tweets.size) { index ->
                    ModernTweetCard(tweets[index])
                }
            }
        }
    }
}

fun fetchAllTweetsOnce() =
    flow {
        val tweets = sampleTweets
        emit(tweets)
    }

val sampleTweets =
    listOf(
        Tweet(
            id = 1,
            username = "Nyi Nyi",
            handle = "nyinyi",
            content = "Just exploring Jetpack Compose and Firebase! #AndroidDev #Firebase",
            imageUrl = null, //  Add a URL if you have one
            timestamp = "1m",
            likeCount = 150,
            commentCount = 20,
            retweetCount = 5,
            isVerified = true,
        ),
        Tweet(
            id = 20,
            username = "Dev Humor",
            handle = "devhumor",
            content = "How to explain programming to non-programmers:\nMe: It's like giving very precise instructions to a very fast but extremely literal alien.",
            timestamp = "4h",
            likeCount = 9876,
            imageUrl = "https://picsum.photos/id/237/200/300",
            retweetCount = 2345,
            commentCount = 765,
        ),
        Tweet(
            id = 30,
            username = "Dev Humor",
            handle = "devhumor",
            content = "How to explain programming to non-programmers:\nMe: It's like giving very precise instructions to a very fast but extremely literal alien.",
            timestamp = "4h",
            likeCount = 9876,
            retweetCount = 2345,
            commentCount = 765,
        ),
        Tweet(
            id = 40,
            username = "Dev Humor",
            handle = "devhumor",
            content = "How to explain programming to non-programmers:\nMe: It's like giving very precise instructions to a very fast but extremely literal alien.",
            timestamp = "4h",
            likeCount = 9876,
            retweetCount = 2345,
            commentCount = 765,
        ),
        Tweet(
            id = 50,
            username = "Dev Humor",
            handle = "devhumor",
            content = "How to explain programming to non-programmers:\nMe: It's like giving very precise instructions to a very fast but extremely literal alien.",
            timestamp = "4h",
            likeCount = 9876,
            imageUrl = "https://picsum.photos/seed/picsum/200/300",
            retweetCount = 2345,
            commentCount = 765,
        ),
        Tweet(
            id = 60,
            username = "Dev Humor",
            handle = "devhumor",
            content = "How to explain programming to non-programmers:\nMe: It's like giving very precise instructions to a very fast but extremely literal alien.",
            timestamp = "4h",
            likeCount = 9876,
            retweetCount = 2345,
            commentCount = 765,
        ),
        Tweet(
            id = 1,
            username = "Nyi Nyi",
            handle = "nyinyi",
            content = "Just exploring Jetpack Compose and Firebase! #AndroidDev #Firebase",
            imageUrl = null, //  Add a URL if you have one
            timestamp = "1m",
            likeCount = 150,
            commentCount = 20,
            retweetCount = 5,
            isVerified = true,
        ),
        Tweet(
            id = 20,
            username = "Dev Humor",
            handle = "devhumor",
            content = "How to explain programming to non-programmers:\nMe: It's like giving very precise instructions to a very fast but extremely literal alien.",
            timestamp = "4h",
            likeCount = 9876,
            retweetCount = 2345,
            commentCount = 765,
        ),
        Tweet(
            id = 30,
            username = "Dev Humor",
            handle = "devhumor",
            content = "How to explain programming to non-programmers:\nMe: It's like giving very precise instructions to a very fast but extremely literal alien.",
            timestamp = "4h",
            likeCount = 9876,
            retweetCount = 2345,
            commentCount = 765,
        ),
        Tweet(
            id = 40,
            username = "Dev Humor",
            handle = "devhumor",
            content = "How to explain programming to non-programmers:\nMe: It's like giving very precise instructions to a very fast but extremely literal alien.",
            timestamp = "4h",
            likeCount = 9876,
            retweetCount = 2345,
            commentCount = 765,
        ),
        Tweet(
            id = 50,
            username = "Dev Humor",
            handle = "devhumor",
            content = "How to explain programming to non-programmers:\nMe: It's like giving very precise instructions to a very fast but extremely literal alien.",
            timestamp = "4h",
            likeCount = 9876,
            retweetCount = 2345,
            commentCount = 765,
        ),
        Tweet(
            id = 60,
            username = "Dev Humor",
            handle = "devhumor",
            content = "How to explain programming to non-programmers:\nMe: It's like giving very precise instructions to a very fast but extremely literal alien.",
            timestamp = "4h",
            likeCount = 9876,
            retweetCount = 2345,
            commentCount = 765,
        ),
    )

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    logOutSuccess: () -> Unit,
    onClickCreatePost: () -> Unit = {},
    onClickSetting: () -> Unit = {},
    onClickProfile: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.checkProfileCompletion()
    }

    if (uiState.userProfileNotCompleted) {
        AlertDialog(
            onDismissRequest = {
            },
            title = { Text("Complete Your Profile") },
            text = { Text("Please complete your profile to continue enjoying the timeline experience.") },
            confirmButton = {
                TextButton(onClick = onClickProfile) {
                    Text("Go to Profile")
                }
            },
            dismissButton = null,
        )
    }

    TwitterTimelineScreen(
        uiState = uiState,
        onClickCreatePost = {
            viewModel.logOut {
                logOutSuccess()
            }
        },
        onClickSettings = onClickSetting,
        onClickProfile = onClickProfile,
    )
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

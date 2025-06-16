package com.nyinyi.quickfeed.ui.screen.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyinyi.domain_model.Post
import com.nyinyi.quickfeed.ui.components.CircleProfileIcon
import com.nyinyi.quickfeed.ui.components.FormattedTweetText
import com.nyinyi.quickfeed.ui.components.ShadowImageCard
import com.nyinyi.quickfeed.ui.components.SimpleCircleProfileIcon
import com.nyinyi.quickfeed.ui.utils.toReadableTimestamp

@Composable
fun ModernTweetCard(
    tweet: Post,
    index: Int,
    onClickLike: (Boolean) -> Unit = {},
) {
    var isLiked by remember { mutableStateOf(tweet.isLiked) }
    var likeCount by remember { mutableStateOf(tweet.likeCount) }
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { /* Navigate to tweet detail */ },
                ),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color.Transparent,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp,
                pressedElevation = 8.dp,
            ),
        border =
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (tweet.authorProfilePictureUrl != null) {
                    CircleProfileIcon(
                        imageUrl = tweet.authorProfilePictureUrl,
                        placeholderIcon = Icons.Default.Person,
                        size = 52.dp,
                    )
                } else {
                    SimpleCircleProfileIcon(
                        icon = Icons.Default.Person,
                        size = 52.dp,
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        iconTint = Color.White,
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tweet.authorUsername,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp),
                    ) {
                        Text(
                            text = "@${tweet.authorHandle}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier =
                                Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.4f,
                                        ),
                                    ),
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        val readableTime =
                            toReadableTimestamp(tweet.timestamp)

                        Text(
                            text = readableTime,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Surface(
                    onClick = { /* Show menu */ },
                    shape = CircleShape,
                    color = Color.Transparent,
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FormattedTweetText(
                content = tweet.content,
                modifier = Modifier.fillMaxWidth(),
            )

            tweet.imageUrl?.let { imageUrl ->
                Spacer(modifier = Modifier.height(16.dp))
                ShadowImageCard(
                    imageUrl = imageUrl,
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 24.dp,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                val likeColor by animateColorAsState(
                    targetValue = if (isLiked) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "Like Color",
                )

                ModernActionButton(
                    icon = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    count = likeCount,
                    color = likeColor,
                    onClick = {
                        isLiked = !isLiked
                        likeCount += if (isLiked) 1 else -1
                        onClickLike(isLiked)
                    },
                )

                ModernActionButton(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    count = tweet.commentCount,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { /* Handle reply */ },
                )

                ModernActionButton(
                    icon = Icons.Outlined.Repeat,
                    count = 0,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { /* Handle retweet */ },
                )

                ModernActionButton(
                    icon = Icons.Outlined.Share,
                    count = null,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { /* Handle share */ },
                )
            }
        }
    }
}

@Composable
private fun ModernActionButton(
    icon: ImageVector,
    count: Int?,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "Button Scale",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .scale(scale)
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    onClick = onClick,
                ).padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp),
        )

        count?.let {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = formatCount(it),
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

private fun formatCount(count: Int): String =
    when {
        count < 1000 -> count.toString()
        count < 1000000 ->
            "${
                (count / 1000.0).let {
                    if (it % 1 == 0.0) it.toInt().toString() else "%.1f".format(it)
                }
            }K"

        else ->
            "${
                (count / 1000000.0).let {
                    if (it % 1 == 0.0) it.toInt().toString() else "%.1f".format(it)
                }
            }M"
    }

package com.nyinyi.quickfeed.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

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

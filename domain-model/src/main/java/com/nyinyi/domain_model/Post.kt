package com.nyinyi.domain_model

import androidx.compose.runtime.Immutable

@Immutable
data class Post(
    val id: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = 0,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val authorUid: String = "",
    val authorUsername: String = "",
    val authorHandle: String = "",
    val authorProfilePictureUrl: String? = null,
    val isLiked: Boolean = false,
)

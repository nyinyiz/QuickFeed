package com.nyinyi.domain_model

data class UserProfile(
    val userId: String,
    val username: String,
    val handle: String,
    val email: String,
    val profilePictureUrl: String?,
    val createdAt: Long,
)

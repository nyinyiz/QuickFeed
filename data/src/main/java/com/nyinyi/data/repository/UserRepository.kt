package com.nyinyi.data.repository

import com.nyinyi.domain_model.UserProfile
import java.io.InputStream

interface UserRepository {
    fun getCurrentUserId(): String?

    suspend fun isProfileComplete(): Result<Boolean>

    suspend fun createUserProfile(
        username: String,
        handle: String,
    ): Result<Unit>

    suspend fun getCurrentUserProfile(): Result<UserProfile?>

    suspend fun updateUserProfile(
        newUsername: String,
        newHandle: String,
        newProfilePictureUri: InputStream?,
    ): Result<Unit>
}

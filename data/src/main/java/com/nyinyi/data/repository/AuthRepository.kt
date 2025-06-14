package com.nyinyi.data.repository

interface AuthRepository {
    fun getCurrentUserId(): String?

    fun isLoggedIn(): Boolean

    suspend fun signUp(
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun login(
        email: String,
        password: String,
    ): Result<Unit>

    fun logout()
}

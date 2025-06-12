package com.nyinyi.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val auth: FirebaseAuth,
    ) : AuthRepository {
        override fun getCurrentUserId(): String? = auth.currentUser?.uid

        override fun isLoggedIn(): Boolean = auth.currentUser != null

        override suspend fun signUp(
            email: String,
            password: String,
        ): Result<Unit> =
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                Result.success(Unit)
            } catch (e: FirebaseAuthException) {
                val errorMessage =
                    when (e.errorCode) {
                        "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists"
                        "ERROR_WEAK_PASSWORD" -> "Password is too weak"
                        "ERROR_INVALID_EMAIL" -> "Invalid email address"
                        "ERROR_OPERATION_NOT_ALLOWED" -> "Email/password accounts are not enabled"
                        else -> "Authentication failed: ${e.localizedMessage}"
                    }
                Result.failure(Exception(errorMessage, e))
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun login(
            email: String,
            password: String,
        ): Result<Unit> =
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override fun logout() {
            auth.signOut()
        }
    }

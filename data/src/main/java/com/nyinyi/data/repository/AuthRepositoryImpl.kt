package com.nyinyi.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val firestore: FirebaseFirestore,
    ) : AuthRepository {
        override fun getCurrentUserId(): String? {
            Log.d("", "Current User Id : ${auth.currentUser?.uid}")
            return auth.currentUser?.uid
        }

        override fun isLoggedIn(): Boolean = auth.currentUser != null

        override suspend fun signUp(
            email: String,
            password: String,
        ): Result<Unit> =
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                val userProfile =
                    mapOf(
                        "userId" to auth.currentUser!!.uid,
                        "username" to "",
                        "handle" to "",
                        "email" to email,
                        "profilePictureUrl" to null,
                        "createdAt" to
                            com.google.firebase.firestore.FieldValue
                                .serverTimestamp(),
                    )
                firestore
                    .collection("users")
                    .document(auth.currentUser!!.uid)
                    .set(userProfile)
                    .await()

                Result.success(Unit)
            } catch (e: FirebaseAuthException) {
                Result.failure(Exception(getSignUpErrorMessage(e.errorCode), e))
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
            } catch (e: FirebaseAuthException) {
                Result.failure(Exception(getLoginErrorMessage(e), e))
            } catch (e: Exception) {
                Result.failure(
                    Exception(
                        "Login failed. Please check your connection and try again.",
                        e,
                    ),
                )
            }

        override fun logout() {
            auth.signOut()
        }

        private fun getSignUpErrorMessage(errorCode: String): String =
            when (errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists"
                "ERROR_WEAK_PASSWORD" -> "Password is too weak"
                "ERROR_INVALID_EMAIL" -> "Invalid email address"
                "ERROR_OPERATION_NOT_ALLOWED" -> "Email/password accounts are not enabled"
                else -> "Authentication failed. Please try again."
            }

        private fun getLoginErrorMessage(exception: FirebaseAuthException): String =
            when (exception) {
                is FirebaseAuthInvalidUserException -> "No account found with this email address. Please check your email or sign up."
                is FirebaseAuthInvalidCredentialsException -> "Incorrect password. Please try again."
                else ->
                    when (exception.errorCode) {
                        "ERROR_USER_DISABLED" -> "This account has been disabled"
                        "ERROR_USER_NOT_FOUND" -> "No account found with this email. Please sign up."
                        "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
                        "ERROR_INVALID_EMAIL" -> "The email address is badly formatted"
                        "ERROR_TOO_MANY_REQUESTS" -> "Too many requests. Please try again later."
                        else -> "Login failed. Please try again."
                    }
            }
    }

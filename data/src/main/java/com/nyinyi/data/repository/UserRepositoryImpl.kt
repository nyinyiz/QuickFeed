package com.nyinyi.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.nyinyi.domain_model.UserProfile
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val firestore: FirebaseFirestore,
        private val supabaseStorage: Storage,
    ) : UserRepository {
        private val usersCollection = firestore.collection("users")
        private val profilePicturesBucket = "profile-pictures"

        override fun getCurrentUserId(): String? {
            Log.d("UserRepositoryImpl", "Current User Id : ${auth.currentUser?.uid}")
            return auth.currentUser?.uid
        }

        override suspend fun isProfileComplete(): Result<Boolean> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    FirebaseAuthException(
                        "NO_USER",
                        "No user logged in.",
                    ),
                )
            return try {
                val doc = usersCollection.document(userId).get().await()
                Result.success(doc.exists() && !doc.getString("username").isNullOrBlank())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun createUserProfile(
            username: String,
            handle: String,
        ): Result<Unit> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    FirebaseAuthException(
                        "NO_USER",
                        "No user logged in.",
                    ),
                )
            val userEmail =
                auth.currentUser?.email ?: return Result.failure(
                    FirebaseAuthException(
                        "NO_EMAIL",
                        "User has no email.",
                    ),
                )

            val userProfileMap =
                mapOf(
                    "userId" to userId,
                    "username" to username,
                    "handle" to handle.lowercase(),
                    "email" to userEmail,
                    "profilePictureUrl" to null,
                    "createdAt" to
                        com.google.firebase.firestore.FieldValue
                            .serverTimestamp(),
                )
            return try {
                usersCollection.document(userId).set(userProfileMap).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun getCurrentUserProfile(): Result<UserProfile?> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    FirebaseAuthException(
                        "NO_USER",
                        "No user logged in.",
                    ),
                )

            return try {
                val doc = usersCollection.document(userId).get().await()
                if (doc.exists()) {
                    val profile =
                        UserProfile(
                            userId = doc.getString("userId") ?: "",
                            username = doc.getString("username") ?: "",
                            handle = doc.getString("handle") ?: "",
                            email = doc.getString("email") ?: auth.currentUser?.email ?: "",
                            profilePictureUrl = doc.getString("profilePictureUrl"),
                            createdAt = doc.getTimestamp("createdAt")?.seconds ?: 0L,
                        )
                    Result.success(profile)
                } else {
                    Result.success(null)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun updateUserProfile(
            newUsername: String,
            newHandle: String,
            newProfilePictureInputStream: InputStream?,
        ): Result<Unit> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    FirebaseAuthException(
                        "NO_USER",
                        "No user logged in.",
                    ),
                )

            return try {
                val updates = mutableMapOf<String, Any>()

                if (newProfilePictureInputStream != null) {
                    val profilePictureUrl =
                        uploadProfilePictureToSupabase(
                            userId = userId,
                            inputStream = newProfilePictureInputStream,
                        )
                    updates["profilePictureUrl"] = profilePictureUrl
                }

                updates["username"] = newUsername
                updates["handle"] = newHandle.lowercase()

                usersCollection.document(userId).update(updates).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("UserRepositoryImpl", "Error updating user profile", e)
                Result.failure(e)
            }
        }

        private suspend fun uploadProfilePictureToSupabase(
            userId: String,
            inputStream: InputStream,
        ): String =
            withContext(Dispatchers.IO) {
                try {
                    val fileBytes = inputStream.readBytes()

                    val fileExtension = "jpg"

                    val timestamp = System.currentTimeMillis()
                    val filePath = "user_profiles/$userId/profile_$timestamp.$fileExtension"

                    Log.d("UserRepositoryImpl", "Uploading file to path: $filePath")
                    Log.d("UserRepositoryImpl", "File size: ${fileBytes.size} bytes")

                    val bucket = supabaseStorage.from(profilePicturesBucket)

                    bucket.upload(
                        path = filePath,
                        data = fileBytes,
                    )

                    Log.d("UserRepositoryImpl", "Successfully uploaded to Supabase Storage")

                    val publicUrl = bucket.publicUrl(filePath)
                    Log.d("UserRepositoryImpl", "Public URL: $publicUrl")

                    publicUrl
                } catch (e: Exception) {
                    Log.e("UserRepositoryImpl", "Error uploading to Supabase Storage", e)
                    throw e
                }
            }
    }

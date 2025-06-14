package com.nyinyi.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nyinyi.domain_model.UserProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val firestore: FirebaseFirestore,
        private val storage: FirebaseStorage,
    ) : UserRepository {
        private val usersCollection = firestore.collection("users")

        override fun getCurrentUserId(): String? {
            Log.d("", "Current User Id : ${auth.currentUser?.uid}")
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
                        "",
                        "No user logged in.",
                    ),
                )
            val userEmail =
                auth.currentUser?.email ?: return Result.failure(
                    FirebaseAuthException(
                        "",
                        " User has no email.",
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
                        "",
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
            newProfilePictureUri: Uri?,
        ): Result<Unit> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    FirebaseAuthException(
                        "",
                        "No user logged in.",
                    ),
                )

            return try {
                val updates = mutableMapOf<String, Any>()

                if (newProfilePictureUri != null) {
                    val storageRef = storage.reference.child("profile_pictures/$userId")

                    storageRef.putFile(newProfilePictureUri).await()

                    val downloadUrl = storageRef.downloadUrl.await().toString()
                    updates["profilePictureUrl"] = downloadUrl
                }

                updates["username"] = newUsername
                updates["handle"] = newHandle

                usersCollection.document(userId).update(updates).await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

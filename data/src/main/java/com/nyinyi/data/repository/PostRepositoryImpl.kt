package com.nyinyi.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nyinyi.domain_model.Post
import com.nyinyi.domain_model.UserProfile
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.InputStream
import javax.inject.Inject

class PostRepositoryImpl
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val firestore: FirebaseFirestore,
        private val supabaseStorage: Storage,
    ) : PostRepository {
        private val usersCollection = firestore.collection("users")
        private val postsCollection = firestore.collection("posts")
        private val timeLinePostBucket = "timeline-post"

        override fun getCurrentUserId(): String? = auth.currentUser?.uid

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
                            likedPosts = doc.get("likedPosts") as? List<String> ?: emptyList(),
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

        override suspend fun createPost(
            text: String,
            imageInputStream: InputStream?,
        ): Result<Unit> =
            try {
                var imageUrl: String? = null
                val postId = postsCollection.document().id

                if (imageInputStream != null) {
                    val postImageUrl = uploadPostImageToSupabase(postId, imageInputStream)
                    imageUrl = postImageUrl
                }

                val postDto =
                    Post(
                        id = postId,
                        content = text,
                        imageUrl = imageUrl,
                        timestamp = Clock.System.now().epochSeconds,
                        likeCount = 0,
                        commentCount = 0,
                        authorUid = getCurrentUserId()!!,
                        authorUsername = getCurrentUserProfile().getOrNull()?.username ?: "",
                        authorHandle = getCurrentUserProfile().getOrNull()?.handle ?: "",
                        authorProfilePictureUrl = getCurrentUserProfile().getOrNull()?.profilePictureUrl,
                        isLiked = false,
                    )

                postsCollection.document(postId).set(postDto).await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun getTimelinePosts(): Flow<Result<List<Post>>> {
            val currentUserId = getCurrentUserId()

            if (currentUserId == null) {
                return flowOf(Result.failure(Exception("No user logged in.")))
            }

            val userFlow =
                callbackFlow {
                    val listener =
                        usersCollection
                            .document(currentUserId)
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) {
                                    trySend(Result.failure(error))
                                } else {
                                    val likedPosts =
                                        (snapshot?.get("likedPosts") as? List<String>) ?: emptyList()
                                    trySend(Result.success(likedPosts))
                                }
                            }
                    awaitClose { listener.remove() }
                }

            val postsFlow =
                callbackFlow {
                    val listener =
                        postsCollection
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) {
                                    trySend(Result.failure(error))
                                } else if (snapshot != null) {
                                    val posts =
                                        snapshot.documents.mapNotNull { doc ->
                                            doc.toObject(Post::class.java)
                                        }
                                    trySend(Result.success(posts))
                                }
                            }
                    awaitClose { listener.remove() }
                }

            return combine(userFlow, postsFlow) { userResult, postsResult ->
                when {
                    userResult.isFailure -> userResult.map { emptyList<Post>() }
                    postsResult.isFailure -> postsResult
                    else -> {
                        val likedPostsIds = userResult.getOrNull() ?: emptyList()
                        val posts = postsResult.getOrNull() ?: emptyList()
                        Result.success(
                            posts.map { post ->
                                post.copy(isLiked = likedPostsIds.contains(post.id))
                            },
                        )
                    }
                }
            }
        }

        override suspend fun likePost(postId: String): Result<Unit> {
            return try {
                withContext(Dispatchers.IO) {
                    val currentUserId = getCurrentUserId() ?: return@withContext
                    firestore.runBatch { batch ->
                        val postRef = postsCollection.document(postId)
                        batch.update(postRef, "likeCount", FieldValue.increment(1))

                        val userRef = usersCollection.document(currentUserId)
                        batch.update(userRef, "likedPosts", FieldValue.arrayUnion(postId))

                        Log.d("unlikePost", "Post liked successfully")
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun unlikePost(postId: String): Result<Unit> {
            return try {
                withContext(Dispatchers.IO) {
                    val currentUserId = getCurrentUserId() ?: return@withContext
                    firestore.runBatch { batch ->
                        val postRef = postsCollection.document(postId)
                        batch.update(postRef, "likeCount", FieldValue.increment(-1))

                        val userRef = usersCollection.document(currentUserId)
                        batch.update(userRef, "likedPosts", FieldValue.arrayRemove(postId))

                        Log.d("unlikePost", "Post unliked successfully")
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun deletePost(post: Post): Result<Unit> {
            val currentUserId =
                getCurrentUserId() ?: return Result.failure(
                    FirebaseAuthException(
                        "NO_USER",
                        "No user logged in.",
                    ),
                )

            if (post.authorUid != currentUserId) {
                return Result.failure(Exception("You are not authorized to delete this post."))
            }

            return try {
                Log.d("deletePost", "Post deleted successfully")
                if (post.imageUrl.isNullOrEmpty().not()) {
                    val path = post.imageUrl!!.substringAfter("$timeLinePostBucket/")
                    if (path.isNotEmpty() && path != post.imageUrl) {
                        supabaseStorage.from(timeLinePostBucket).delete(listOf(path))
                    }
                }

                postsCollection.document(post.id).delete().await()

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("PostRepositoryImpl", "Error deleting post", e)
                Result.failure(e)
            }
        }

        private suspend fun uploadPostImageToSupabase(
            postId: String,
            imageInputStream: InputStream,
        ): String =
            withContext(Dispatchers.IO) {
                try {
                    val fileBytes = imageInputStream.readBytes()
                    val fileExtension = "jpg"

                    val timestamp = System.currentTimeMillis()
                    val filePath = "posts/$postId/post_$timestamp.$fileExtension"

                    val bucket = supabaseStorage.from(timeLinePostBucket)

                    bucket.upload(path = filePath, data = fileBytes)

                    val publicUrl = bucket.publicUrl(filePath)
                    publicUrl
                } catch (e: Exception) {
                    Log.e("UserRepositoryImpl", "Error uploading to Supabase Storage", e)
                    throw e
                }
            }
    }

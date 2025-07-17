package com.nyinyi.data.repository

import com.nyinyi.domain_model.Post
import com.nyinyi.domain_model.UserProfile
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface PostRepository {
    fun getCurrentUserId(): String?

    suspend fun getCurrentUserProfile(): Result<UserProfile?>

    suspend fun createPost(
        text: String,
        imageInputStream: InputStream?,
    ): Result<Unit>

    suspend fun getTimelinePosts(): Flow<Result<List<Post>>>

    suspend fun likePost(postId: String): Result<Unit>

    suspend fun unlikePost(postId: String): Result<Unit>

    suspend fun deletePost(post: Post): Result<Unit>

    suspend fun getPostById(postId: String): Result<Post?>

    suspend fun updatePostById(
        postId: String,
        text: String,
        imageInputStream: InputStream?
    ): Result<Unit>
}

package com.nyinyi.domain.usecase.post

import com.nyinyi.data.repository.PostRepository
import java.io.InputStream
import javax.inject.Inject

class UpdatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(
        postId: String,
        text: String,
        imageInputStream: InputStream?
    ) = repository.updatePostById(postId, text, imageInputStream)
}
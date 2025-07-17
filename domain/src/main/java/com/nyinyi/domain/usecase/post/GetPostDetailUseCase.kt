package com.nyinyi.domain.usecase.post

import com.nyinyi.data.repository.PostRepository
import javax.inject.Inject

class GetPostDetailUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(id: String) = repository.getPostById(id)
}
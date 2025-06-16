package com.nyinyi.domain.usecase.post

import com.nyinyi.data.repository.PostRepository
import javax.inject.Inject

class UnLikeUseCase
    @Inject
    constructor(
        private val repository: PostRepository,
    ) {
        suspend operator fun invoke(postId: String) = repository.unlikePost(postId)
    }

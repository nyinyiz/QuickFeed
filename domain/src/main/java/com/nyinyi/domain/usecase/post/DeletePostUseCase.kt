package com.nyinyi.domain.usecase.post

import com.nyinyi.data.repository.PostRepository
import com.nyinyi.domain_model.Post
import javax.inject.Inject

class DeletePostUseCase
    @Inject
    constructor(
        private val repository: PostRepository,
    ) {
        suspend operator fun invoke(post: Post) = repository.deletePost(post)
    }

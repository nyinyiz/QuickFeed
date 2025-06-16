package com.nyinyi.domain.usecase

import com.nyinyi.data.repository.PostRepository
import java.io.InputStream
import javax.inject.Inject

class CreatePostUseCase
    @Inject
    constructor(
        private val repository: PostRepository,
    ) {
        suspend operator fun invoke(
            text: String,
            imageInputStream: InputStream?,
        ) = repository.createPost(text, imageInputStream)
    }

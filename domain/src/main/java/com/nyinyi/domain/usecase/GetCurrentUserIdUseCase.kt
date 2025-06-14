package com.nyinyi.domain.usecase

import com.nyinyi.data.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserIdUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        suspend operator fun invoke() = repository.getCurrentUserId()
    }

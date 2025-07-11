package com.nyinyi.domain.usecase.user

import com.nyinyi.data.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserProfileUseCase
    @Inject
    constructor(
        private val repository: UserRepository,
    ) {
        suspend operator fun invoke() = repository.getCurrentUserProfile()
    }

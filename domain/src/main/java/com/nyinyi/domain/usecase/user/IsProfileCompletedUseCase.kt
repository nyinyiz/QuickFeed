package com.nyinyi.domain.usecase.user

import com.nyinyi.data.repository.UserRepository
import javax.inject.Inject

class IsProfileCompletedUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke() = userRepository.isProfileComplete()
    }

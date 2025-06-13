package com.nyinyi.domain.usecase

import com.nyinyi.data.repository.AuthRepository
import javax.inject.Inject

class UserSignInUseCase
    @Inject
    constructor(
        private val userRepository: AuthRepository,
    ) {
        suspend operator fun invoke(
            email: String,
            password: String,
        ) = userRepository.login(email, password)
    }

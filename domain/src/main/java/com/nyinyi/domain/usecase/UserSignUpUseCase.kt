package com.nyinyi.domain.usecase

import com.nyinyi.data.repository.AuthRepository
import javax.inject.Inject

class UserSignUpUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        suspend operator fun invoke(
            email: String,
            password: String,
        ) = repository.signUp(email, password)
    }

package com.nyinyi.domain.usecase

import com.nyinyi.data.repository.AuthRepository
import javax.inject.Inject

class IsLoggedInUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        suspend operator fun invoke() = repository.isLoggedIn()
    }

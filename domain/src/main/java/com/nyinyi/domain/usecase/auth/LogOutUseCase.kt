package com.nyinyi.domain.usecase.auth

import com.nyinyi.data.repository.AuthRepository
import javax.inject.Inject

class LogOutUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        suspend operator fun invoke() = repository.logout()
    }

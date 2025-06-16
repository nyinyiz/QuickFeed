package com.nyinyi.domain.usecase

import com.nyinyi.data.repository.UserRepository
import java.io.InputStream
import javax.inject.Inject

class UpdateCurrentUserProfileUseCase
    @Inject
    constructor(
        private val repository: UserRepository,
    ) {
        suspend operator fun invoke(
            newUsername: String,
            newHandle: String,
            newProfilePictureUri: InputStream?,
        ) = repository.updateUserProfile(
            newUsername = newUsername,
            newHandle = newHandle,
            newProfilePictureUri = newProfilePictureUri,
        )
    }

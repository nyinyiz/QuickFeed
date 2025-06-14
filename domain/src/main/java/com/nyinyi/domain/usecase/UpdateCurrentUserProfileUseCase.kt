package com.nyinyi.domain.usecase

import android.net.Uri
import com.nyinyi.data.repository.UserRepository
import javax.inject.Inject

class UpdateCurrentUserProfileUseCase
    @Inject
    constructor(
        private val repository: UserRepository,
    ) {
        suspend operator fun invoke(
            newUsername: String,
            newHandle: String,
            newProfilePictureUri: Uri?,
        ) = repository.updateUserProfile(
            newUsername = newUsername,
            newHandle = newHandle,
            newProfilePictureUri = newProfilePictureUri,
        )
    }

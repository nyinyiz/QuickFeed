package com.nyinyi.quickfeed.ui.screen.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.common.utils.ConnectionObserver
import com.nyinyi.domain.usecase.GetCurrentUserProfileUseCase
import com.nyinyi.domain.usecase.IsProfileCompletedUseCase
import com.nyinyi.domain.usecase.UpdateCurrentUserProfileUseCase
import com.nyinyi.domain_model.UserProfile
import com.nyinyi.quickfeed.provider.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
        private val saveUserProfileUseCase: UpdateCurrentUserProfileUseCase,
        private val isProfileCompleted: IsProfileCompletedUseCase,
        private val connectionObserver: ConnectionObserver,
        private val dispatcherProvider: DispatcherProvider,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(ProfileUiState())
        val uiState = _uiState.asStateFlow()

        private val _event = MutableSharedFlow<ProfileEvent>()
        val event = _event.asSharedFlow()

        init {
            checkProfileCompletion()
            loadUserProfile()
        }

        private fun checkProfileCompletion() {
            viewModelScope.launch(dispatcherProvider.io()) {
                _uiState.update { it.copy(isLoading = true) }
                val result = isProfileCompleted()
                result
                    .onSuccess { profileCompleted ->
                        _uiState.update {
                            it.copy(
                                userProfileNotCompleted = profileCompleted.not(),
                                isLoading = false,
                            )
                        }
                        if (profileCompleted.not()) {
                            loadUserProfile()
                        }
                    }.onFailure {
                        _event.emit(ProfileEvent.SaveFailure(it.message ?: "Unknown error"))
                    }
            }
        }

        private fun loadUserProfile() {
            viewModelScope.launch(dispatcherProvider.io()) {
                Log.d("ProfileViewModel", "loadUserProfile")
                _uiState.update { it.copy(isLoading = true) }
                val result = getCurrentUserProfileUseCase()
                result
                    .onSuccess { profile ->
                        Log.d("ProfileViewModel", "loadUserProfile: $profile")
                        _uiState.update { it.copy(userProfile = profile, isLoading = false) }
                    }.onFailure {
                        _uiState.update { it.copy(isLoading = false, error = it.error) }
                        _event.emit(ProfileEvent.SaveFailure(it.message ?: "Unknown error"))
                    }
            }
        }

        fun onSaveProfile(
            name: String,
            handle: String,
            newProfileImageUri: Uri? = null,
        ) {
            Log.d("ProfileViewModel", "onSaveProfile: $name, $handle, $newProfileImageUri")

            viewModelScope.launch(dispatcherProvider.io()) {
                _uiState.update { it.copy(isSaving = true) }
                val result =
                    saveUserProfileUseCase(
                        newUsername = name,
                        newHandle = handle,
                        newProfilePictureUri = newProfileImageUri,
                    )
                result
                    .onSuccess {
                        _event.emit(ProfileEvent.SaveSuccess)
                        loadUserProfile()
                    }.onFailure { e ->
                        _event.emit(ProfileEvent.SaveFailure(e.message ?: "Failed to save profile."))
                    }
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userProfileNotCompleted: Boolean = false,
    val userProfile: UserProfile? = null,
    val isEditing: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
)

sealed interface ProfileEvent {
    data object SaveSuccess : ProfileEvent

    data class SaveFailure(
        val message: String,
    ) : ProfileEvent
}

package com.nyinyi.quickfeed.ui.screen.createPost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.common.utils.ConnectionObserver
import com.nyinyi.domain.usecase.CreatePostUseCase
import com.nyinyi.domain.usecase.GetCurrentUserIdUseCase
import com.nyinyi.domain.usecase.GetCurrentUserProfileUseCase
import com.nyinyi.domain_model.UserProfile
import com.nyinyi.quickfeed.provider.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel
    @Inject
    constructor(
        private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
        private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
        private val createPostUseCase: CreatePostUseCase,
        private val connectionObserver: ConnectionObserver,
        private val dispatcherProvider: DispatcherProvider,
    ) : ViewModel() {
        private val _state = MutableStateFlow(CreatePostState())
        val state = _state.asStateFlow()

        private val _event = MutableSharedFlow<CreatePostEvent>()
        val event = _event.asSharedFlow()

        init {
            viewModelScope.launch(dispatcherProvider.io()) {
                _state.update { it.copy(isLoading = true) }
                val result = getCurrentUserIdUseCase()
                _state.update { it.copy(isLoading = false, userId = result ?: "") }
            }
            loadUserProfile()
        }

        fun clearError() {
            _state.update { it.copy(error = null) }
        }

        private fun loadUserProfile() {
            viewModelScope.launch(dispatcherProvider.io()) {
                _state.update { it.copy(isLoading = true) }
                val result = getCurrentUserProfileUseCase()
                result
                    .onSuccess { profile ->
                        _state.update { it.copy(userProfile = profile, isLoading = false) }
                    }.onFailure {
                        _event.emit(CreatePostEvent.Error(it.message ?: "Unknown error"))
                    }
            }
        }

        fun createPost(
            postContent: String,
            postImage: InputStream? = null,
        ) {
            viewModelScope.launch(dispatcherProvider.io()) {
                _state.update { it.copy(isLoading = true) }
                createPostUseCase(
                    text = postContent,
                    imageInputStream = postImage,
                ).onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _event.emit(CreatePostEvent.Success)
                }.onFailure { exception ->
                    _state.update { it.copy(isLoading = false) }
                    _event.emit(CreatePostEvent.Error("Failed to create post: ${exception.message}"))
                }
            }
        }
    }

data class CreatePostState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userProfile: UserProfile? = null,
    val userId: String = "",
)

sealed class CreatePostEvent {
    data class Error(
        val message: String,
    ) : CreatePostEvent()

    object Success : CreatePostEvent()
}

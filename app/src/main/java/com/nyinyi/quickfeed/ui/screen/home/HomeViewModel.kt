package com.nyinyi.quickfeed.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.common.utils.ConnectionObserver
import com.nyinyi.domain.usecase.CreatePostUseCase
import com.nyinyi.domain.usecase.GetCurrentUserIdUseCase
import com.nyinyi.domain.usecase.GetCurrentUserProfileUseCase
import com.nyinyi.domain.usecase.GetTimeLinePostUseCase
import com.nyinyi.domain.usecase.IsProfileCompletedUseCase
import com.nyinyi.domain.usecase.LogOutUseCase
import com.nyinyi.domain_model.Post
import com.nyinyi.domain_model.UserProfile
import com.nyinyi.quickfeed.provider.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val logOutUseCase: LogOutUseCase,
        private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
        private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
        private val isProfileCompleted: IsProfileCompletedUseCase,
        private val createPostUseCase: CreatePostUseCase,
        private val getTimeLinePostUseCase: GetTimeLinePostUseCase,
        private val connectionObserver: ConnectionObserver,
        private val dispatcherProvider: DispatcherProvider,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(HomeUiState())
        val uiState = _uiState.asStateFlow()

        private val _event = MutableSharedFlow<HomeEvent>()
        val event = _event.asSharedFlow()

        init {
            viewModelScope.launch(dispatcherProvider.io()) {
                _uiState.update { it.copy(isLoading = true) }
                val result = getCurrentUserIdUseCase()
                _uiState.update { it.copy(isLoading = false, userId = result ?: "") }
            }
            checkProfileCompletion()
        }

        fun clearError() {
            _uiState.update { it.copy(errorMessage = null) }
        }

        fun logOut() {
            viewModelScope.launch(dispatcherProvider.main()) {
                logOutUseCase()
                _event.emit(HomeEvent.LogOutSuccess)
            }
        }

        fun checkProfileCompletion() {
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
                        if (profileCompleted) {
                            loadUserProfile()
                        }
                    }.onFailure {
                        _event.emit(HomeEvent.Error(it.message ?: "Unknown error"))
                    }
            }
        }

        private fun loadUserProfile() {
            viewModelScope.launch(dispatcherProvider.io()) {
                _uiState.update { it.copy(isLoading = true) }
                val result = getCurrentUserProfileUseCase()
                result
                    .onSuccess { profile ->
                        _uiState.update { it.copy(userProfile = profile, isLoading = false) }
                    }.onFailure {
                        _event.emit(HomeEvent.Error(it.message ?: "Unknown error"))
                    }
            }
        }

        fun loadTimelinePosts() {
            viewModelScope.launch(dispatcherProvider.io()) {
                getTimeLinePostUseCase()
                    .onStart {
                        _uiState.update { it.copy(isTimelineLoading = true) }
                    }.catch {
                        _uiState.update {
                            it.copy(
                                isTimelineLoading = false,
                                errorMessage = it.errorMessage,
                            )
                        }
                        _event.emit(
                            HomeEvent.Error(
                                "Failed to load timeline: " + (it.message ?: "Unknown error"),
                            ),
                        )
                    }.collectLatest { result ->
                        result
                            .onSuccess { posts ->
                                _uiState.update {
                                    it.copy(
                                        timelinePosts = posts,
                                        isTimelineLoading = false,
                                    )
                                }
                            }.onFailure { exception ->
                                _uiState.update { it.copy(isTimelineLoading = false) }
                                _event.emit(HomeEvent.Error("Failed to load timeline data: ${exception.message}"))
                            }
                    }
            }
        }

        fun createPost(
            postContent: String,
            postImage: InputStream? = null,
        ) {
            viewModelScope.launch(dispatcherProvider.io()) {
                _uiState.update { it.copy(isCreatePost = true) }
                createPostUseCase(
                    text = postContent,
                    imageInputStream = postImage,
                ).onSuccess {
                    _uiState.update { it.copy(isCreatePost = false) }
                    _event.emit(HomeEvent.PostCreationSuccess)
                    loadTimelinePosts()
                }.onFailure { exception ->
                    _uiState.update { it.copy(isCreatePost = false) }
                    _event.emit(HomeEvent.Error("Failed to create post: ${exception.message}"))
                }
            }
        }
    }

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userProfile: UserProfile? = null,
    val userProfileNotCompleted: Boolean = false,
    val userId: String = "",
    val isTimelineLoading: Boolean = false,
    val timelinePosts: List<Post> = emptyList(),
    val isCreatePost: Boolean = false,
)

sealed class HomeEvent {
    data class Error(
        val errorMessage: String,
    ) : HomeEvent()

    object LogOutSuccess : HomeEvent()

    object PostCreationSuccess : HomeEvent()
}

package com.nyinyi.quickfeed.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.common.utils.ConnectionObserver
import com.nyinyi.domain.usecase.GetCurrentUserIdUseCase
import com.nyinyi.domain.usecase.IsProfileCompletedUseCase
import com.nyinyi.domain.usecase.LogOutUseCase
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
class HomeViewModel
    @Inject
    constructor(
        private val logOutUseCase: LogOutUseCase,
        private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
        private val isProfileCompleted: IsProfileCompletedUseCase,
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

        fun logOut(onSuccess: () -> Unit) {
            viewModelScope.launch(dispatcherProvider.main()) {
                logOutUseCase()
                onSuccess()
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
                    }.onFailure {
                        _event.emit(HomeEvent.Error(it.message ?: "Unknown error"))
                    }
            }
        }
    }

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userProfileNotCompleted: Boolean = false,
    val userId: String = "",
)

sealed class HomeEvent {
    data class Error(
        val errorMessage: String,
    ) : HomeEvent()
}

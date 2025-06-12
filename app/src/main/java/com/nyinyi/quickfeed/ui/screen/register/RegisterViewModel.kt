package com.nyinyi.quickfeed.ui.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.common.utils.ConnectionObserver
import com.nyinyi.domain.usecase.UserSignUpUseCase
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
class RegisterViewModel
    @Inject
    constructor(
        private val userSignUpUseCase: UserSignUpUseCase,
        private val connectionObserver: ConnectionObserver,
        private val dispatcherProvider: DispatcherProvider,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(RegisterState())
        val uiState = _uiState.asStateFlow()

        private val _authResult = MutableSharedFlow<RegisterResultEvent>()
        val authResult = _authResult.asSharedFlow()

        init {
            with(connectionObserver) {
                onConnected = {
                }
                onDisconnected = {
                }
                startObserving()
            }
        }

        fun onSignUpClicked(
            email: String,
            password: String,
            confirmPassword: String,
        ) {
            if (email.isBlank() || password.isBlank()) {
                _uiState.update { it.copy(error = "All fields are required.") }
                return
            }
            if (password != confirmPassword) {
                _uiState.update { it.copy(error = "Passwords do not match.") }
                return
            }
            if (password.length < 6) {
                _uiState.update { it.copy(error = "Password must be at least 6 characters.") }
                return
            }

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val result = userSignUpUseCase(email, password)

                result
                    .onSuccess {
                        _authResult.emit(RegisterResultEvent.Success)
                    }.onFailure { exception ->
                        _authResult.emit(
                            RegisterResultEvent.Failure(
                                exception.message ?: "An unknown error occurred.",
                            ),
                        )
                    }

                _uiState.update { it.copy(isLoading = false) }
            }
        }

        fun clearError() {
            _uiState.update { it.copy(error = null) }
        }

        override fun onCleared() {
            super.onCleared()
            connectionObserver.stopObserving()
        }
    }

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface RegisterResultEvent {
    data object Success : RegisterResultEvent

    data class Failure(
        val message: String,
    ) : RegisterResultEvent
}

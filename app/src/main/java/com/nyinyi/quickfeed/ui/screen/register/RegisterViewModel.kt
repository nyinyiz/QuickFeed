package com.nyinyi.quickfeed.ui.screen.register

import android.util.Patterns
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
            val trimmedEmail = email.trim()
            val trimmedPassword = password.trim()
            val trimmedConfirmPassword = confirmPassword.trim()

            if (trimmedEmail.isBlank() || trimmedPassword.isBlank() || trimmedConfirmPassword.isBlank()) { // Also check confirmPassword for blank
                // It's better to specify which field is blank if possible, or a general message
                _uiState.update { it.copy(error = "All fields are required.", errorType = ErrorType.EMAIL) } // Or a new ErrorType.GENERAL_FIELD
                return
            }

            // Email Validation
            if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                _uiState.update { it.copy(error = "Invalid email address format.", errorType = ErrorType.EMAIL) }
                return
            }

            // Password length check (ensure this comes before checking if passwords match if length is a primary concern)
            if (trimmedPassword.length < 6) {
                _uiState.update { it.copy(error = "Password must be at least 6 characters.", errorType = ErrorType.PASSWORD) }
                return
            }

            // Password confirmation check
            if (trimmedPassword != trimmedConfirmPassword) {
                _uiState.update { it.copy(error = "Passwords do not match.", errorType = ErrorType.CONFIRM_PASSWORD) }
                return
            }

            viewModelScope.launch(dispatcherProvider.io()) {
                // Use IO dispatcher for network calls
                _uiState.update { it.copy(isLoading = true, error = null, errorType = ErrorType.NONE) } // Clear previous errors

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
            _uiState.update { it.copy(error = null, errorType = ErrorType.NONE) }
        }

        override fun onCleared() {
            super.onCleared()
            connectionObserver.stopObserving()
        }
    }

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorType: ErrorType = ErrorType.NONE,
)

enum class ErrorType {
    NONE,
    EMAIL,
    PASSWORD,
    CONFIRM_PASSWORD,
    AUTH,
}

sealed interface RegisterResultEvent {
    data object Success : RegisterResultEvent

    data class Failure(
        val message: String,
    ) : RegisterResultEvent
}

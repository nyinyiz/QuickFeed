package com.nyinyi.quickfeed.ui.screen.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.common.utils.ConnectionObserver
import com.nyinyi.domain.usecase.auth.UserSignInUseCase
import com.nyinyi.quickfeed.provider.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val userSignInUseCase: UserSignInUseCase,
        private val connectionObserver: ConnectionObserver,
        private val dispatcherProvider: DispatcherProvider,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LoginUiState())
        val uiState = _uiState.asStateFlow()

        private val _uiEvent = MutableStateFlow<LoginUiEvent?>(null)
        val uiEvent = _uiEvent.asStateFlow()

        init {
            with(connectionObserver) {
                onConnected = {
                }
                onDisconnected = {
                }
                startObserving()
            }
        }

        fun onSignIn(
            email: String,
            password: String,
        ) {
            val trimmedEmail = email.trim()
            val trimmedPassword = password.trim()

            if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
                _uiState.update { it.copy(errorMessage = "All fields are required.") }
                return
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Invalid email address format.",
                    )
                }
                return
            }

            viewModelScope.launch(dispatcherProvider.io()) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val result = userSignInUseCase(email, password)

                result
                    .onSuccess {
                        _uiEvent.emit(LoginUiEvent.LoginSuccess)
                    }.onFailure {
                        _uiEvent.emit(LoginUiEvent.LoginError(it.message ?: "An error occurred"))
                    }

                _uiState.update { it.copy(isLoading = false) }
            }
        }

        fun clearError() {
            _uiState.update { it.copy(errorMessage = null) }
        }

        override fun onCleared() {
            super.onCleared()
            connectionObserver.stopObserving()
        }
    }

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LoginUiEvent {
    data object LoginSuccess : LoginUiEvent

    data class LoginError(
        val message: String,
    ) : LoginUiEvent
}

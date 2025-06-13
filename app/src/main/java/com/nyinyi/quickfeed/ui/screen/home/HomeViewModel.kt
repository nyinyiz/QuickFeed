package com.nyinyi.quickfeed.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.common.utils.ConnectionObserver
import com.nyinyi.domain.usecase.GetCurrentUserIdUseCase
import com.nyinyi.domain.usecase.LogOutUseCase
import com.nyinyi.quickfeed.provider.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
        private val connectionObserver: ConnectionObserver,
        private val dispatcherProvider: DispatcherProvider,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(HomeUiState())
        val uiState = _uiState.asStateFlow()

        init {
            viewModelScope.launch(dispatcherProvider.io()) {
                _uiState.update { it.copy(isLoading = true) }
                val result = getCurrentUserIdUseCase()
                _uiState.update { it.copy(isLoading = false, userId = result ?: "") }
            }
        }

        fun logOut(onSuccess: () -> Unit) {
            viewModelScope.launch(dispatcherProvider.main()) {
                logOutUseCase()
                onSuccess()
            }
        }
    }

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userId: String = "",
)

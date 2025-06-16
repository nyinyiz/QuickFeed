package com.nyinyi.quickfeed.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.common.utils.ConnectionObserver
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
class SettingViewModel
    @Inject
    constructor(
        private val logOutUseCase: LogOutUseCase,
        private val connectionObserver: ConnectionObserver,
        private val dispatcherProvider: DispatcherProvider,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SettingUiState())
        val uiState = _uiState.asStateFlow()

        private val _event = MutableSharedFlow<SettingUiEvent>()
        val event = _event.asSharedFlow()

        fun logOut() {
            viewModelScope.launch(dispatcherProvider.main()) {
                logOutUseCase()
                _event.emit(SettingUiEvent.LogOutSuccess)
            }
        }

        fun clearError() {
            _uiState.update { it.copy(error = null) }
        }
    }

data class SettingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class SettingUiEvent {
    object LogOutSuccess : SettingUiEvent()

    data class Error(
        val message: String,
    ) : SettingUiEvent()
}

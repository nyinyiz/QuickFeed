package com.nyinyi.quickfeed.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.common.utils.ConnectionObserver
import com.nyinyi.domain.usecase.auth.LogOutUseCase
import com.nyinyi.quickfeed.BuildConfig
import com.nyinyi.quickfeed.provider.DispatcherProvider
import com.nyinyi.quickfeed.provider.ThemePreferenceManager
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
        private val themePreferenceManager: ThemePreferenceManager,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SettingUiState())
        val uiState = _uiState.asStateFlow()

        private val _event = MutableSharedFlow<SettingUiEvent>()
        val event = _event.asSharedFlow()

        init {
            getAppVersion()
            getDarkModeStatus()
        }

        fun getDarkModeStatus() {
            viewModelScope.launch(dispatcherProvider.io()) {
                val isDarkMode = themePreferenceManager.getDarkModeStatus()
                _uiState.update { it.copy(isDarkMode = isDarkMode) }
            }
        }

        fun getAppVersion() {
            _uiState.update { it.copy(appVersion = BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")") }
        }

        fun logOut() {
            viewModelScope.launch(dispatcherProvider.main()) {
                logOutUseCase()
                _event.emit(SettingUiEvent.LogOutSuccess)
            }
        }

        fun clearError() {
            _uiState.update { it.copy(error = null) }
        }

        fun changeTheme(isDarkMode: Boolean) {
            viewModelScope.launch(dispatcherProvider.io()) {
                _uiState.update { it.copy(isDarkMode = isDarkMode) }
                themePreferenceManager.saveDarkModeStatus(isDarkMode)
            }
        }
    }

data class SettingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val appVersion: String = "",
    val isDarkMode: Boolean = false,
)

sealed class SettingUiEvent {
    object LogOutSuccess : SettingUiEvent()

    data class Error(
        val message: String,
    ) : SettingUiEvent()
}

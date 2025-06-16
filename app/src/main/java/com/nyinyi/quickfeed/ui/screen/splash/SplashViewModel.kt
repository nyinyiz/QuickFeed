package com.nyinyi.quickfeed.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinyi.common.utils.ConnectionObserver
import com.nyinyi.domain.usecase.auth.IsLoggedInUseCase
import com.nyinyi.quickfeed.provider.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashDestination {
    object WelcomeScreen : SplashDestination()

    object HomeScreen : SplashDestination()

    object Undefined : SplashDestination()
}

sealed class SplashState {
    object Loading : SplashState()

    object Connected : SplashState()

    object Disconnected : SplashState()
}

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val isLoggedInUseCase: IsLoggedInUseCase,
        private val connectionObserver: ConnectionObserver,
        private val dispatcherProvider: DispatcherProvider,
    ) : ViewModel() {
        private val _hasInternetAccess = MutableStateFlow(false)
        val hasInternetAccess: StateFlow<Boolean> = _hasInternetAccess.asStateFlow()

        private val _navigateTo = MutableStateFlow<SplashDestination>(SplashDestination.Undefined)
        val navigateTo = _navigateTo.asStateFlow()

        private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
        val state = _state.asStateFlow()

        init {
            connectionObserver.onConnected = { hasInternet ->
                _hasInternetAccess.value = hasInternet
                if (hasInternet) {
                    _state.value = SplashState.Connected
                    checkLoginAndDecideRoute()
                } else {
                    _state.value = SplashState.Disconnected
                }
            }

            connectionObserver.onDisconnected = {
                _hasInternetAccess.value = false
                _state.value = SplashState.Disconnected
            }

            connectionObserver.startObserving()
            _hasInternetAccess.value = connectionObserver.isCurrentlyConnected()
        }

        fun checkLoginAndDecideRoute() {
            viewModelScope.launch(dispatcherProvider.io()) {
                isLoggedInUseCase().let { isLoggedIn ->
                    if (isLoggedIn) {
                        _navigateTo.value = SplashDestination.HomeScreen
                    } else {
                        _navigateTo.value = SplashDestination.WelcomeScreen
                    }
                }
            }
        }

        override fun onCleared() {
            super.onCleared()
            connectionObserver.stopObserving()
        }
    }

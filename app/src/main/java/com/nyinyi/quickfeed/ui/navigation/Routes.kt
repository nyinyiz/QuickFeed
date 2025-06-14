package com.nyinyi.quickfeed.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object WelcomeScreen : Routes

    @Serializable
    data object SplashScreen : Routes

    @Serializable
    data object LoginScreen : Routes

    @Serializable
    data object RegisterScreen : Routes

    @Serializable
    data object HomeScreen : Routes

    @Serializable
    data object ProfileScreen : Routes

    @Serializable
    data object SettingsScreen : Routes
}

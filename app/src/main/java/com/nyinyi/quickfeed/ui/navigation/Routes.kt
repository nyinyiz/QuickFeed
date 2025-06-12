package com.nyinyi.quickfeed.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object SplashScreen : Routes

    @Serializable
    data object LoginScreen : Routes

    @Serializable
    data object RegisterScreen : Routes

    @Serializable
    data object TimeLineScreen : Routes

    @Serializable
    data object ProfileScreen : Routes
}

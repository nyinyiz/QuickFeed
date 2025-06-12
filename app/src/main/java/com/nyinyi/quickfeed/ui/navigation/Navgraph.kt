package com.nyinyi.quickfeed.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nyinyi.quickfeed.ui.screen.login.LoginScreen
import com.nyinyi.quickfeed.ui.screen.splash.SplashScreen
import com.nyinyi.quickfeed.ui.screen.welcome.WelcomeScreen

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    onToggleTheme: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SplashScreen,
    ) {
        composable<Routes.SplashScreen> {
            SplashScreen {
                navController.navigate(Routes.WelcomeScreen)
            }
        }

        composable<Routes.WelcomeScreen> {
            WelcomeScreen(
                onToggleTheme = onToggleTheme,
            )
        }

        composable<Routes.LoginScreen> {
            LoginScreen()
        }
    }
}

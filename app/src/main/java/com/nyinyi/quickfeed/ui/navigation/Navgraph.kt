package com.nyinyi.quickfeed.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.nyinyi.quickfeed.ui.screen.login.LoginScreen
import com.nyinyi.quickfeed.ui.screen.register.RegisterScreen
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
                onSignUpClick = {
                    navController.navigate(
                        route = Routes.RegisterScreen,
                    )
                },
                onSignInClick = {
                    navController.navigate(
                        route = Routes.LoginScreen,
                    )
                },
            )
        }

        composable<Routes.LoginScreen> {
            LoginScreen(
                onLoginClicked = { email, password ->
                    // Handle login logic here
                },
                onNavigateToRegister = {
                    navController.navigate(
                        route = Routes.RegisterScreen,
                        navOptions =
                            navOptions {
                                popUpTo(Routes.WelcomeScreen) {
                                    inclusive = false
                                }
                            },
                    )
                },
                onForgotPassword = {
                    // Handle forgot password logic here
                },
            )
        }

        composable<Routes.RegisterScreen> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(
                        route = Routes.LoginScreen,
                        navOptions =
                            navOptions {
                                popUpTo(Routes.WelcomeScreen) {
                                    inclusive = false
                                }
                            },
                    )
                },
                onNavigateToLogin = {
                    navController.navigate(
                        route = Routes.LoginScreen,
                        navOptions =
                            navOptions {
                                popUpTo(Routes.WelcomeScreen) {
                                    inclusive = true
                                }
                            },
                    )
                },
            )
        }
    }
}

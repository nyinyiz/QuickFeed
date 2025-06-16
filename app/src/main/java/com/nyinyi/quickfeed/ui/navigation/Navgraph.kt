package com.nyinyi.quickfeed.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.nyinyi.quickfeed.ui.screen.createPost.CreatePostScreen
import com.nyinyi.quickfeed.ui.screen.home.HomeScreen
import com.nyinyi.quickfeed.ui.screen.login.LoginScreen
import com.nyinyi.quickfeed.ui.screen.profile.ProfileScreen
import com.nyinyi.quickfeed.ui.screen.register.RegisterScreen
import com.nyinyi.quickfeed.ui.screen.setting.SettingScreen
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
            SplashScreen(
                onNavigationToWelcome = {
                    navController.navigate(
                        route = Routes.WelcomeScreen,
                        navOptions =
                            navOptions {
                                popUpTo(Routes.SplashScreen) {
                                    inclusive = false
                                }
                            },
                    )
                },
                onNavigationToHome = {
                    navController.navigate(
                        route = Routes.HomeScreen,
                        navOptions =
                            navOptions {
                                popUpTo(Routes.SplashScreen) {
                                    inclusive = false
                                }
                            },
                    )
                },
            )
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
                onLoginSuccess = {
                    navController.navigate(
                        route = Routes.HomeScreen,
                        navOptions =
                            navOptions {
                                popUpTo(Routes.WelcomeScreen) {
                                    inclusive = true
                                }
                            },
                    )
                },
                backPressed = {
                    navController.popBackStack()
                },
                onNavigateToRegister = {
                    navController.navigate(
                        route = Routes.RegisterScreen,
                        navOptions =
                            navOptions {
                                popUpTo(Routes.WelcomeScreen) {
                                    inclusive = true
                                }
                            },
                    )
                },
                onForgotPassword = {
                },
            )
        }

        composable<Routes.RegisterScreen> {
            RegisterScreen(
                backPressed = {
                    navController.popBackStack()
                },
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

        composable<Routes.HomeScreen> {
            HomeScreen(
                logOutSuccess = {
                    navController.navigate(
                        route = Routes.WelcomeScreen,
                        navOptions =
                            navOptions {
                                popUpTo(Routes.HomeScreen) {
                                    inclusive = true
                                }
                            },
                    )
                },
                onClickSetting = {
                    navController.navigate(
                        route = Routes.SettingsScreen,
                    )
                },
                onClickProfile = {
                    navController.navigate(
                        route = Routes.ProfileScreen,
                    )
                },
                onClickCreatePost = {
                    navController.navigate(
                        route = Routes.CreatePostScreen,
                    )
                },
            )
        }

        composable<Routes.SettingsScreen> {
            SettingScreen(
                onThemeChange = { themeSetting ->
                    onToggleTheme()
                },
                logOutSuccess = {
                    navController.navigate(
                        route = Routes.WelcomeScreen,
                        navOptions =
                            navOptions {
                                popUpTo(Routes.SettingsScreen) {
                                    inclusive = true
                                }
                            },
                    )
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<Routes.ProfileScreen> {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<Routes.CreatePostScreen> {
            CreatePostScreen(
                onBackPress = {
                    navController.popBackStack()
                },
            )
        }
    }
}

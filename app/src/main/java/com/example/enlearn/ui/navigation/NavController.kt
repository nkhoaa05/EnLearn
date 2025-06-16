package com.example.enlearn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.enlearn.auth.AuthStateManager
import com.example.enlearn.presentation.home.MainScreen
import com.example.enlearn.ui.screen.MultipleChoiceQuestion.LessonCompletedScreen
import com.example.enlearn.ui.screen.MultipleChoiceQuestion.MultipleChoiceScreen
import com.example.enlearn.ui.screen.intro.OnboardingScreen1
import com.example.enlearn.ui.screen.intro.OnboardingScreen2
import com.example.enlearn.ui.screen.intro.OnboardingScreen3
import com.example.enlearn.ui.screen.login.LoginScreen
import com.example.enlearn.ui.screen.login.SignUpScreen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) "home" else "onboarding1"
    AuthStateManager(navController)

    NavHost(navController = navController, startDestination = startDestination) {
        // On boarding
        composable("onboarding1") {
            OnboardingScreen1(
                onNextClicked = {
                    navController.navigate("onboarding2")
                },
                onLoginClicked = {
                    navController.navigate("login")
                },
                navController
            )
        }
        composable("onboarding2") {
            OnboardingScreen2(
                onNextClicked = {
                    navController.navigate("onboarding3")
                },
                onLoginClicked = {
                    navController.navigate("login")
                },
                navController
            )
        }
        composable("onboarding3") {
            OnboardingScreen3(
                onNextClicked = {
                    navController.navigate("login")
                },
                onLoginClicked = { navController.navigate("login") },
                navController
            )
        }

        //Navigation MutipleChoiceQuestion
        composable("lesson") {
            MultipleChoiceScreen(
                onNavigateToCompleted = {
                    navController.navigate("completed") {
                        popUpTo("lesson") { inclusive = true }
                    }
                },
                onBack = {
                    // navController.popBackStack()
                }
            )
        }
        composable("completed") {
            LessonCompletedScreen(
                onBackToHome = {
                    // Xử lý khi nhấn back to home
                    // navController.navigate("home") { popUpTo... }
                }
            )
        }
        // Login
        composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home")
                        {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    navController
                )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                navController
            )
        }

        // Main
        composable("home") {
            MainScreen()
        }
    }
}

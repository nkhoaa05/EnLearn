package com.example.enlearn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.enlearn.ui.screen.MultipleChoiceQuestion.LessonCompletedScreen
import com.example.enlearn.ui.screen.MultipleChoiceQuestion.MultipleChoiceScreen
import com.example.enlearn.ui.screen.home.HomeScreen
import com.example.enlearn.ui.screen.intro.OnboardingScreen1
import com.example.enlearn.ui.screen.intro.OnboardingScreen2
import com.example.enlearn.ui.screen.intro.OnboardingScreen3


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "onboarding1") {
        // Splash
//        composable("splash") { SplashScreen(navController) }
        // On boarding
        composable("onboarding1") { OnboardingScreen1(navController) }
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
                onChooseLanguageClicked = {
                    navController.navigate("home")
                },
                onLoginClicked = { navController.navigate("home") },
                navController
            )
        }

        //Navigation MutipleChoiceQuestion
        composable("lesson") {
            MultipleChoiceScreen(
                onNavigateToCompleted = {
                    navController.navigate("completed") {
                        // Xóa màn hình bài học khỏi stack để không quay lại được
                        popUpTo("lesson") { inclusive = true }
                    }
                },
                onBack = {
                    // Xử lý khi nhấn nút back, ví dụ: quay lại màn hình chính
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
        // Main
        composable("home") { HomeScreen() }
    }
}

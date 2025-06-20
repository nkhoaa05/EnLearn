package com.example.enlearn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.enlearn.auth.AuthStateManager
import com.example.enlearn.presentation.home.MainScreen
import com.example.enlearn.ui.ViewModel.MultipleChoice.MultipleChoiceViewModelFactory
import com.example.enlearn.ui.screen.MultipleChoiceQuestion.LessonCompletedScreen
import com.example.enlearn.ui.screen.MultipleChoiceQuestion.MultipleChoiceScreen
import com.example.enlearn.ui.screen.intro.OnboardingScreen1
import com.example.enlearn.ui.screen.intro.OnboardingScreen2
import com.example.enlearn.ui.screen.intro.OnboardingScreen3
import com.example.enlearn.ui.screen.login.LoginScreen
import com.example.enlearn.ui.screen.login.SignUpScreen
import com.example.enlearn.ui.viewmodel.MultipleChoiceViewModel
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
            // 1. Truyền hành động `onLessonClicked` vào MainScreen
            MainScreen(
                onLessonClicked = { chapterId, lessonId ->
                    // 2. Khi hành động này được gọi, thực hiện điều hướng
                    navController.navigate("lesson/$chapterId/$lessonId")
                }
            )
        }

        //Navigation MutipleChoiceQuestion
        composable(
            route = "lesson/{chapterId}/{lessonId}",
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: return@composable
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable

            val viewModel: MultipleChoiceViewModel = viewModel(
                factory = MultipleChoiceViewModelFactory(chapterId, lessonId)
            )

            MultipleChoiceScreen(
                viewModel = viewModel,
                // SỬA Ở ĐÂY: Đặt tên rõ ràng cho các tham số
                onNavigateToCompleted = { score, totalQuestions ->
                    // Bây giờ bạn có thể dùng `score` và `totalQuestions`
                    navController.navigate("completed/$score/$totalQuestions") {
                        popUpTo("home")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Màn hình hoàn thành bài học
        composable(
            route = "completed/{score}/{totalQuestions}",
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("totalQuestions") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val totalQuestions = backStackEntry.arguments?.getInt("totalQuestions") ?: 0
            LessonCompletedScreen(
                score = score,
                totalQuestions = totalQuestions,
                onBackToHome = {
                    navController.popBackStack() // Quay lại màn hình home
                }
            )
        }

    }
}

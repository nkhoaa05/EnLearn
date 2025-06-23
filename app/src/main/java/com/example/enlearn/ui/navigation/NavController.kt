package com.example.enlearn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.enlearn.auth.AuthStateManager
import com.example.enlearn.presentation.home.MainScreen
import com.example.enlearn.ui.ViewModel.MultipleChoice.MultipleChoiceViewModelFactory
import com.example.enlearn.ui.screen.MultipleChoiceQuestion.LearningTipScreen
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
        composable("home") { backStackEntry -> // <-- Phải nhận NavBackStackEntry

            // NHẬN TÍN HIỆU:
            // Lắng nghe giá trị từ SavedStateHandle dưới dạng một State.
            val shouldRefresh by backStackEntry.savedStateHandle.getStateFlow("refresh_home", false).collectAsState()

            MainScreen(
                mainNavController = navController,
            )
        }

        composable(
            route = "learning_tip/{chapterId}/{lessonId}",
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LearningTipScreen(
                onGotItClicked = {
                    // Khi nhấn "Got it!", đi đến màn hình câu hỏi
                    navController.navigate("lesson/$chapterId/$lessonId")
                },
                onBack = { navController.popBackStack() }
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
                onNavigateToCompleted = { score, totalQuestions, lessonTitle ->
                    val encodedTitle = java.net.URLEncoder.encode(lessonTitle, "UTF-8")
                    navController.navigate("completed/$score/$totalQuestions/$encodedTitle") {
                        popUpTo("lesson/$chapterId/$lessonId") {
                            inclusive = true
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Màn hình hoàn thành bài học
        composable(
            route = "completed/{score}/{totalQuestions}/{lessonTitle}",
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("totalQuestions") { type = NavType.IntType },
                navArgument("lessonTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val totalQuestions = backStackEntry.arguments?.getInt("totalQuestions") ?: 0
            val lessonTitle = backStackEntry.arguments?.getString("lessonTitle")?.let {
                java.net.URLDecoder.decode(it, "UTF-8")
            } ?: "the lesson"
            LessonCompletedScreen(
                score = score,
                totalQuestions = totalQuestions,
                lessonTitle = lessonTitle,
                onBackToHome = {
                    // GỬI TÍN HIỆU:
                    // 1. Lấy ra màn hình 'home' từ back stack.
                    // 2. Đặt một giá trị (key="refresh_home", value=true) vào SavedStateHandle của nó.
                    navController.getBackStackEntry("home").savedStateHandle.set("refresh_home", true)

                    // SAU ĐÓ MỚI QUAY LẠI
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }

    }
}

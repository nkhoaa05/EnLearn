package com.example.enlearn.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.enlearn.ui_screen.home.HomeScreen
import com.example.enlearn.ui_screen.intro.OnboardingScreen1
import com.example.enlearn.ui_screen.intro.OnboardingScreen2
import com.example.enlearn.ui_screen.intro.OnboardingScreen3
import com.example.enlearn.ui_screen.intro.SplashScreen


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        // Splash
        composable("splash") { SplashScreen(navController) }
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
        // Main
        composable("home") { HomeScreen(navController) }
    }
}

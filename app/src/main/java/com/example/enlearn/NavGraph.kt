package com.example.enlearn

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
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
        composable("home") { HomeScreen(navController) }
    }
}

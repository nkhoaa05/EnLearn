package com.example.enlearn

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable



@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
//        composable("splash") { SplashScreen(navController) }
//        composable("main") { MainScreen() }
    }
}

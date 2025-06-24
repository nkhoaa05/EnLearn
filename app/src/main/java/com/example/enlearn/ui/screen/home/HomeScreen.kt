package com.example.enlearn.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.enlearn.R
import com.example.enlearn.presentation.profile.ProfileScreen
import com.example.enlearn.ui.screen.home.LessonScreen
import com.example.enlearn.ui.viewModel.HomeListItem
import com.example.enlearn.ui.viewModel.HomeViewModel


// 1. ĐỊNH NGHĨA CÁC MÀN HÌNH CHO BOTTOM NAV
sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavScreen("home_content", "Home", Icons.Default.Home)
    object Task : BottomNavScreen("task_content", "Task", Icons.Default.List)
    object Profile : BottomNavScreen("profile_content", "Profile", Icons.Default.AccountCircle)
}

// 2. COMPOSABLE CHÍNH, SẼ ĐƯỢC GỌI TỪ AppNavGraph
@Composable
fun MainScreen(mainNavController: NavHostController,
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Home.route) {
                // Nội dung của tab Home
                HomeScreenContent(
                    onLessonClicked = { chapterId, lessonId ->
                        mainNavController.navigate("lesson/$chapterId/$lessonId")
                    },

                )
            }
            composable(BottomNavScreen.Task.route) {
                // Nội dung của tab Task
                LessonScreen(
                    onLessonClicked = { chapterId, lessonId ->
                        mainNavController.navigate("lesson/$chapterId/$lessonId")
                    }
                )
            }
            composable(BottomNavScreen.Profile.route) {
                // Nội dung của tab Profile
                ProfileScreen()
            }
        }
    }
}


// 3. TẠO RA BOTTOM NAVIGATION BAR
@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Task,
        BottomNavScreen.Profile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                label = { Text(text = screen.title) },
                icon = { Icon(imageVector = screen.icon, contentDescription = "Navigation Icon") },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun HomeScreenContent(
    onLessonClicked: (chapterId: String, lessonId: String) -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Tự động làm mới dữ liệu khi người dùng quay lại màn hình này
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                homeViewModel.refreshData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Giao diện chính
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item { HomeHeader(userName = uiState.user?.displayName() ?: "Guest") }

            // Lặp qua danh sách item duy nhất
            items(
                items = uiState.homeListItems,

                key = { item ->
                    when (item) {
                        is HomeListItem.ContinueLearningHeader -> "header_continuing"
                        is HomeListItem.AllLessonLearnedHeader -> "header_completed"
                        is HomeListItem.LessonItem -> "lesson-${item.lesson.chapterId}-${item.lesson.id}"
                        is HomeListItem.EmptyState -> "empty-${item.message}"
                    }
                },
                contentType = { item -> item::class.java.simpleName }
            ) { item ->

                when (item) {
                    is HomeListItem.ContinueLearningHeader -> {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeader(title = "Continue Learning")
                    }
                    is HomeListItem.AllLessonLearnedHeader -> {
                        Spacer(modifier = Modifier.height(40.dp))
                        SectionHeader(title = "All Lesson Learned")
                    }
                    is HomeListItem.LessonItem -> {
                        LessonCard(
                            title = item.lesson.title,
                            onClick = { onLessonClicked(item.lesson.chapterId, item.lesson.id) }
                        )
                    }
                    is HomeListItem.EmptyState -> {
                        EmptyStateText(text = item.message)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// Tách Header ra để dễ quản lý
@Composable
private fun HomeHeader(userName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF6A2DEE))
            .padding(horizontal = 24.dp, vertical = 36.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar_placeholder),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(60.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(
                    text = "Hello, $userName",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "What would you like to learn today?",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Composable
private fun LessonCard(title: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF4F4F4))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

// SectionHeader giữ nguyên
@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = "See All", color = Color.Gray, fontSize = 14.sp)
    }
}

// Component để hiển thị khi danh sách rỗng
@Composable
private fun EmptyStateText(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}
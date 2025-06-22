package com.example.enlearn.presentation.home

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
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
// Tên hàm này là `MainScreen` vì nó đại diện cho toàn bộ giao diện chính sau khi đăng nhập.
@Composable
fun MainScreen(mainNavController: NavHostController,
               shouldRefreshHome: Boolean, // Nhận tín hiệu
               onRefreshDone: () -> Unit   // Nhận hành động reset
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
                    shouldRefresh = shouldRefreshHome,
                    onRefreshDone = onRefreshDone
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


// 4. ĐỔI TÊN HÀM `HomeScreen` CŨ THÀNH `HomeScreenContent`
// Nó chỉ là nội dung, không phải toàn bộ màn hình.
@Composable
private fun HomeScreenContent(
    onLessonClicked: (chapterId: String, lessonId: String) -> Unit,
    shouldRefresh: Boolean,
    onRefreshDone: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    // THÊM KHỐI NÀY VÀO ĐÂY
    // --------------------------------------------------
    LaunchedEffect(shouldRefresh) {
        // Khối này sẽ chạy lại mỗi khi giá trị của `shouldRefresh` thay đổi
        if (shouldRefresh) {
            Log.d("HomeScreen", "Refresh signal received, reloading data...")
            // 1. Gọi ViewModel để tải lại dữ liệu từ đầu
            homeViewModel.loadInitialData()

            // 2. Gọi hàm callback để reset tín hiệu,
            // tránh việc tải lại liên tục nếu recomposition xảy ra
            onRefreshDone()
        }
    }
    // --------------------------------------------------

    // Phần giao diện của bạn giữ nguyên, không cần thay đổi
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
            item { HomeHeader(userName = uiState.user?.displayName() ?: "Guest") }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { SectionHeader(title = "Continue Learning") }
            if (uiState.continuingLessons.isEmpty()) {
                item { EmptyStateText(text = "Start a new lesson to see your progress here!") }
            } else {
                items(uiState.continuingLessons) { lesson ->
                    LessonCard(title = lesson.title, onClick = { onLessonClicked(lesson.chapterId, lesson.id) })
                }
            }
            item { Spacer(modifier = Modifier.height(40.dp)) }
            item { SectionHeader(title = "All Lesson Learned") }
            if (uiState.completedLessons.isEmpty()) {
                item { EmptyStateText(text = "No lessons completed yet. Keep going!") }
            } else {
                items(uiState.completedLessons) { lesson ->
                    LessonCard(title = lesson.title, onClick = { onLessonClicked(lesson.chapterId, lesson.id) })
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
            .background(Color(0xFF6A2DEE)) // Màu tím
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

// Sửa lại LessonCard để nhận onClick
@Composable
private fun LessonCard(title: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF4F4F4))
            .clickable(onClick = onClick) // Thêm hành động click
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
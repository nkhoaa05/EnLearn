package com.example.enlearn.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.enlearn.R
import com.example.enlearn.data.model.User
import com.example.enlearn.presentation.profile.ProfileScreen
import com.example.enlearn.ui.components.MainScaffoldWithBottomNav
import com.example.enlearn.ui.screen.home.LessonScreen
import com.example.enlearn.ui.viewModel.ChapterViewModel
import com.example.enlearn.ui.viewModel.LoginViewModel

@Composable
fun HomeScreen() {
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    val userState = viewModel.appUser.observeAsState()
    val user = userState.value ?: User()

    val chapterViewModel: ChapterViewModel = viewModel()
    LaunchedEffect(Unit) {
        chapterViewModel.fetchChapters()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(bottom = 60.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF410FA3)) // Màu tím
                .padding(36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_placeholder),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Column {
                        Text(
                            text = "Hello, ${user.fullName}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "What would you like to learn today?",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Continue Learning
        SectionHeader(title = "Continue Learning")
        Spacer(modifier = Modifier.height(8.dp))
        LessonCard(title = "Lesson 1: Greetings & Introductions")

        Spacer(modifier = Modifier.height(100.dp))

        // Section: All Lesson Learned
        SectionHeader(title = "All Lesson Learned")
        Spacer(modifier = Modifier.height(8.dp))
        LessonCard(title = "Lesson 1: Greetings & Introductions")

        Spacer(modifier = Modifier.height(32.dp))
    }
}


@Composable
fun LessonCard(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF4F4F4)) // Xám nhẹ
            .padding(20.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            color = Color.Black
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "See All", color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun MainScreen(
    // 1. Nhận hành động từ AppNavGraph
    onLessonClicked: (chapterId: String, lessonId: String) -> Unit
) {
    MainScaffoldWithBottomNav(
        screens = listOf(
            { HomeScreen() },
            // 2. Truyền hành động xuống cho LessonScreen
            { LessonScreen(onLessonClicked = onLessonClicked) },
            { ProfileScreen() }
        ),
        defaultIndex = 0 // Hoặc lấy từ state nào đó
    )
}



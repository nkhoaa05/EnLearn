package com.example.enlearn.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.enlearn.presentation.home.MainScreen
import com.example.enlearn.presentation.profile.ProfileScreen
import com.example.enlearn.ui.components.MainScaffoldWithBottomNav
import com.example.enlearn.ui.viewModel.ChapterViewModel


@Composable
fun LessonScreen(chapterViewModel: ChapterViewModel = viewModel()) {
    val chapters by chapterViewModel.chapters
    val primaryColor = Color(0xFF410FA3)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(primaryColor)
        ) {
            Text(
                "Lesson",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        if (chapters.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(200.dp))
                Text(
                    "Loading...",
                    modifier = Modifier.fillMaxSize(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn {
                chapters.forEach { chapter ->
                    // Hiển thị tiêu đề chapter
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                            text = chapter.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .padding(8.dp)
                                .padding(start = 12.dp)
                        )  }

                    }
                    // Hiển thị các lesson trong chapter
                    items(chapter.lessons.size) { index ->
                        val lesson = chapter.lessons[index]
                        LessonCard(
                            chapter = chapter.title,
                            lesson = lesson.title,
                            onClick = {
                                // Xử lý click ở đây
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun LessonCard(chapter: String, lesson: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(12.dp)
            .height(70.dp)
            .width(400.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF4F4F4)), // Xám nhẹ
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = lesson,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}


@Composable
fun LessonsScreen() {
    MainScaffoldWithBottomNav(
        screens = listOf(
            { MainScreen() },
            { LessonScreen() },
            { ProfileScreen() }
        ),
        defaultIndex = 1
    )
}
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun LessonScreen(chapterViewModel: ChapterViewModel = viewModel(),
                 onLessonClicked: (chapterId: String, lessonId: String) -> Unit) {
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
                .offset(y = 10.dp)
        ) {
            Text(
                "Lesson",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
        val chapters by chapterViewModel.chapters

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
                        Text(
                            text = chapter.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray)
                                .padding(8.dp)
                        )
                    }
                    // Hiển thị các lesson trong chapter
                    items(chapter.lessons.size) { index ->
                        val lesson = chapter.lessons[index]
                        LessonCard(
                            lessonTitle = lesson.title,
                            onClick = {
                                // Xử lý click ở đây
                                onLessonClicked(chapter.id, lesson.id)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun LessonCard(lessonTitle: String, onClick: () -> Unit) {
    // Sửa lại để nhận đúng tham số
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Gán hành động vào đây
            .padding(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = lessonTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}
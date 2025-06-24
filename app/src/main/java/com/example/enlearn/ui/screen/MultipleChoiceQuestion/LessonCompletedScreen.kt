package com.example.enlearn.ui.screen.MultipleChoiceQuestion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enlearn.R
import com.example.enlearn.ui.theme.BlueAction
import com.example.enlearn.ui.theme.PurplePrimary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonCompletedScreen(
    lessonTitle: String, // Nhận lessonTitle
    onBackToHome: () -> Unit,
    score: Int,
    totalQuestions: Int,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Image(painter = painterResource(R.drawable.back_icon),
                            contentDescription = "Back",
                            modifier = Modifier.size(30.dp))
                    }
                },
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PurplePrimary)
            )
        },

        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 110.dp, bottom = 32.dp, start = 32.dp, end = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.completed_lesson),
                contentDescription = "Lesson Completed",
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                // HIỂN THỊ ĐÚNG TÊN BÀI HỌC
                text = "You have completed '$lessonTitle' of the English language course",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueAction),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back to home", fontSize = 16.sp)
            }
        }
    }
}
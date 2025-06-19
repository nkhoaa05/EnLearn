
package com.example.enlearn.ui.screen.MultipleChoiceQuestion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enlearn.ui.components.AnswerOptionButton
import com.example.enlearn.ui.viewmodel.AnswerState
import com.example.enlearn.ui.viewmodel.MultipleChoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleChoiceScreen(
    viewModel: MultipleChoiceViewModel,
    onNavigateToCompleted: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLessonFinished) {
        LaunchedEffect(uiState.isLessonFinished) {
            onNavigateToCompleted()
        }
    }

    // Box để xếp chồng nội dung chính và panel kết quả
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            // TopBar hiển thị tiến trình
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("${uiState.currentQuestionIndex + 1} / ${uiState.questions.size}") },
                    navigationIcon = {
                        // IconButton(onClick = onBack) { ... }
                    }
                )
            }
        ) { paddingValues ->
            // Nội dung chính
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    uiState.isLoading -> { /* ... */ }
                    uiState.error != null -> { /* ... */ }
                    uiState.currentQuestion != null -> {
                        val question = uiState.currentQuestion!!
                        Text(
                            text = "Question ${question.number}: ${question.question}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        question.options.forEachIndexed { index, optionText ->
                            AnswerOptionButton(
                                letter = ('A' + index),
                                text = optionText,
                                isSelected = uiState.selectedOptionIndex == index,
                                enabled = !uiState.isSubmitted, // Vô hiệu hóa khi đã submit
                                onClick = { viewModel.onOptionSelected(index) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        // Panel kết quả hiển thị ở dưới cùng
        ResultPanel(
            modifier = Modifier.align(Alignment.BottomCenter),
            isVisible = uiState.isSubmitted,
            answerState = uiState.answerState,
            onNext = { viewModel.goToNextQuestion() },
            onTryAgain = { viewModel.tryAgain() }
        )

        // Nút Submit chính, chỉ hiển thị khi chưa submit
        if (!uiState.isSubmitted && !uiState.isLoading) {
            Button(
                onClick = { viewModel.submitAnswer() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = uiState.selectedOptionIndex != null
            ) {
                Text("Submit")
            }
        }
    }
}

// Component cho Panel kết quả
// Component cho Panel kết quả (ĐÃ SỬA LỖI)
@Composable
private fun ResultPanel(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    answerState: AnswerState,
    onNext: () -> Unit,
    onTryAgain: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        // KHAI BÁO CÁC BIẾN RIÊNG LẺ
        val backgroundColor: Color
        val title: String
        val buttonText: String
        val buttonAction: () -> Unit

        // GÁN GIÁ TRỊ TRONG WHEN
        when (answerState) {
            AnswerState.CORRECT -> {
                backgroundColor = Color(0xFF3D5AFE) // Blue
                title = "Correct Answer!"
                buttonText = "Next"
                buttonAction = onNext
            }
            AnswerState.INCORRECT -> {
                backgroundColor = Color(0xFFD50000) // Red
                title = "Wrong Answer!"
                buttonText = "Try again"
                buttonAction = onTryAgain
            }
            else -> {
                // Trường hợp mặc định để tránh lỗi
                backgroundColor = Color.Transparent
                title = ""
                buttonText = ""
                buttonAction = {}
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            // Giờ đây có thể dùng .copy() an toàn
            colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = buttonAction,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    // Truyền màu đúng vào đây
                    Text(buttonText, color = backgroundColor)
                }
            }
        }
    }
}

package com.example.enlearn.ui.screen.MultipleChoiceQuestion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enlearn.ui.theme.BlueAction
import com.example.enlearn.ui.theme.GreyBorder
import com.example.enlearn.ui.theme.PurplePrimary
import com.example.enlearn.ui.viewmodel.AnswerState
import com.example.enlearn.ui.viewmodel.MultipleChoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleChoiceScreen(
    viewModel: MultipleChoiceViewModel,
    onNavigateToCompleted: (score: Int, totalQuestions: Int) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLessonFinished) {
        LaunchedEffect(uiState.isLessonFinished) {
            if (uiState.isLessonFinished) { // Thêm kiểm tra này để đảm bảo chỉ gọi khi isLessonFinished là true
                onNavigateToCompleted(uiState.score, uiState.questions.size) // Sửa ở đây
            }
        }
    }

    // Box để xếp chồng nội dung chính và panel kết quả
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            // TopBar hiển thị tiến trình
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("${uiState.currentQuestionIndex + 1} / ${uiState.questions.size}",
                        color = Color.White)},
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = PurplePrimary
                    )
                )
            },
            containerColor = Color.White
        ) { paddingValues ->
            // Nội dung chính
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator()
                    uiState.error != null -> Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                    uiState.currentQuestion != null -> {
                        // Tách nội dung chính ra một Composable riêng
                        QuizContent(
                            uiState = uiState,
                            onOptionSelected = viewModel::onOptionSelected,
                            onSubmit = viewModel::submitAnswer
                        )
                    }
                }
            }
        }

        // Panel kết quả hiển thị ở dưới cùng
        ResultPanel(
            modifier = Modifier.align(Alignment.BottomCenter),
            isVisible = uiState.isSubmitted,
            answerState = uiState.answerState,
            onNext = { viewModel.proceedToNextQuestion() },
            onTryAgain = { viewModel.proceedToNextQuestion() }
        )

        // Nút Submit chính, chỉ hiển thị khi chưa submit
//        if (!uiState.isSubmitted && !uiState.isLoading) {
//            Button(
//                onClick = { viewModel.submitAnswer() },
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                enabled = uiState.selectedOptionIndex != null
//            ) {
//                Text("Submit")
//            }
//        }
    }
}

@Composable
private fun QuizContent(
    uiState: com.example.enlearn.ui.viewmodel.QuizUiState,
    onOptionSelected: (Int) -> Unit,
    onSubmit: () -> Unit
) {
    val question = uiState.currentQuestion!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Phần câu hỏi
        Text(
            text = "Question ${question.number}:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = question.question,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Phần các lựa chọn
        LazyColumn(
            modifier = Modifier.weight(1f)
                .padding(top = 50.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(question.options) { index, optionText ->
                AnswerOptionCard(
                    index = index,
                    text = optionText,
                    isSelected = uiState.selectedOptionIndex == index,
                    onClick = { onOptionSelected(index) }
                )
                Spacer(modifier = Modifier.height(40.dp))

            }
        }

        // Nút Submit
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BlueAction),
            enabled = uiState.selectedOptionIndex != null
        ) {
            Text("Submit", fontSize = 16.sp)
        }
    }
}

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
                buttonText = "Next"
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
            shape = RoundedCornerShape(topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp),
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
@Composable
fun AnswerOptionCard(
    index: Int,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val letter = ('A' + index).toString() // Chuyển index 0, 1, 2 thành 'A', 'B', 'C'

    val borderColor = if (isSelected) PurplePrimary else GreyBorder

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Đảm bảo các item trong Row có chiều cao bằng nhau
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Phần chữ cái A, B, C
        Box(
            modifier = Modifier
                .size(40.dp),
//            .border(width = 2.dp, color = borderColor, shape = CircleShape)
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Phần nội dung câu trả lời
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.Black
        )
    }
}
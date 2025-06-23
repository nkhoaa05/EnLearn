package com.example.enlearn.ui.screen.MultipleChoiceQuestion

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.enlearn.data.model.LessonStatus
import com.example.enlearn.ui.theme.BlueAction
import com.example.enlearn.ui.theme.BlueSelection
import com.example.enlearn.ui.theme.GreyBorder
import com.example.enlearn.ui.theme.PurplePrimary
import com.example.enlearn.ui.viewmodel.AnswerState
import com.example.enlearn.ui.viewmodel.MultipleChoiceViewModel
import com.example.enlearn.ui.viewmodel.QuizUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleChoiceScreen(
    viewModel: MultipleChoiceViewModel,
    onNavigateToCompleted: (score: Int, totalQuestions: Int, lessonTitle: String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Xử lý lưu tiến trình dở dang khi người dùng thoát màn hình
    DisposableEffect(lifecycleOwner) {
        Log.d("ContinueLearning", "DisposableEffect has been set up.")

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                Log.d("ContinueLearning", "ON_PAUSE event detected.")

                if (!uiState.isLessonFinished) {
                    Log.d("ContinueLearning", "Lesson is not finished. Calling saveProgress...")
                    viewModel.saveProgress(status = LessonStatus.IN_PROGRESS,
                        chapterId = viewModel.chapterId,
                        lessonId = viewModel.lessonId)
                } else {
                    Log.d("ContinueLearning", "Lesson is already finished. Skipping save.")
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            Log.d("ContinueLearning", "DisposableEffect is being disposed.")
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Xử lý điều hướng khi bài học kết thúc
    LaunchedEffect(uiState.isLessonFinished) {
        if (uiState.isLessonFinished) {
            onNavigateToCompleted(uiState.score, uiState.questions.size, uiState.lessonTitle)
        }
    }

    // Dùng Box để xếp chồng nội dung chính và ResultPanel
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (uiState.questions.isNotEmpty()) {
                            Text(
                                text = "${uiState.currentQuestionIndex + 1} / ${uiState.questions.size}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PurplePrimary)
                )
            },
            containerColor = Color.White
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when {
                    uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    uiState.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(uiState.error!!, color = MaterialTheme.colorScheme.error) }
                    uiState.currentQuestion != null -> {
                        QuizContent(
                            uiState = uiState,
                            onOptionSelected = viewModel::onOptionSelected,
                            onSubmit = viewModel::submitAnswer
                        )
                    }
                }
            }
        }

        // Panel kết quả sẽ nằm đè lên trên khi isSubmitted = true
        ResultPanel(
            modifier = Modifier.align(Alignment.BottomCenter),
            isVisible = uiState.isSubmitted,
            answerState = uiState.answerState,
            onNext = { viewModel.proceedToNextQuestion() }
        )
    }
}

@Composable
private fun QuizContent(
    uiState: QuizUiState,
    onOptionSelected: (Int) -> Unit,
    onSubmit: () -> Unit
) {
    val question = uiState.currentQuestion!!

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Question ${question.number}:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(question.question, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(question.options) { index, optionText ->
                val isCorrectOption = (index == question.correctAnswerIndex)
                AnswerOptionCard(
                    index = index,
                    text = optionText,
                    isSelected = uiState.selectedOptionIndex == index,
                    answerState = uiState.answerState,
                    isSubmitted = uiState.isSubmitted,
                    isCorrectOption = isCorrectOption,
                    onClick = { onOptionSelected(index) }
                )
            }
        }

        // Nút Submit chỉ hiển thị khi chưa trả lời
        if (!uiState.isSubmitted) {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueAction),
                enabled = uiState.selectedOptionIndex != null
            ) {
                Text("Submit", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ResultPanel(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    answerState: AnswerState,
    onNext: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        val backgroundColor: Color
        val title: String
        val buttonText: String
        val buttonTextColor: Color

        when (answerState) {
            AnswerState.CORRECT -> {
                backgroundColor = Color(0xFF5B7BFE)
                title = "Correct Answer!"
                buttonText = "Next"
                buttonTextColor = Color(0xFF5B7BFE)
            }
            AnswerState.INCORRECT -> {
                backgroundColor = Color(0xFFE91E63)
                title = "Wrong Answer!"
                buttonText = "Next"
                buttonTextColor = Color(0xFFE91E63)
            }
            else -> {
                backgroundColor = Color.Transparent
                title = ""
                buttonText = ""
                buttonTextColor = Color.Transparent
            }
        }

        Box(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
            Column(
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(buttonText, color = buttonTextColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun AnswerOptionCard(
    index: Int,
    text: String,
    isSelected: Boolean,
    answerState: AnswerState,
    isSubmitted: Boolean,
    isCorrectOption: Boolean,
    onClick: () -> Unit
) {
    val letter = ('A' + index).toString()

    val (borderColor, backgroundColor, contentColor) = when {
        isSubmitted && isCorrectOption -> Triple(Color(0xFF00C853), Color(0xFFE8F5E9), Color(0xFF00C853))
        isSubmitted && isSelected && answerState == AnswerState.INCORRECT -> Triple(Color(0xFFD32F2F), Color(0xFFFFEBEE), Color(0xFFD32F2F))
        isSelected -> Triple(PurplePrimary, BlueSelection, PurplePrimary)
        else -> Triple(GreyBorder, Color.White, Color.Black)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(enabled = !isSubmitted, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).border(width = 2.dp, color = borderColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = letter, color = contentColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 18.sp, color = Color.Black)
    }
}
package com.example.enlearn.ui.screen.MultipleChoiceQuestion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.enlearn.data.model.MultipleChoiceOject.AnswerOption
import com.example.enlearn.ui.ViewModel.AnswerState
import com.example.enlearn.ui.ViewModel.MultipleChoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleChoiceScreen(
    viewModel: MultipleChoiceViewModel = viewModel(),
    onNavigateToCompleted: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Điều hướng khi bài học kết thúc
    if (uiState.isLessonFinished) {
        // LaunchedEffect để đảm bảo chỉ gọi 1 lần
        LaunchedEffect(Unit) {
            onNavigateToCompleted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Thanh tiến trình
                    LinearProgressIndicator(
                        progress = { 0.5f }, // Dữ liệu động
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading && uiState.currentQuestion == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                uiState.currentQuestion?.let { question ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Select the correct word",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(64.dp))
                        Text(
                            text = question.questionText,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )

                        // Hiển thị đáp án đúng khi trả lời sai
                        if (uiState.isSubmitted && uiState.answerStates[uiState.selectedOptionId] == AnswerState.INCORRECT) {
                            val correctOptionText = question.options.find { it.id == question.correctOptionId }?.text
                            Text(
                                text = correctOptionText ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF00C853) // Màu xanh lá
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        question.options.forEach { option ->
                            AnswerOptionItem(
                                option = option,
                                isSelected = uiState.selectedOptionId == option.id,
                                state = uiState.answerStates.getOrElse(option.id) { AnswerState.UNSELECTED },
                                isSubmitted = uiState.isSubmitted,
                                onClick = { viewModel.onOptionSelected(option.id) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = { viewModel.submitAnswer() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            enabled = uiState.selectedOptionId != null && !uiState.isSubmitted
                        ) {
                            Text(text = if (uiState.isSubmitted) "Continue" else "Submit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerOptionItem(
    option: AnswerOption,
    isSelected: Boolean,
    state: AnswerState,
    isSubmitted: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isSelected && state == AnswerState.CORRECT -> Color(0xFF00C853) // Green
        isSelected && state == AnswerState.INCORRECT -> Color(0xFFD32F2F) // Red
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    val backgroundColor = when {
        isSelected && state == AnswerState.CORRECT -> Color(0xFFE8F5E9) // Light Green
        isSelected && state == AnswerState.INCORRECT -> Color(0xFFFFEBEE) // Light Red
        else -> MaterialTheme.colorScheme.surface
    }

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                isSelected && state == AnswerState.CORRECT ->
                    Icon(Default.Check, contentDescription = "Correct", tint = Color.White, modifier = Modifier.background(borderColor, CircleShape).padding(2.dp))
                isSelected && state == AnswerState.INCORRECT -> {
                    Icon(Default.Close, contentDescription = "Incorrect", tint = Color.White, modifier = Modifier.background(borderColor, CircleShape).padding(2.dp))
                }
                else ->
                    RadioButton(selected = isSelected, onClick = null)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = option.text,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
        }
    }
}
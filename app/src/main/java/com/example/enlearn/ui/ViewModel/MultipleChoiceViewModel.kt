package com.example.enlearn.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enlearn.data.model.MultipleChoiceOject.Question
import com.example.enlearn.data.repository.MultipleChoiceRepository.LessonRepository
import com.example.enlearn.data.repository.MultipleChoiceRepository.LessonRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Trạng thái cho từng câu trả lời
enum class AnswerState { UNSELECTED, CORRECT, INCORRECT }

// Trạng thái chung của màn hình
data class MultipleChoiceUiState(
    val isLoading: Boolean = true,
    val currentQuestion: Question? = null,
    val selectedOptionId: String? = null,
    val answerStates: Map<String, AnswerState> = emptyMap(),
    val isSubmitted: Boolean = false,
    val isLessonFinished: Boolean = false
)

class MultipleChoiceViewModel(
    private val lessonRepository: LessonRepository = LessonRepositoryImpl() // Sau này sẽ inject bằng Hilt/Koin
) : ViewModel() {

    private val _uiState = MutableStateFlow(MultipleChoiceUiState())
    val uiState: StateFlow<MultipleChoiceUiState> = _uiState.asStateFlow()

    init {
        loadNextQuestion()
    }

    fun onOptionSelected(optionId: String) {
        if (!_uiState.value.isSubmitted) {
            _uiState.update { it.copy(selectedOptionId = optionId) }
        }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        val question = currentState.currentQuestion ?: return
        val selectedId = currentState.selectedOptionId ?: return

        val isCorrect = selectedId == question.correctOptionId
        val newAnswerStates = mapOf(
            selectedId to if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT
        )

        _uiState.update {
            it.copy(
                isSubmitted = true,
                answerStates = newAnswerStates
            )
        }

        // Tự động chuyển câu sau 2 giây hoặc chờ người dùng bấm "Continue"
        viewModelScope.launch {
            delay(2000)
            loadNextQuestion()
        }
    }

    private fun loadNextQuestion() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val nextQuestion = lessonRepository.getNextQuestion()
            if (nextQuestion != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentQuestion = nextQuestion,
                        selectedOptionId = null,
                        answerStates = emptyMap(),
                        isSubmitted = false
                    )
                }
            } else {
                // Hết bài học
                _uiState.update { it.copy(isLoading = false, isLessonFinished = true) }
            }
        }
    }
}
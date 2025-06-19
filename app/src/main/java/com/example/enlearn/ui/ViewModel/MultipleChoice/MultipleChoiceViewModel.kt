package com.example.enlearn.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enlearn.data.model.MultipleChoiceOject.QuestionData
import com.example.enlearn.data.repository.MultipleChoiceRepository.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AnswerState { UNSELECTED, CORRECT, INCORRECT }

data class MultipleChoiceUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val questions: List<QuestionData> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val answerState: AnswerState = AnswerState.UNSELECTED,
    val isSubmitted: Boolean = false,
    val isLessonFinished: Boolean = false,
    val score: Int = 0
) {
    val currentQuestion: QuestionData?
        get() = questions.getOrNull(currentQuestionIndex)
}

class MultipleChoiceViewModel(
    private val lessonRepository: LessonRepository,
    private val chapterId: String,
    private val lessonId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(MultipleChoiceUiState())
    val uiState: StateFlow<MultipleChoiceUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
    }

    private fun loadLesson() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = lessonRepository.getLesson(chapterId, lessonId)
            result.onSuccess { lessonData ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        questions = lessonData.questions
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "An unknown error occurred"
                    )
                }
            }
        }
    }

    fun onOptionSelected(optionIndex: Int) {
        if (!_uiState.value.isSubmitted) {
            _uiState.update { it.copy(selectedOptionIndex = optionIndex) }
        }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        val question = currentState.currentQuestion ?: return
        val selectedIndex = currentState.selectedOptionIndex ?: return

        val isCorrect = selectedIndex == question.correctAnswerIndex
        val newAnswerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT

        _uiState.update {
            it.copy(
                isSubmitted = true,
                answerState = newAnswerState,
                score = if (isCorrect) it.score + 1 else it.score
            )
        }
    }

    fun goToNextQuestion() {
        val currentState = _uiState.value
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex < currentState.questions.size) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedOptionIndex = null,
                    isSubmitted = false,
                    answerState = AnswerState.UNSELECTED
                )
            }
        } else {
            _uiState.update { it.copy(isLessonFinished = true) }
        }
    }

    // HÀM MỚI: Reset lại trạng thái để người dùng thử lại câu hỏi hiện tại
    fun tryAgain() {
        _uiState.update {
            it.copy(
                selectedOptionIndex = null,
                isSubmitted = false,
                answerState = AnswerState.UNSELECTED
            )
        }
    }
}


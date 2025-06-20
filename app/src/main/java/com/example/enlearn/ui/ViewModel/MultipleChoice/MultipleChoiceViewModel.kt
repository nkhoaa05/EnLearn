package com.example.enlearn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.enlearn.data.model.Question // Đảm bảo import đúng model
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Enum để quản lý trạng thái của câu trả lời
enum class AnswerState { UNSELECTED, CORRECT, INCORRECT }

// Lớp trạng thái cho toàn bộ UI, rất quan trọng!
data class QuizUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val lessonTitle: String = "",
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val answerState: AnswerState = AnswerState.UNSELECTED,
    val isSubmitted: Boolean = false,
    val isLessonFinished: Boolean = false,
    val score: Int = 0
) {
    // Helper để lấy câu hỏi hiện tại một cách an toàn
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)
}

class MultipleChoiceViewModel(
    private val chapterId: String,
    private val lessonId: String
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        // Log để kiểm tra xem ID có được truyền vào đúng không
        Log.d("QuizVM", "Initializing for chapterId: $chapterId, lessonId: $lessonId")
        fetchQuestionsForLesson()
    }

    private fun fetchQuestionsForLesson() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val lessonDoc = db.collection("chapters").document(chapterId)
                    .collection("lessons").document(lessonId)
                    .get().await()

                val lessonTitle = lessonDoc.getString("title") ?: "Lesson"
                val questionMaps = lessonDoc.get("questions") as? List<Map<String, Any>> ?: emptyList()

                val questionsList = questionMaps.map { map ->
                    Question(
                        number = (map["number"] as? Long)?.toInt() ?: 0,
                        question = map["question"] as? String ?: "",
                        options = map["options"] as? List<String> ?: emptyList(),
                        correctAnswerIndex = (map["correctAnswerIndex"] as? Long)?.toInt() ?: -1
                    )
                }

                Log.d("QuizVM", "Successfully fetched ${questionsList.size} questions for lesson: $lessonTitle")

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        questions = questionsList,
                        lessonTitle = lessonTitle
                    )
                }
            } catch (e: Exception) {
                Log.e("QuizVM", "Error fetching questions", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load lesson. Please try again.")
                }
            }
        }
    }

    // Các hàm logic cho quiz
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
        _uiState.update {
            it.copy(
                isSubmitted = true,
                answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT,
                score = if (isCorrect) it.score + 1 else it.score
            )
        }
    }

    fun proceedToNextQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedOptionIndex = null,
                    isSubmitted = false,
                    answerState = AnswerState.UNSELECTED
                )
            }
        } else {
            // Hết bài học
            _uiState.update { it.copy(isLessonFinished = true) }
        }
    }
}




package com.example.enlearn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enlearn.data.model.LessonStatus
import com.example.enlearn.data.model.Progress
import com.example.enlearn.data.model.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

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

    private val auth = FirebaseAuth.getInstance()
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

    fun saveCurrentProgress() {
        // Chỉ lưu khi đã trả lời ít nhất 1 câu, tránh lưu khi vừa vào đã thoát
        if (_uiState.value.currentQuestionIndex >= 0 && _uiState.value.questions.isNotEmpty()) {
            saveProgress(LessonStatus.IN_PROGRESS)
        }
    }

    // Hàm lưu progress chung
    fun saveProgress(status: LessonStatus) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)

        val progressToSave = Progress(
            chapterId = this.chapterId,
            lessonId = this.lessonId,
            status = status.name,
            score = _uiState.value.score,
            totalQuestions = _uiState.value.questions.size,
            lastAccessed = Date()
        )

        db.runTransaction { transaction ->
            // 1. ĐỌC: Lấy snapshot của document user
            val snapshot = transaction.get(userRef)
            if (!snapshot.exists()) {
                Log.e("QuizVM", "User document does not exist, cannot save progress.")
                return@runTransaction // Thoát khỏi transaction
            }

            // LẤY DỮ LIỆU TRỰC TIẾP TỪ TRƯỜNG "progress"
            val progressDataFromDb = snapshot.get("progress")

            // Parse thủ công và an toàn từ List<Map> thành List<Progress>
            val existingProgressList = (progressDataFromDb as? List<*>)?.mapNotNull { item ->
                (item as? Map<String, Any>)?.let { map ->
                    Progress(
                        chapterId = map["chapterId"] as? String ?: "",
                        lessonId = map["lessonId"] as? String ?: "",
                        status = map["status"] as? String ?: "",
                        score = (map["score"] as? Long)?.toInt() ?: 0,
                        totalQuestions = (map["totalQuestions"] as? Long)?.toInt() ?: 0,
                        lastAccessed = (map["lastAccessed"] as? com.google.firebase.Timestamp)?.toDate()
                    )
                }
            }?.toMutableList() ?: mutableListOf() // Nếu null hoặc trống, tạo list mới

            // 2. SỬA: Tìm và cập nhật/thêm mới
            val index = existingProgressList.indexOfFirst { it.lessonId == this.lessonId }

            if (index != -1) {
                // Đã tồn tại -> cập nhật
                existingProgressList[index] = progressToSave
            } else {
                // Chưa tồn tại -> thêm mới
                existingProgressList.add(progressToSave)
            }

            // 3. GHI: Ghi đè lại toàn bộ mảng progress đã cập nhật
            transaction.update(userRef, "progress", existingProgressList)

        }.addOnSuccessListener {
            Log.d("QuizVM", "Progress saved with status: $status")
        }.addOnFailureListener { e ->
            Log.e("QuizVM", "Failed to save progress", e)
        }
    }

    private fun onLessonCompleted() {
        saveProgress(LessonStatus.COMPLETED)
        _uiState.update { it.copy(isLessonFinished = true) }
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
            // Hết bài học, gọi hàm cập nhật progress
            onLessonCompleted()
        }
    }

}




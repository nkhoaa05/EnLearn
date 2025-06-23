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
    val chapterId: String,
    val lessonId: String

) : ViewModel() {
    private val instanceId = System.currentTimeMillis() % 10000
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    private val TAG = "QuizVM_Debug"
    private val TAG2 = "ViewModelLifecycle"

    init {
        // Log để kiểm tra xem ID có được truyền vào đúng không
        Log.d(TAG, "[ID: $instanceId] --- ViewModel INITIALIZED ---")
        Log.d(TAG, "[ID: $instanceId] Chapter ID: $chapterId")
        Log.d(TAG, "[ID: $instanceId] Lesson ID: $lessonId")
        Log.d(TAG, "--- ViewModel INIT ---")
        Log.d(TAG2, "Instance ID: @${System.identityHashCode(this)}")
        Log.d(TAG2, "Content: chapterId=$chapterId, lessonId=$lessonId")
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
            saveProgress(LessonStatus.COMPLETED, this.chapterId, this.lessonId)
        }
    }

    // Hàm lưu progress chung
    fun saveProgress(status: LessonStatus, chapterId: String, lessonId: String) {
        Log.d(TAG, "[ID: $instanceId] --- saveProgress CALLED ---")
        Log.d(TAG, "[ID: $instanceId] Status: $status")
        Log.d(TAG, "[ID: $instanceId] Received Chapter ID: $chapterId")
        Log.d(TAG, "Attempting to save progress. Status: $status, Chapter: $chapterId, Lesson: $lessonId")
        Log.d(TAG2, "--- proceedToNextQuestion CALLED ---")
        Log.d(TAG2, "Instance ID: @${System.identityHashCode(this)}")
        Log.d(TAG2, "Content: chapterId=$chapterId, lessonId=$lessonId")
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "Cannot save progress, user is NULL.")
            return
        }

        val progressToSave = Progress(
            chapterId = chapterId,
            lessonId = lessonId,
            status = status.name,
            score = _uiState.value.score,
            totalQuestions = _uiState.value.questions.size,
            lastAccessed = Date()
        )

        Log.d(TAG, "Progress object to save: $progressToSave")
        val userRef = db.collection("users").document(userId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            if (!snapshot.exists()) {
                throw Exception("User document does not exist.")
            }

            val progressDataFromDb = snapshot.get("progress")
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
            }?.toMutableList() ?: mutableListOf()

            val index = existingProgressList.indexOfFirst { it.chapterId == chapterId && it.lessonId == lessonId }

            if (status == LessonStatus.IN_PROGRESS && index != -1 && existingProgressList[index].status == LessonStatus.COMPLETED.name) {
                Log.d(TAG, "Lesson already completed. Skipping update to IN_PROGRESS.")
                return@runTransaction
            }

            if (index != -1) {
                existingProgressList[index] = progressToSave
            } else {
                existingProgressList.add(progressToSave)
            }

            transaction.update(userRef, "progress", existingProgressList)

        }.addOnSuccessListener {
            Log.d(TAG, "✅ Transaction to update progress completed successfully.")
        }.addOnFailureListener { e ->
            Log.e(TAG, "❌ Transaction to update progress failed.", e)
        }
    }

    private fun onLessonCompleted() {
        Log.d(TAG2, "--- proceedToNextQuestion CALLED ---")
        Log.d(TAG2, "Instance ID: @${System.identityHashCode(this)}")
        Log.d(TAG2, "Content: chapterId=$chapterId, lessonId=$lessonId")

        saveProgress(LessonStatus.COMPLETED, this.chapterId, this.lessonId)
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




package com.example.enlearn.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enlearn.data.model.Lesson
import com.example.enlearn.data.model.LessonStatus
import com.example.enlearn.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class HomeUiState(
    val user: User? = null,
    val allLessons: Map<String, Lesson> = emptyMap(), // Map lessonId -> Lesson object
    val isLoading: Boolean = true,
    val error: String? = null
) {
    // Lọc ra bài học đang học dở (sắp xếp theo lần truy cập gần nhất)
    val continuingLessons: List<Lesson>
        get() = user?.progress
            ?.filter { it.status == LessonStatus.IN_PROGRESS.name }
            ?.sortedByDescending { it.lastAccessed }
            ?.mapNotNull { progress -> allLessons[progress.lessonId] }
            ?: emptyList()

    // Lọc ra các bài học đã hoàn thành
    val completedLessons: List<Lesson>
        get() = user?.progress
            ?.filter { it.status == LessonStatus.COMPLETED.name }
            ?.sortedByDescending { it.lastAccessed }
            ?.mapNotNull { progress -> allLessons[progress.lessonId] }
            ?: emptyList()
}


class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Tải song song thông tin người dùng và danh sách tất cả các bài học
                val userDeferred = async { fetchCurrentUser() }
                val allLessonsDeferred = async { fetchAllLessons() }

                val user = userDeferred.await()
                val allLessonsMap = allLessonsDeferred.await()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        allLessons = allLessonsMap
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load data.") } // Giả sử bạn có trường error trong UiState
                Log.e("HomeViewModel", "Error loading initial data", e)
            }
        }
    }

    private suspend fun fetchCurrentUser(): User? {
        val userId = auth.currentUser?.uid ?: return null
        return db.collection("users").document(userId).get().await()
            .toObject(User::class.java)
    }

    private suspend fun fetchAllLessons(): Map<String, Lesson> {
        val lessonsMap = mutableMapOf<String, Lesson>()
        val chaptersSnapshot = db.collection("chapters").get().await()
        for (chapterDoc in chaptersSnapshot.documents) {
            val lessonsSnapshot = chapterDoc.reference.collection("lessons").get().await()
            for (lessonDoc in lessonsSnapshot.documents) {
                val lesson = lessonDoc.toObject(Lesson::class.java)
                if (lesson != null) {
                    // Gán ID cho lesson và chapter vào map
                    lessonsMap[lessonDoc.id] = lesson.copy(id = lessonDoc.id, chapterId = chapterDoc.id)
                }
            }
        }
        return lessonsMap
    }
}
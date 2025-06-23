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

// --- CÁC LỚP ĐẠI DIỆN CHO TRẠNG THÁI VÀ ITEM TRÊN UI ---

// Sealed interface để biểu diễn các loại item khác nhau trong LazyColumn
sealed interface HomeListItem {
    // Dùng object cho các item không chứa dữ liệu để tiết kiệm bộ nhớ
    object ContinueLearningHeader : HomeListItem
    object AllLessonLearnedHeader : HomeListItem
    data class LessonItem(val lesson: Lesson) : HomeListItem
    data class EmptyState(val message: String) : HomeListItem
}

// Data class cho trạng thái của toàn bộ màn hình Home
data class HomeUiState(
    val user: User? = null,
    val homeListItems: List<HomeListItem> = emptyList(), // Chỉ có 1 danh sách duy nhất cho UI
    val isLoading: Boolean = true,
    val error: String? = null
)


// --- VIEWMODEL CHÍNH ---

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "HomeViewModel"

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Tải dữ liệu lần đầu khi ViewModel được tạo
        refreshData()
    }

    // Hàm public để UI có thể gọi và làm mới dữ liệu
    fun refreshData() {
        viewModelScope.launch {
            Log.d(TAG, "Refreshing data...")
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Tải song song thông tin người dùng và danh sách tất cả các bài học
                val userDeferred = async { fetchCurrentUser() }
                val allLessonsMapDeferred = async { fetchAllLessons() }

                val user = userDeferred.await()
                val allLessonsMap = allLessonsMapDeferred.await()

                // Từ dữ liệu thô, xây dựng danh sách item cho UI
                val finalUiList = buildHomeList(user, allLessonsMap)

                // Cập nhật State một lần duy nhất với tất cả dữ liệu mới
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        homeListItems = finalUiList
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing data", e)
                _uiState.update { it.copy(isLoading = false, error = "Failed to load data.") }
            }
        }
    }

    // --- CÁC HÀM PRIVATE HỖ TRỢ ---

    private suspend fun fetchCurrentUser(): User? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            db.collection("users").document(userId).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch or parse user.", e)
            null
        }
    }

    private suspend fun fetchAllLessons(): Map<String, Lesson> {
        val lessonsMap = mutableMapOf<String, Lesson>()
        try {
            val chaptersSnapshot = db.collection("chapters").get().await()
            for (chapterDoc in chaptersSnapshot.documents) {
                val lessonsSnapshot = chapterDoc.reference.collection("lessons").get().await()
                for (lessonDoc in lessonsSnapshot.documents) {
                    val lesson = lessonDoc.toObject(Lesson::class.java)
                    if (lesson != null) {
                        // SỬ DỤNG KEY KẾT HỢP ĐỂ ĐẢM BẢO TÍNH DUY NHẤT
                        val compositeKey = "${chapterDoc.id}-${lessonDoc.id}"
                        lessonsMap[compositeKey] = lesson.copy(id = lessonDoc.id, chapterId = chapterDoc.id)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch all lessons.", e)
        }
        Log.d(TAG, "fetchAllLessons completed. Map size: ${lessonsMap.size}")
        return lessonsMap
    }

    private fun buildHomeList(user: User?, allLessons: Map<String, Lesson>): List<HomeListItem> {
        val list = mutableListOf<HomeListItem>()
        val progressList = user?.progress ?: emptyList()

        // Mục "Continue Learning"
        list.add(HomeListItem.ContinueLearningHeader)
        val continuingLessons = progressList
            .filter { it.status == LessonStatus.IN_PROGRESS.name }
            .sortedByDescending { it.lastAccessed }
            .mapNotNull { progress ->
                val compositeKey = "${progress.chapterId}-${progress.lessonId}"
                allLessons[compositeKey]
            }

        if (continuingLessons.isEmpty()) {
            list.add(HomeListItem.EmptyState("Start a new lesson to see your progress here!"))
        } else {
            continuingLessons.forEach { list.add(HomeListItem.LessonItem(it)) }
        }

        // Mục "All Lesson Learned"
        list.add(HomeListItem.AllLessonLearnedHeader)
        val completedLessons = progressList
            .filter { it.status == LessonStatus.COMPLETED.name }
            .sortedByDescending { it.lastAccessed }
            .mapNotNull { progress ->
                val compositeKey = "${progress.chapterId}-${progress.lessonId}"
                allLessons[compositeKey]
            }

        if (completedLessons.isEmpty()) {
            list.add(HomeListItem.EmptyState("No lessons completed yet. Keep going!"))
        } else {
            completedLessons.forEach { list.add(HomeListItem.LessonItem(it)) }
        }

        return list
    }
}
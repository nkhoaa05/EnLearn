package com.example.enlearn.ui.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enlearn.data.model.Chapter
import com.example.enlearn.data.model.Lesson
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChapterViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _chapters = mutableStateOf<List<Chapter>>(emptyList())
    val chapters: State<List<Chapter>> = _chapters

    init {
        fetchChapters()
    }

    private fun fetchChapters() {
        Log.d("ChapterViewModel", "🔍 Bắt đầu fetch chapters từ Firestore (phiên bản Coroutine)")
        viewModelScope.launch {
            try {
                // 1. Lấy tất cả các document chapter
                val chapterDocuments = db.collection("chapters").get().await().documents
                Log.d("ChapterViewModel", "✅ Fetch thành công: ${chapterDocuments.size} documents chapter")

                // 2. Dùng async để tải tất cả các lesson của tất cả các chapter MỘT CÁCH SONG SONG
                val chapterJobs = chapterDocuments.map { doc ->
                    async { // Mỗi chapter là một công việc bất đồng bộ (job)
                        val chapterId = doc.id
                        val title = doc.getString("title")

                        if (title == null) {
                            Log.d("ChapterViewModel", "⚠️ Bỏ qua chapter [$chapterId] vì thiếu title")
                            return@async null // Trả về null nếu chapter không hợp lệ
                        }

                        Log.d("ChapterViewModel", "📘 Bắt đầu xử lý Chapter: id=$chapterId, title=$title")

                        // Lấy lessons cho chapter này
                        val lessonDocuments = db.collection("chapters").document(chapterId)
                            .collection("lessons").get().await().documents
                        Log.d("ChapterViewModel", "➡️ Đã fetch ${lessonDocuments.size} lessons cho chapter [$chapterId]")

                        val lessonList = lessonDocuments.mapNotNull { lessonDoc ->
                            val lessonId = lessonDoc.id
                            val lessonTitle = lessonDoc.getString("title") ?: return@mapNotNull null
                            Lesson(id = lessonId, title = lessonTitle, questions = emptyList())
                        }

                        Chapter(id = chapterId, title = title, lessons = lessonList)
                    }
                }

                // 3. Chờ tất cả các job tải lesson hoàn thành và lọc ra những chapter hợp lệ
                val chaptersList = chapterJobs.awaitAll().filterNotNull()

                // 4. SẮP XẾP DANH SÁCH SAU KHI ĐÃ CÓ TẤT CẢ DỮ LIỆU
                val sortedChapters = chaptersList.sortedBy { chapter ->
                    extractNumberFromTitle(chapter.title)
                }
                Log.d("ChapterViewModel", "🎉 Hoàn tất: Đã load và sắp xếp ${sortedChapters.size} chapters")

                // 5. Cập nhật State
                _chapters.value = sortedChapters

            } catch (e: Exception) {
                Log.e("ChapterViewModel", "❌ Lỗi trong quá trình fetch và xử lý chapters", e)
            }
        }
    }
    private fun extractNumberFromTitle(title: String): Int {
        // Sử dụng biểu thức chính quy (Regex) để tìm số đầu tiên trong chuỗi
        return Regex("""\d+""").find(title)?.value?.toIntOrNull() ?: Int.MAX_VALUE
    }
}


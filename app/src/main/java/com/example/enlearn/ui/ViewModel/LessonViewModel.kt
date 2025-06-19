package com.example.enlearn.ui.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.enlearn.data.model.Chapter
import com.example.enlearn.data.model.Lesson
import com.example.enlearn.data.model.Question
import com.google.firebase.firestore.FirebaseFirestore

class ChapterViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _chapters = mutableStateOf<List<Chapter>>(emptyList())
    val chapters: State<List<Chapter>> = _chapters

    init {
        fetchChapters()
    }

    fun fetchChapters() {
        Log.d("ChapterViewModel", "🔍 Bắt đầu fetch chapters từ Firestore")

        db.collection("chapters")
            .get()
            .addOnSuccessListener { result ->
                Log.d("ChapterViewModel", "✅ Fetch thành công: ${result.size()} documents")
                val chaptersList = mutableListOf<Chapter>()
                val totalChapters = result.size()
                var loadedChapters = 0

                for (doc in result.documents) {
                    val chapterId = doc.id
                    val title = doc.getString("title")

                    if (title == null) {
                        Log.d("ChapterViewModel", "⚠️ Bỏ qua chapter [$chapterId] vì thiếu title")
                        loadedChapters++
                        continue
                    }

                    Log.d("ChapterViewModel", "📘 Chapter: id=$chapterId, title=$title")

                    // Fetch lessons trong subcollection
                    db.collection("chapters")
                        .document(chapterId)
                        .collection("lessons")
                        .get()
                        .addOnSuccessListener { lessonResult ->
                            Log.d(
                                "ChapterViewModel",
                                "➡️ Đã fetch ${lessonResult.size()} lessons cho chapter [$chapterId]"
                            )

                            val lessonList = lessonResult.documents.mapNotNull { lessonDoc ->
                                try {
                                    val lessonId = lessonDoc.id
                                    val lessonTitle = lessonDoc.getString("title") ?: run {
                                        Log.d(
                                            "ChapterViewModel",
                                            "⚠️ Lesson [$lessonId] thiếu title"
                                        )
                                        return@mapNotNull null
                                    }

                                    val questionsRaw = lessonDoc.get("questions") as? List<*>
                                    if (questionsRaw == null) {
                                        Log.d(
                                            "ChapterViewModel",
                                            "⚠️ Lesson [$lessonId] không có trường 'questions'"
                                        )
                                        return@mapNotNull null
                                    }

                                    val questionList = questionsRaw.mapNotNull { questionObj ->
                                        val questionMap =
                                            questionObj as? Map<*, *> ?: return@mapNotNull null
                                        val number = (questionMap["number"] as? Long)?.toInt()
                                        val questionText = questionMap["question"] as? String
                                        val options = questionMap["options"] as? List<*>
                                        val correctAnswerIndex =
                                            (questionMap["correctAnswerIndex"] as? Long)?.toInt()

                                        if (number == null || questionText == null || options == null || correctAnswerIndex == null) {
                                            Log.d(
                                                "ChapterViewModel",
                                                "⚠️ Bỏ qua question không đầy đủ trong lesson [$lessonId]"
                                            )
                                            return@mapNotNull null
                                        }

                                        val optionStrings = options.mapNotNull { it as? String }
                                        if (optionStrings.size != options.size) {
                                            Log.d(
                                                "ChapterViewModel",
                                                "⚠️ Câu hỏi có option không hợp lệ trong lesson [$lessonId]"
                                            )
                                            return@mapNotNull null
                                        }

                                        Log.d(
                                            "ChapterViewModel",
                                            "📝 Loaded question $number in lesson [$lessonId]"
                                        )

                                        Question(
                                            number = number,
                                            question = questionText,
                                            options = optionStrings,
                                            correctAnswerIndex = correctAnswerIndex
                                        )
                                    }

                                    Log.d(
                                        "ChapterViewModel",
                                        "📗 Loaded lesson: $lessonId, title=$lessonTitle, questions=${questionList.size}"
                                    )

                                    Lesson(
                                        id = lessonId,
                                        title = lessonTitle,
                                        questions = questionList
                                    )
                                } catch (e: Exception) {
                                    Log.e("ChapterViewModel", "❌ Lỗi khi parse lesson", e)
                                    null
                                }
                            }

                            chaptersList.add(
                                Chapter(
                                    id = chapterId,
                                    title = title,
                                    lessons = lessonList
                                )
                            )

                            loadedChapters++
                            Log.d(
                                "ChapterViewModel",
                                "✅ Đã load chapter [$chapterId], tổng lessons: ${lessonList.size}"
                            )

                            // Khi tất cả các chapter đã load xong
                            if (loadedChapters == totalChapters) {
                                val sortedChapters = chaptersList.sortedBy { chapter ->
                                    val match = Regex("""Chapter\s*(\d+)""").find(chapter.title)
                                    match?.groupValues?.get(1)?.toIntOrNull() ?: Int.MAX_VALUE
                                }
                                _chapters.value = sortedChapters
                                Log.d(
                                    "ChapterViewModel",
                                    "🎉 Hoàn tất: Đã load ${chaptersList.size} chapters đầy đủ"
                                )
                            }
                        }
                        .addOnFailureListener {
                            Log.e(
                                "ChapterViewModel",
                                "❌ Lỗi load lessons cho chapter [$chapterId]",
                                it
                            )
                            loadedChapters++
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ChapterViewModel", "❌ Lỗi fetch chapters", exception)
            }
    }
}


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

    private fun fetchChapters() {
        Log.d("ChapterViewModel", "Fetching chapters")
        db.collection("chapters")
            .get()
            .addOnSuccessListener { result ->
                val chaptersList = result.documents.mapNotNull { doc ->
                    try {
                        Log.d("LessonViewModel","Loading..")
                        val title = doc.getString("title") ?: return@mapNotNull null
                        val lessonsRaw = doc.get("lessons") as? List<*> ?: return@mapNotNull null

                        val lessonList = lessonsRaw.mapNotNull { lessonObj ->
                            val lessonMap = lessonObj as? Map<*, *> ?: return@mapNotNull null
                            val lessonId = lessonMap["id"] as? String ?: return@mapNotNull null
                            val lessonTitle =
                                lessonMap["title"] as? String ?: return@mapNotNull null
                            val questionsRaw =
                                lessonMap["questions"] as? List<*> ?: return@mapNotNull null

                            val questionList = questionsRaw.mapNotNull { questionObj ->
                                val questionMap =
                                    questionObj as? Map<*, *> ?: return@mapNotNull null
                                val number = (questionMap["number"] as? Long)?.toInt()
                                    ?: return@mapNotNull null
                                val questionText =
                                    questionMap["question"] as? String ?: return@mapNotNull null
                                val options =
                                    questionMap["options"] as? List<*> ?: return@mapNotNull null
                                val correctAnswerIndex =
                                    (questionMap["correctAnswerIndex"] as? Long)?.toInt()
                                        ?: return@mapNotNull null

                                val optionStrings = options.mapNotNull { it as? String }
                                if (optionStrings.size != options.size) return@mapNotNull null

                                Question(
                                    number = number,
                                    question = questionText,
                                    options = optionStrings,
                                    correctAnswerIndex = correctAnswerIndex
                                )
                            }

                            Lesson(
                                id = lessonId,
                                title = lessonTitle,
                                questions = questionList
                            )
                        }

                        Chapter(
                            id = doc.id,
                            title = title,
                            lessons = lessonList
                        )

                    } catch (e: Exception) {
                        Log.e("ChapterViewModel", "Error parsing chapter", e)
                        null
                    }
                }

                _chapters.value = chaptersList
            }
            .addOnFailureListener { exception ->
                Log.e("ChapterViewModel", "Error fetching chapters", exception)
            }
    }
}

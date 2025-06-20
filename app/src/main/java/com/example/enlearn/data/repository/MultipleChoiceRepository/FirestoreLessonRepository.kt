package com.example.enlearn.data.repository.MultipleChoiceRepository

import com.example.enlearn.data.model.MultipleChoiceOject.Chapter
import com.example.enlearn.data.model.MultipleChoiceOject.Lesson

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreLessonRepository : LessonRepository {

    // Lấy instance của Firestore
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getLesson(chapterId: String, lessonId: String): Result<Lesson> {
        return try {
            // 1. Trỏ đến document của chapter cụ thể
            val documentSnapshot = db.collection("chapters").document(chapterId).get().await()

            // 2. Chuyển đổi document thành đối tượng ChapterData
            val chapter = documentSnapshot.toObject(Chapter::class.java)

            // 3. Tìm lesson cụ thể trong danh sách lessons của chapter đó
            val lesson = chapter?.lessons?.find { it.id == lessonId }

            if (lesson != null) {
                Result.success(lesson)
            } else {
                Result.failure(Exception("Lesson with id=$lessonId not found in chapter id=$chapterId"))
            }
        } catch (e: Exception) {
            // Bắt các lỗi mạng hoặc lỗi chuyển đổi dữ liệu
            Result.failure(e)
        }
    }
}
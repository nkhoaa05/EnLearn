package com.example.enlearn.data.repository.MultipleChoiceRepository

import com.example.enlearn.data.model.MultipleChoiceOject.LessonData

interface LessonRepository {
    // Lấy một bài học cụ thể dựa vào ID của chapter và lesson
    suspend fun getLesson(chapterId: String, lessonId: String): Result<LessonData>
}
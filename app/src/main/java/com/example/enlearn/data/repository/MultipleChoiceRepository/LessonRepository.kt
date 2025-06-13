package com.example.enlearn.data.repository.MultipleChoiceRepository

import com.example.enlearn.data.model.MultipleChoiceOject.Question

interface LessonRepository {
    // Lấy câu hỏi tiếp theo, trả về null nếu hết bài học
    suspend fun getNextQuestion(): Question?
}
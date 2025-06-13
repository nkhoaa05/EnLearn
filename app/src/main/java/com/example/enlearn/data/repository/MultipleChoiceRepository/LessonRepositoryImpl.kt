package com.example.enlearn.data.repository.MultipleChoiceRepository

import com.example.enlearn.data.model.MultipleChoiceOject.AnswerOption
import com.example.enlearn.data.model.MultipleChoiceOject.Question
import kotlinx.coroutines.delay

class LessonRepositoryImpl : LessonRepository {

    private var questionIndex = 0
    private val questions = listOf(
        Question(
            id = "q1",
            questionText = "Hello",
            options = listOf(
                AnswerOption("o1", "Cảm Ơn"),
                AnswerOption("o2", "Xin Chào"),
                AnswerOption("o3", "Tạm Biệt")
            ),
            correctOptionId = "o2"
        ),
        // Thêm các câu hỏi khác ở đây...
    )

    override suspend fun getNextQuestion(): Question? {
        delay(500) // Giả lập độ trễ mạng/database
        return if (questionIndex < questions.size) {
            questions[questionIndex++]
        } else {
            null
        }
    }
}
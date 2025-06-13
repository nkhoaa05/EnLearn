package com.example.enlearn.data.model.MultipleChoiceOject

data class Question(
    val id: String,
    val questionText: String, // Ví dụ: "Hello"
    val options: List<AnswerOption>,
    val correctOptionId: String
)
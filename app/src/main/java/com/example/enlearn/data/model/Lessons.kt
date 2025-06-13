package com.example.enlearn.data.model

data class Lesson(
    val id: String = "",
    val title: String = "",
    val questions: List<Question> = emptyList()
)

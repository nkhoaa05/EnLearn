package com.example.enlearn.data.model

data class Questions(
    val id: String = "",
    val lessonId: String = "",
    val questionText: String = "",
    val options: List<String> = listOf(),
    val correctAnswerIndex: Int = 0
)

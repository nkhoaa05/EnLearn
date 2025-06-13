package com.example.enlearn.data.model

data class Question(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0
)

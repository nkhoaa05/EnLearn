package com.example.enlearn.data.model

data class Question(
    val number: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

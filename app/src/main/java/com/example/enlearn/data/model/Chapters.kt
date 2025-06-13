package com.example.enlearn.data.model

data class Chapter(
    val id: String = "",
    val title: String = "",
    val lessons: List<Lesson> = emptyList()
)


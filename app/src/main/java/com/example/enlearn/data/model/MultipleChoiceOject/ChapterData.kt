package com.example.enlearn.data.model.MultipleChoiceOject

import com.example.enlearn.data.model.Lesson

data class Chapter(
    val id: String = "",
    val title: String = "",
    val lessons: List<Lesson> = emptyList()
) {
    constructor() : this("", "", emptyList())
}
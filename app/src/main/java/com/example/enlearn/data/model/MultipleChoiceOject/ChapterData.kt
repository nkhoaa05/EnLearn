package com.example.enlearn.data.model.MultipleChoiceOject

import com.google.firebase.firestore.IgnoreExtraProperties

data class Chapter(
    val id: String = "",
    val title: String = "",
    val lessons: List<Lesson> = emptyList()
) {
    constructor() : this("", "", emptyList())
}
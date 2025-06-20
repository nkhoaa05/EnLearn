package com.example.enlearn.data.model.MultipleChoiceOject
import com.google.firebase.firestore.IgnoreExtraProperties

data class Lesson(
    val id: String = "",
    val title: String = "",
    // Quan trọng: questions sẽ không được tải ở màn hình danh sách
    @get:com.google.firebase.firestore.Exclude
    val questions: List<Question> = emptyList()
) {
    constructor() : this("", "", emptyList())
}
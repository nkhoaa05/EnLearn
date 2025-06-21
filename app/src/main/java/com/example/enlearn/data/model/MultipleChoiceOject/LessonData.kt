package com.example.enlearn.data.model.MultipleChoiceOject

data class LessonData(
    val id: String = "",
    val title: String = "",
    // Quan trọng: questions sẽ không được tải ở màn hình danh sách
    @get:com.google.firebase.firestore.Exclude
    val questions: List<Question> = emptyList()
) {
    constructor() : this("", "", emptyList())
}
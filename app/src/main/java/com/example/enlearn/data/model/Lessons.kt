package com.example.enlearn.data.model

import com.google.firebase.firestore.Exclude

data class Lesson(
    val id: String,
    val title: String,
    val chapterId: String = "",

    // Dùng @get:Exclude để không lưu trường này vào Firestore,
    // và cũng không tải nó về khi lấy danh sách lesson.
    @get:Exclude
    val questions: List<Question> = emptyList()
){
    constructor() : this("", "", "", emptyList())
}

package com.example.enlearn.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Trạng thái của một bài học
enum class LessonStatus {
    NOT_STARTED,
    IN_PROGRESS, // Đang học
    COMPLETED    // Đã hoàn thành
}

data class Progress(
    val chapterId: String = "",
    val lessonId: String = "",
    val status: String = LessonStatus.NOT_STARTED.name, // Lưu dưới dạng String để tương thích với Firestore
    val score: Int = 0,
    val totalQuestions: Int = 0,
    @ServerTimestamp val lastAccessed: Date? = null // Thời gian truy cập cuối cùng
) {
    // Constructor rỗng cho Firestore
    constructor() : this("", "", LessonStatus.NOT_STARTED.name, 0, 0, null)
}

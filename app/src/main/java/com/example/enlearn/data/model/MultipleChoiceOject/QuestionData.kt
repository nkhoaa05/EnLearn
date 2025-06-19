package com.example.enlearn.data.model.MultipleChoiceOject

import com.google.firebase.firestore.IgnoreExtraProperties

// Annotation này giúp bỏ qua các trường không xác định trong JSON khi chuyển đổi
@IgnoreExtraProperties
data class QuestionData(
    val number: Int = 0,
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = -1
)
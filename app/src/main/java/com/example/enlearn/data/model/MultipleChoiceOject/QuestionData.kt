package com.example.enlearn.data.model.MultipleChoiceOject

import com.google.firebase.firestore.IgnoreExtraProperties

data class Question(
    val number: Int = 0,
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = -1
) {
    // Constructor rỗng bắt buộc phải có để Firestore tự động chuyển đổi
    constructor() : this(0, "", emptyList(), -1)
}
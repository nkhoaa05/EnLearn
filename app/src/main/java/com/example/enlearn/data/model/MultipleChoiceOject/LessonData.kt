package com.example.enlearn.data.model.MultipleChoiceOject
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class LessonData(
    val id: String = "",
    val title: String = "",
    val questions: List<QuestionData> = emptyList()
)
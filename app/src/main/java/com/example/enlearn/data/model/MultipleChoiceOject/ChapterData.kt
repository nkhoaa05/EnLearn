package com.example.enlearn.data.model.MultipleChoiceOject

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChapterData(
    val id: String = "",
    val title: String = "",
    val lessons: List<LessonData> = emptyList()
)
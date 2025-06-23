package com.example.enlearn.ui.ViewModel.MultipleChoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.enlearn.data.repository.MultipleChoiceRepository.FirestoreLessonRepository
import com.example.enlearn.ui.viewmodel.MultipleChoiceViewModel

class MultipleChoiceViewModelFactory(
    private val chapterId: String,
    private val lessonId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MultipleChoiceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MultipleChoiceViewModel(
                chapterId = chapterId,
                lessonId = lessonId
            ) as T
        }
        // Nếu không đúng class, báo lỗi
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
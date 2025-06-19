package com.example.enlearn.ui.ViewModel.MultipleChoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.enlearn.data.repository.MultipleChoiceRepository.FirestoreLessonRepository
import com.example.enlearn.ui.viewmodel.MultipleChoiceViewModel

// Factory này nhận các ID cần thiết để biết phải tải bài học nào
class MultipleChoiceViewModelFactory(
    private val chapterId: String,
    private val lessonId: String
) : ViewModelProvider.Factory {

    // Ghi đè phương thức create để cung cấp phiên bản ViewModel của riêng chúng ta
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra xem class được yêu cầu có phải là MultipleChoiceViewModel không
        if (modelClass.isAssignableFrom(MultipleChoiceViewModel::class.java)) {
            // Nếu đúng, tạo một instance mới và truyền repository cùng các ID vào
            @Suppress("UNCHECKED_CAST")
            return MultipleChoiceViewModel(
                lessonRepository = FirestoreLessonRepository(), // Khởi tạo repository ở đây
                chapterId = chapterId,
                lessonId = lessonId
            ) as T
        }
        // Nếu không đúng class, báo lỗi
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.enlearn.ui.viewModel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.enlearn.auth.GoogleAuthRepository
import com.example.enlearn.data.AuthRepository
import com.example.enlearn.data.model.User
import com.example.enlearn.utils.AuthResultCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val googleAuthRepository = GoogleAuthRepository(FirebaseAuth.getInstance())
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "LoginViewModel"

    // LiveData để thông báo cho UI về trạng thái, chỉ UI quan tâm _appUser
    private val _appUser = MutableLiveData<User?>()
    val appUser: LiveData<User?> = _appUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _startGoogleSignIn = MutableLiveData<Unit>()
    val startGoogleSignIn: LiveData<Unit> = _startGoogleSignIn

    init {
        // Kiểm tra nếu người dùng đã đăng nhập từ trước
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            loadUserFromFirestore(currentUser.uid)
        }
    }

    // --- CÁC HÀM XỬ LÝ CHÍNH ---

    fun login(email: String, password: String) {
        _isLoading.value = true
        authRepository.login(email, password, object : AuthResultCallback {
            override fun onSuccess(firebaseUser: FirebaseUser?) {
                if (firebaseUser != null) {
                    // Sau khi login thành công, tải thông tin từ Firestore
                    loadUserFromFirestore(firebaseUser.uid)
                } else {
                    handleLoginFailure("Login failed: FirebaseUser is null.")
                }
            }

            override fun onFailure(errorMessage: String) {
                handleLoginFailure(errorMessage)
            }
        })
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        _isLoading.value = true
        authRepository.register(email, password, firstName, lastName, object : AuthResultCallback {
            override fun onSuccess(firebaseUser: FirebaseUser?) {
                if (firebaseUser != null) {
                    // Tạo đối tượng User của bạn và lưu nó
                    val newUser = User(
                        id = firebaseUser.uid,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        fullName = "$firstName $lastName".trim(),
                        isGoogleUser = false
                    )
                    saveUserToFirestore(newUser, true)
                } else {
                    handleLoginFailure("Registration failed: FirebaseUser is null.")
                }
            }

            override fun onFailure(errorMessage: String) {
                handleLoginFailure(errorMessage)
            }
        })
    }

    fun onGoogleSignInClicked() {
        _startGoogleSignIn.value = Unit
    }

    fun getGoogleSignInIntent(context: Context): Intent {
        return googleAuthRepository.getGoogleSignInClient(context).signInIntent
    }

    fun loginWithGoogle(data: Intent?) {
        _isLoading.value = true
        googleAuthRepository.handleSignInResult(
            data,
            onSuccess = { firebaseUser -> // firebaseUser ở đây là FirebaseUser?
                // SỬA Ở ĐÂY: Thêm kiểm tra null
                if (firebaseUser != null) {
                    // Nếu không null, gọi hàm checkAndSave
                    checkAndSaveGoogleUser(firebaseUser)
                } else {
                    // Nếu null, xử lý lỗi
                    handleLoginFailure("Google Sign-In failed: User is null.")
                }
            },
            onError = { e ->
                handleLoginFailure(e.message ?: "Google Sign-In failed.")
            }
        )
    }

    fun logout(context: Context) {
        FirebaseAuth.getInstance().signOut()
        googleAuthRepository.getGoogleSignInClient(context).signOut()
        _appUser.value = null
        _loginSuccess.value = false
    }

    // --- CÁC HÀM HỖ TRỢ ---

    private fun loadUserFromFirestore(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    _appUser.postValue(user)
                    _loginSuccess.postValue(true)
                    Log.d(TAG, "User data loaded successfully: ${user?.fullName}")
                } else {
                    // Trường hợp này ít xảy ra nếu saveUser logic đúng
                    handleLoginFailure("User not found in Firestore.")
                }
                _isLoading.postValue(false)
            }
            .addOnFailureListener { e ->
                handleLoginFailure("Failed to load user data: ${e.message}")
            }
    }

    private fun saveUserToFirestore(user: User, isNewUser: Boolean) {
        firestore.collection("users").document(user.id)
            // SỬA Ở ĐÂY: Dùng SetOptions.merge()
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "User data saved to Firestore successfully.")
                loadUserFromFirestore(user.id)
            }
            .addOnFailureListener { e ->
                handleLoginFailure("Failed to save user data: ${e.message}")
            }
    }

    private fun checkAndSaveGoogleUser(firebaseUser: FirebaseUser) {
        val userRef = firestore.collection("users").document(firebaseUser.uid)
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                // Người dùng Google mới, tạo và lưu
                val nameParts = firebaseUser.displayName?.trim()?.split(" ") ?: listOf()
                val newUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    fullName = firebaseUser.displayName ?: "",
                    firstName = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else firebaseUser.displayName ?: "",
                    lastName = if (nameParts.isNotEmpty()) nameParts.first() else "",
                    isGoogleUser = true
                )
                saveUserToFirestore(newUser, true)
            } else {
                // Người dùng Google đã tồn tại, chỉ cần tải dữ liệu của họ
                loadUserFromFirestore(firebaseUser.uid)
            }
        }
    }

    private fun handleLoginFailure(errorMessage: String) {
        Log.e(TAG, errorMessage)
        _error.postValue(errorMessage)
        _isLoading.postValue(false)
        _loginSuccess.postValue(false)
    }
}
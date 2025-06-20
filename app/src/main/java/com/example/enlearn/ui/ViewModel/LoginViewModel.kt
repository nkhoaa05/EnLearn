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


class LoginViewModel : ViewModel() {

    // Firebase Authentication
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val googleAuthRepository = GoogleAuthRepository(firebaseAuth)
    private val authRepository = AuthRepository()

    // Firestore lưu trữ dữ liệu người dùng
    private val firestore = FirebaseFirestore.getInstance()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _appUser = MutableLiveData<User>()
    val appUser: LiveData<User> = _appUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _startGoogleSignIn = MutableLiveData<Unit>()
    val startGoogleSignIn: LiveData<Unit> = _startGoogleSignIn

    // Login
    fun login(email: String, password: String) {
        authRepository.login(email, password, object : AuthResultCallback {
            override fun onSuccess(user: FirebaseUser?) {
                if (user != null) {
                    val appUser = firebaseUserToAppUser(user)
                    _user.postValue(user)
                    _appUser.postValue(appUser)
                    saveUserToFirestore(appUser)  // Lưu vào Firestore sau đăng nhập
                    loadUserFromFirestore(user.uid)
                } else {
                    _error.postValue("Đăng nhập thất bại: user null")
                }
            }

            override fun onFailure(errorMessage: String) {
                _error.postValue(errorMessage)
            }
        })
    }

    //Sign up
    fun register(firstName: String, lastName: String, email: String, password: String) {
        authRepository.register(email, password, firstName, lastName, object : AuthResultCallback {
            override fun onSuccess(user: FirebaseUser?) {
                val currentUser = firebaseAuth.currentUser
                if (currentUser == null) {
                    _error.postValue("Đăng ký thành công nhưng không lấy được user hiện tại")
                    return
                }

                val appUser = firebaseUserToAppUser(currentUser).copy(
                    firstName = firstName,
                    lastName = lastName,
                    fullName = "$lastName $firstName"
                )
                _user.postValue(currentUser)
                _appUser.postValue(appUser)
                saveUserToFirestore(appUser)  // Lưu vào Firestore sau đăng ký
            }

            override fun onFailure(errorMessage: String) {
                _error.postValue(errorMessage)
            }
        })
    }

    fun onGoogleSignInClicked() {
        _startGoogleSignIn.value = Unit
    }

    // Trả về Intent Google Sign-In đúng
    fun getGoogleSignInIntent(context: Context): Intent {
        return googleAuthRepository.getGoogleSignInClient(context).signInIntent
    }

    // Google Login
    fun loginWithGoogle(data: Intent?) {
        googleAuthRepository.handleSignInResult(
            data,
            onSuccess = {
                val firebaseUser = googleAuthRepository.getCurrentUser()
                val appUser = firebaseUserToAppUser(firebaseUser)
                _user.value = firebaseUser
                _appUser.value = appUser
                saveUserToFirestore(appUser)
                if (firebaseUser != null) {
                    loadUserFromFirestore(firebaseUser.uid)
                }
            },
            onError = { e ->
                _error.value = e.message ?: "Unknown error"
            }
        )
    }

    // Logout
    fun logout(context: Context) {
        // Đăng xuất Firebase
        firebaseAuth.signOut()

        // Đăng xuất Google
        googleAuthRepository.getGoogleSignInClient(context).signOut()

        // Cập nhật trạng thái user
        _user.value = null
    }


    fun firebaseUserToAppUser(firebaseUser: FirebaseUser?): User {
        if (firebaseUser == null) return User()

        val isGoogle = firebaseUser.providerData.any { it.providerId == "google.com" }
        val fullName = firebaseUser.displayName ?: ""
        val email = firebaseUser.email ?: ""
        val uid = firebaseUser.uid

        var firstName: String = ""
        var lastName: String = ""

        if (isGoogle) {
            val nameParts = fullName.trim().split(" ")
            if (nameParts.size >= 2) {
                lastName = nameParts.first()
                firstName = nameParts.drop(1).joinToString(" ")
            } else {
                firstName = fullName // fallback
            }
        }

        return User(
            id = uid,
            email = email,
            fullName = fullName,
            firstName = firstName,
            lastName = lastName,
            isGoogleUser = isGoogle
        )
    }

    // Lưu lại dữ liệu tài khoản người dùng
    private fun saveUserToFirestore(user: User) {
        if (user.id.isBlank()) {
            _error.postValue("User id is blank, không lưu được user")
            return
        }

        val userRef = firestore.collection("users").document(user.id)

        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val data = hashMapOf(
                    "id" to user.id,
                    "email" to user.email,
                    "fullName" to user.fullName,
                    "isGoogleUser" to user.isGoogleUser,
                    "firstName" to user.firstName,
                    "lastName" to user.lastName,
                    "progress" to user.progress.map {
                        mapOf(
                            "chapterId" to it.chapterId,
                            "lessonId" to it.lessonId,
                            "completed" to it.completed
                        )
                    }
                )

                userRef.set(data)
                    .addOnSuccessListener {
                        println("Lưu user mới lên Firestore thành công: ${user.id}")
                    }
                    .addOnFailureListener { e ->
                        _error.postValue("Lỗi lưu dữ liệu người dùng: ${e.message}")
                    }
            } else {
                println("Người dùng đã tồn tại trong Firestore: ${user.id}")
            }
        }.addOnFailureListener { e ->
            _error.postValue("Lỗi kiểm tra người dùng Firestore: ${e.message}")
        }

    }

    fun loadUserFromFirestore(uid: String) {
        val TAG = "LoadUser"

        Log.d(TAG, "Bắt đầu tải user từ Firestore với uid: $uid")

        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                Log.d(TAG, "Kết nối Firestore thành công.")

                if (document.exists()) {
                    Log.d(TAG, "Tìm thấy document user: ${document.data}")

                    val user = User(
                        id = document.id,
                        email = document.getString("email") ?: "",
                        fullName = document.getString("fullName") ?: "",
                        firstName = document.getString("firstName") ?: "",
                        lastName = document.getString("lastName") ?: "",
                        isGoogleUser = document.getBoolean("isGoogleUser") ?: false
                    )

                    Log.d(TAG, "Tạo đối tượng User: $user")
                    Log.d("ProfileDebug", "User loaded: ${user.fullName}, Email: ${user.email}")


                    _appUser.value = user
                } else {
                    Log.e(TAG, "Không tìm thấy người dùng trong Firestore với UID: $uid")
                    _error.value = "Người dùng không tồn tại trong Firestore"
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Lỗi khi load user từ Firestore: ${e.message}", e)
                _error.value = "Lỗi khi load user: ${e.message}"
            }
    }

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let {
            loadUserFromFirestore(it)
        }
    }
}


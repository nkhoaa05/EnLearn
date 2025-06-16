package com.example.enlearn.ui.viewModel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.enlearn.auth.GoogleAuthRepository
import com.example.enlearn.data.AuthRepository
import com.example.enlearn.utils.AuthResultCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val googleAuthRepository = GoogleAuthRepository(firebaseAuth)
    private val authRepository = AuthRepository()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _startGoogleSignIn = MutableLiveData<Unit>()
    val startGoogleSignIn: LiveData<Unit> = _startGoogleSignIn

    fun login(email: String, password: String) {
        authRepository.login(email, password, object : AuthResultCallback {
            override fun onSuccess(user: FirebaseUser?) {
                if (user != null) {
                    _user.postValue(user)
                } else {
                    _error.postValue("Đăng nhập thất bại: user null")
                }
            }

            override fun onFailure(errorMessage: String) {
                _error.postValue(errorMessage)
            }
        })
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        authRepository.register(email, password, firstName, lastName, object : AuthResultCallback {
            override fun onSuccess(user: FirebaseUser?) {
                _user.postValue(user)
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

    fun loginWithGoogle(data: Intent?) {
        googleAuthRepository.handleSignInResult(
            data,
            onSuccess = {
                _user.value = googleAuthRepository.getCurrentUser()
            },
            onError = { e ->
                _error.value = e.message ?: "Unknown error"
            }
        )
    }
}

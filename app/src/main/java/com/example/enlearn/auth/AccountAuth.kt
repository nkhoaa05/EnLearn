package com.example.enlearn.data

import com.example.enlearn.utils.AuthResultCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest

class AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, callback: AuthResultCallback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.onSuccess(firebaseAuth.currentUser)
                } else {
                    val errorMessage = when (val exception = task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            when (exception.errorCode) {
                                "ERROR_INVALID_EMAIL" -> "Email không đúng định dạng"
                                "ERROR_WRONG_PASSWORD" -> "Sai mật khẩu"
                                else -> "Thông tin đăng nhập không hợp lệ"
                            }
                        }

                        is FirebaseAuthInvalidUserException -> {
                            when (exception.errorCode) {
                                "ERROR_USER_NOT_FOUND" -> "Người dùng không tồn tại"
                                "ERROR_USER_DISABLED" -> "Tài khoản đã bị vô hiệu hóa"
                                else -> "Tài khoản không hợp lệ"
                            }
                        }

                        else -> exception?.localizedMessage ?: "Đăng nhập thất bại"
                    }
                    callback.onFailure(errorMessage)
                }
            }
    }


    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        callback: AuthResultCallback
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val fullName = "$firstName $lastName"
                    val profileUpdates = userProfileChangeRequest {
                        displayName = fullName
                    }
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            callback.onSuccess(user)
                        } else {
                            callback.onFailure(
                                updateTask.exception?.message ?: "Update profile failed"
                            )
                        }
                    }
                } else {
                    val errorMessage = when (val exception = task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            when (exception.errorCode) {
                                "ERROR_INVALID_EMAIL" -> "Email không hợp lệ"
                                else -> "Thông tin đăng ký không hợp lệ"
                            }
                        }

                        is FirebaseAuthUserCollisionException -> {
                            "Email đã được sử dụng"
                        }

                        is FirebaseAuthException -> {
                            when (exception.errorCode) {
                                "ERROR_OPERATION_NOT_ALLOWED" -> "Chức năng đăng ký đang bị tắt"
                                else -> exception.localizedMessage ?: "Đăng ký thất bại"
                            }
                        }

                        else -> exception?.localizedMessage ?: "Đăng ký thất bại"
                    }
                    callback.onFailure(errorMessage)

                }
            }
    }

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
}

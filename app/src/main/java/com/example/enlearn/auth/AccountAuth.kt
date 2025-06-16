package com.example.enlearn.data

import com.example.enlearn.utils.AuthResultCallback
import com.google.firebase.auth.FirebaseAuth
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
                    callback.onFailure(task.exception?.message ?: "Đăng nhập thất bại")
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
                    callback.onFailure(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
}

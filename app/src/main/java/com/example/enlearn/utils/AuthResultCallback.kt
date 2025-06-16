package com.example.enlearn.utils

import com.google.firebase.auth.FirebaseUser

interface AuthResultCallback {
    fun onSuccess(user: FirebaseUser?)
    fun onFailure(errorMessage: String)
}

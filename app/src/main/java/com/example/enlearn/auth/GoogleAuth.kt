package com.example.enlearn.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthRepository(private val firebaseAuth: FirebaseAuth) {

    fun getCurrentUser() = firebaseAuth.currentUser

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("874049449773-poqtoa6ri0o4h3i9vsb2umsk762h6kaf.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun handleSignInResult(
        data: Intent?,
        onSuccess: (user: com.google.firebase.auth.FirebaseUser?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (!idToken.isNullOrEmpty()) {
                firebaseAuthWithGoogle(idToken, onSuccess, onError)
            } else {
                onError(Exception("ID token is null or empty"))
            }
        } catch (e: ApiException) {
            onError(e)
        }
    }

    private fun firebaseAuthWithGoogle(
        idToken: String,
        onSuccess: (user: com.google.firebase.auth.FirebaseUser?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    onSuccess(user)
                } else {
                    onError(task.exception ?: Exception("Firebase authentication failed"))
                }
            }
    }

}

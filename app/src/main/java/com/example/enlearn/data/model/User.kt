package com.example.enlearn.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val isGoogleUser: Boolean = false
) {
    fun displayName(): String {
        return if (isGoogleUser) {
            fullName ?: ""
        } else {
            listOfNotNull(lastName, firstName).joinToString(" ").trim()
        }
    }


    fun displayEmail(): String {
        return email.ifBlank { "Unknown Email" }
    }
}

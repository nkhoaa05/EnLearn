package com.example.enlearn.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val isGoogleUser: Boolean = false,
    var progress: List<Progress> = emptyList()
) {
    fun displayName(): String {
        return if (isGoogleUser) {
            fullName
        } else {
            listOfNotNull(lastName, firstName).joinToString(" ").trim()
        }
    }


    fun displayEmail(): String {
        return email.ifBlank { "Unknown Email" }
    }
}

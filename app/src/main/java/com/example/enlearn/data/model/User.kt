package com.example.enlearn.data.model

import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    @get:PropertyName("isGoogleUser") @set:PropertyName("isGoogleUser")
    var isGoogleUser: Boolean = false,

    // VÀ SỬA Ở ĐÂY
    @get:PropertyName("progress") @set:PropertyName("progress")
    var progress: List<Progress> = emptyList()

) {
    constructor() : this("", "", "", "", "", false, emptyList())

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

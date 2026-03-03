package com.bc220424990.app.data.model

/**
 * User data model
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val role: UserRole = UserRole.USER
)

enum class UserRole {
    USER,
    ADMIN
}


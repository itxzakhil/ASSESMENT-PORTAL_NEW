package com.example.data.model

enum class UserRole {
    ADMIN,
    STUDENT
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val rollNumber: String? = null,
    val department: String = "Computer Science & Engineering",
    val semester: String = "6th Sem"
)

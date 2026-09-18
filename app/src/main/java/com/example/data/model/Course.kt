package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val title: String,
    val description: String,
    val instructor: String,
    val department: String,
    val totalQuestions: Int = 0,
    val durationMinutes: Int = 90,
    val difficulty: String = "Intermediate",
    val createdAt: Long = System.currentTimeMillis()
)

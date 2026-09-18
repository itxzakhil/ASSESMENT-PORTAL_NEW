package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "coding_questions",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("courseId")]
)
data class CodingQuestion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val title: String,
    val difficulty: String = "EASY", // EASY, MEDIUM, HARD
    val timeLimitSeconds: Double = 2.0,
    val memoryLimitMB: Int = 256,
    val description: String,
    val constraints: String,
    val sampleInput: String,
    val sampleOutput: String,
    val starterCodePython: String,
    val starterCodeJava: String,
    val starterCodeCpp: String,
    val maxScore: Int = 100,
    val tags: String = "Algorithms, Arrays"
)

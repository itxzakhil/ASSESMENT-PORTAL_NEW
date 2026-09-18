package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "test_cases",
    foreignKeys = [
        ForeignKey(
            entity = CodingQuestion::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("questionId")]
)
data class TestCase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long,
    val input: String,
    val expectedOutput: String,
    val isHidden: Boolean = false, // false = Public sample test case, true = Hidden grading edge case
    val weight: Int = 20, // percentage or points weight
    val label: String = "Test Case"
)

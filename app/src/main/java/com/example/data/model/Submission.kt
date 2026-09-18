package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "submissions")
data class Submission(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long,
    val courseId: Long,
    val studentId: String,
    val studentName: String,
    val language: String, // PYTHON, JAVA, CPP
    val code: String,
    val status: String, // ACCEPTED, WRONG_ANSWER, RUNTIME_ERROR, TIME_LIMIT_EXCEEDED
    val score: Int, // 0 to 100
    val testCasesPassed: Int,
    val totalTestCases: Int,
    val executionTimeMs: Long,
    val memoryUsedKb: Int,
    val testResultsSummary: String, // formatted summary of test case runs
    val timestamp: Long = System.currentTimeMillis()
)

data class TestResultItem(
    val testCaseId: Long,
    val label: String,
    val input: String,
    val expectedOutput: String,
    val actualOutput: String,
    val passed: Boolean,
    val isHidden: Boolean,
    val executionTimeMs: Long,
    val errorMessage: String? = null
)

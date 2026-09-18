package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Submission
import kotlinx.coroutines.flow.Flow

@Dao
interface SubmissionDao {
    @Query("SELECT * FROM submissions ORDER BY timestamp DESC")
    fun getAllSubmissions(): Flow<List<Submission>>

    @Query("SELECT * FROM submissions WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getSubmissionsByStudent(studentId: String): Flow<List<Submission>>

    @Query("SELECT * FROM submissions WHERE questionId = :questionId ORDER BY timestamp DESC")
    fun getSubmissionsByQuestion(questionId: Long): Flow<List<Submission>>

    @Query("SELECT * FROM submissions WHERE questionId = :questionId AND studentId = :studentId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestSubmissionForQuestion(questionId: Long, studentId: String): Flow<Submission?>

    @Query("SELECT * FROM submissions WHERE questionId = :questionId AND studentId = :studentId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSubmissionForQuestionDirect(questionId: Long, studentId: String): Submission?

    @Query("SELECT * FROM submissions WHERE courseId = :courseId ORDER BY timestamp DESC")
    fun getSubmissionsByCourse(courseId: Long): Flow<List<Submission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: Submission): Long

    @Query("SELECT COUNT(*) FROM submissions")
    suspend fun getTotalSubmissionsCount(): Int

    @Query("SELECT AVG(score) FROM submissions")
    suspend fun getAverageScore(): Double?
}

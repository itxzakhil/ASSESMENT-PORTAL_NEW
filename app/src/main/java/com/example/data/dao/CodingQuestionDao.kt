package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CodingQuestion
import kotlinx.coroutines.flow.Flow

@Dao
interface CodingQuestionDao {
    @Query("SELECT * FROM coding_questions WHERE courseId = :courseId ORDER BY id ASC")
    fun getQuestionsByCourse(courseId: Long): Flow<List<CodingQuestion>>

    @Query("SELECT * FROM coding_questions WHERE courseId = :courseId ORDER BY id ASC")
    suspend fun getQuestionsByCourseDirect(courseId: Long): List<CodingQuestion>

    @Query("SELECT * FROM coding_questions WHERE id = :questionId LIMIT 1")
    fun getQuestionById(questionId: Long): Flow<CodingQuestion?>

    @Query("SELECT * FROM coding_questions WHERE id = :questionId LIMIT 1")
    suspend fun getQuestionByIdDirect(questionId: Long): CodingQuestion?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: CodingQuestion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<CodingQuestion>): List<Long>

    @Update
    suspend fun updateQuestion(question: CodingQuestion)

    @Delete
    suspend fun deleteQuestion(question: CodingQuestion)

    @Query("SELECT COUNT(*) FROM coding_questions")
    suspend fun getTotalQuestionCount(): Int
}

package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TestCase
import kotlinx.coroutines.flow.Flow

@Dao
interface TestCaseDao {
    @Query("SELECT * FROM test_cases WHERE questionId = :questionId ORDER BY id ASC")
    fun getTestCasesByQuestion(questionId: Long): Flow<List<TestCase>>

    @Query("SELECT * FROM test_cases WHERE questionId = :questionId ORDER BY id ASC")
    suspend fun getTestCasesByQuestionDirect(questionId: Long): List<TestCase>

    @Query("SELECT * FROM test_cases WHERE questionId = :questionId AND isHidden = 0 ORDER BY id ASC")
    fun getPublicTestCases(questionId: Long): Flow<List<TestCase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestCase(testCase: TestCase): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestCases(testCases: List<TestCase>): List<Long>

    @Delete
    suspend fun deleteTestCase(testCase: TestCase)
}

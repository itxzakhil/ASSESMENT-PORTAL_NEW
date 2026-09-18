package com.example.data.repository

import com.example.data.dao.CodingQuestionDao
import com.example.data.dao.CourseDao
import com.example.data.dao.SubmissionDao
import com.example.data.dao.TestCaseDao
import com.example.data.model.CodingQuestion
import com.example.data.model.Course
import com.example.data.model.Submission
import com.example.data.model.TestCase
import kotlinx.coroutines.flow.Flow

class AssessmentRepository(
    private val courseDao: CourseDao,
    private val questionDao: CodingQuestionDao,
    private val testCaseDao: TestCaseDao,
    private val submissionDao: SubmissionDao
) {
    constructor(database: com.example.data.db.AppDatabase) : this(
        database.courseDao(),
        database.codingQuestionDao(),
        database.testCaseDao(),
        database.submissionDao()
    )

    // Courses
    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()

    fun getCourseById(courseId: Long): Flow<Course?> = courseDao.getCourseById(courseId)

    suspend fun createCourse(course: Course): Long = courseDao.insertCourse(course)

    suspend fun updateCourse(course: Course) = courseDao.updateCourse(course)

    suspend fun deleteCourse(course: Course) = courseDao.deleteCourse(course)

    // Questions
    fun getQuestionsByCourse(courseId: Long): Flow<List<CodingQuestion>> =
        questionDao.getQuestionsByCourse(courseId)

    suspend fun getQuestionsByCourseDirect(courseId: Long): List<CodingQuestion> =
        questionDao.getQuestionsByCourseDirect(courseId)

    fun getQuestionById(questionId: Long): Flow<CodingQuestion?> =
        questionDao.getQuestionById(questionId)

    suspend fun getQuestionByIdDirect(questionId: Long): CodingQuestion? =
        questionDao.getQuestionByIdDirect(questionId)

    suspend fun createQuestion(question: CodingQuestion): Long =
        questionDao.insertQuestion(question)

    suspend fun deleteQuestion(question: CodingQuestion) =
        questionDao.deleteQuestion(question)

    // Test Cases
    fun getTestCasesByQuestion(questionId: Long): Flow<List<TestCase>> =
        testCaseDao.getTestCasesByQuestion(questionId)

    suspend fun getTestCasesByQuestionDirect(questionId: Long): List<TestCase> =
        testCaseDao.getTestCasesByQuestionDirect(questionId)

    fun getPublicTestCases(questionId: Long): Flow<List<TestCase>> =
        testCaseDao.getPublicTestCases(questionId)

    suspend fun addTestCase(testCase: TestCase): Long =
        testCaseDao.insertTestCase(testCase)

    suspend fun addTestCases(testCases: List<TestCase>): List<Long> =
        testCaseDao.insertTestCases(testCases)

    // Submissions
    val allSubmissions: Flow<List<Submission>> = submissionDao.getAllSubmissions()

    fun getSubmissionsByStudent(studentId: String): Flow<List<Submission>> =
        submissionDao.getSubmissionsByStudent(studentId)

    fun getLatestSubmissionForQuestion(questionId: Long, studentId: String): Flow<Submission?> =
        submissionDao.getLatestSubmissionForQuestion(questionId, studentId)

    suspend fun getLatestSubmissionForQuestionDirect(questionId: Long, studentId: String): Submission? =
        submissionDao.getLatestSubmissionForQuestionDirect(questionId, studentId)

    suspend fun saveSubmission(submission: Submission): Long =
        submissionDao.insertSubmission(submission)
}

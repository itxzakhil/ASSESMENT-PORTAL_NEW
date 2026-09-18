package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CodingQuestion
import com.example.data.model.Course
import com.example.data.model.Submission
import com.example.data.model.TestCase
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.repository.AssessmentRepository
import com.example.engine.AutoGradeResult
import com.example.engine.CodeExecutionEngine
import com.example.engine.RunOutput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    AUTH,
    ADMIN_DASHBOARD,
    ADMIN_CREATE_COURSE,
    STUDENT_COURSES,
    CODING_ENVIRONMENT,
    ANALYTICS_DASHBOARD
}

data class UiNotification(
    val message: String,
    val isError: Boolean = false,
    val id: Long = System.currentTimeMillis()
)

class AssessmentViewModel(
    private val repository: AssessmentRepository
) : ViewModel() {

    // Default users for easy testing & demoing the flow
    val adminUser = UserProfile(
        id = "ADMIN_001",
        name = "Prof. Arvind Sharma",
        email = "admin@codetantra.edu",
        role = UserRole.ADMIN,
        department = "Dept. of Computer Science & Engineering"
    )

    val defaultStudent = UserProfile(
        id = "STU_RAHUL",
        name = "Rahul Sharma",
        email = "rahul.s@student.edu",
        role = UserRole.STUDENT,
        rollNumber = "CS2024-042",
        department = "Computer Science & Engineering",
        semester = "6th Semester"
    )

    val studentPriya = UserProfile(
        id = "STU_PRIYA",
        name = "Priya Patel",
        email = "priya.p@student.edu",
        role = UserRole.STUDENT,
        rollNumber = "CS2024-089",
        department = "Information Technology",
        semester = "6th Semester"
    )

    // Current state
    private val _currentUser = MutableStateFlow<UserProfile>(adminUser)
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.AUTH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    // Data streams from Room
    val allCourses: StateFlow<List<Course>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubmissions: StateFlow<List<Submission>> = repository.allSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selection states
    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse.asStateFlow()

    private val _courseQuestions = MutableStateFlow<List<CodingQuestion>>(emptyList())
    val courseQuestions: StateFlow<List<CodingQuestion>> = _courseQuestions.asStateFlow()

    private val _selectedQuestion = MutableStateFlow<CodingQuestion?>(null)
    val selectedQuestion: StateFlow<CodingQuestion?> = _selectedQuestion.asStateFlow()

    private val _questionTestCases = MutableStateFlow<List<TestCase>>(emptyList())
    val questionTestCases: StateFlow<List<TestCase>> = _questionTestCases.asStateFlow()

    // Coding Environment state
    private val _selectedLanguage = MutableStateFlow("PYTHON") // PYTHON, JAVA, CPP
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _editorCode = MutableStateFlow("")
    val editorCode: StateFlow<String> = _editorCode.asStateFlow()

    private val _customInput = MutableStateFlow("")
    val customInput: StateFlow<String> = _customInput.asStateFlow()

    private val _isRunningCode = MutableStateFlow(false)
    val isRunningCode: StateFlow<Boolean> = _isRunningCode.asStateFlow()

    private val _runOutput = MutableStateFlow<RunOutput?>(null)
    val runOutput: StateFlow<RunOutput?> = _runOutput.asStateFlow()

    private val _isAutoGrading = MutableStateFlow(false)
    val isAutoGrading: StateFlow<Boolean> = _isAutoGrading.asStateFlow()

    private val _autoGradeResult = MutableStateFlow<AutoGradeResult?>(null)
    val autoGradeResult: StateFlow<AutoGradeResult?> = _autoGradeResult.asStateFlow()

    private val _latestQuestionSubmission = MutableStateFlow<Submission?>(null)
    val latestQuestionSubmission: StateFlow<Submission?> = _latestQuestionSubmission.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun loginAs(user: UserProfile) {
        _currentUser.value = user
        if (user.role == UserRole.ADMIN) {
            _currentScreen.value = AppScreen.ADMIN_DASHBOARD
        } else {
            _currentScreen.value = AppScreen.STUDENT_COURSES
        }
        showNotification("Signed in as ${user.name} (${user.role})")
    }

    fun logout() {
        _currentScreen.value = AppScreen.AUTH
    }

    fun showNotification(message: String, isError: Boolean = false) {
        _notification.value = UiNotification(message, isError)
    }

    fun dismissNotification() {
        _notification.value = null
    }

    fun selectCourse(course: Course, targetScreen: AppScreen? = null) {
        _selectedCourse.value = course
        viewModelScope.launch {
            val questions = repository.getQuestionsByCourseDirect(course.id)
            _courseQuestions.value = questions
            if (targetScreen != null) {
                _currentScreen.value = targetScreen
            } else if (_currentUser.value.role == UserRole.STUDENT) {
                _currentScreen.value = AppScreen.STUDENT_COURSES
            }
        }
    }

    fun selectQuestion(question: CodingQuestion) {
        _selectedQuestion.value = question
        viewModelScope.launch {
            val testCases = repository.getTestCasesByQuestionDirect(question.id)
            _questionTestCases.value = testCases
            _customInput.value = question.sampleInput
            
            // Set code according to current language
            updateCodeForLanguage(question, _selectedLanguage.value)

            // Fetch any prior submission by this student
            val latest = repository.getLatestSubmissionForQuestionDirect(
                question.id,
                _currentUser.value.id
            )
            _latestQuestionSubmission.value = latest

            _runOutput.value = null
            _autoGradeResult.value = null
            _currentScreen.value = AppScreen.CODING_ENVIRONMENT
        }
    }

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        _selectedQuestion.value?.let { q ->
            updateCodeForLanguage(q, language)
        }
    }

    private fun updateCodeForLanguage(question: CodingQuestion, language: String) {
        val code = when (language) {
            "PYTHON" -> question.starterCodePython
            "JAVA" -> question.starterCodeJava
            "CPP" -> question.starterCodeCpp
            else -> question.starterCodePython
        }
        _editorCode.value = code
    }

    fun updateEditorCode(code: String) {
        _editorCode.value = code
    }

    fun updateCustomInput(input: String) {
        _customInput.value = input
    }

    fun resetStarterCode() {
        _selectedQuestion.value?.let { q ->
            updateCodeForLanguage(q, _selectedLanguage.value)
            showNotification("Reset code to starter template")
        }
    }

    /**
     * Executes the code against either the custom input or first public test case
     */
    fun runCode(customInputOverride: String? = null) {
        val q = _selectedQuestion.value ?: return
        val code = _editorCode.value
        val lang = _selectedLanguage.value
        val input = customInputOverride ?: _customInput.value.ifBlank { q.sampleInput }

        _isRunningCode.value = true
        _runOutput.value = null

        viewModelScope.launch {
            val output = CodeExecutionEngine.executeSingleInput(
                question = q,
                code = code,
                language = lang,
                input = input
            )
            _runOutput.value = output
            _isRunningCode.value = false
        }
    }

    /**
     * Executes Auto-Grade against all test cases (both public and hidden),
     * awards final score, and persists submission record.
     */
    fun submitAssessment() {
        val q = _selectedQuestion.value ?: return
        val course = _selectedCourse.value ?: return
        val code = _editorCode.value
        val lang = _selectedLanguage.value
        val testCases = _questionTestCases.value

        _isAutoGrading.value = true
        _autoGradeResult.value = null

        viewModelScope.launch {
            val gradeResult = CodeExecutionEngine.autoGrade(
                question = q,
                code = code,
                language = lang,
                testCases = testCases
            )
            _autoGradeResult.value = gradeResult
            _isAutoGrading.value = false

            // Save submission to database
            val user = _currentUser.value
            val submission = Submission(
                questionId = q.id,
                courseId = course.id,
                studentId = user.id,
                studentName = user.name,
                language = lang,
                code = code,
                status = gradeResult.status,
                score = gradeResult.totalScore,
                testCasesPassed = gradeResult.passedCount,
                totalTestCases = gradeResult.totalCount,
                executionTimeMs = gradeResult.executionTimeMs,
                memoryUsedKb = gradeResult.memoryUsedKb,
                testResultsSummary = "${gradeResult.passedCount}/${gradeResult.totalCount} passed (${gradeResult.totalScore}%). ${gradeResult.feedbackMessage}"
            )
            val subId = repository.saveSubmission(submission)
            _latestQuestionSubmission.value = submission.copy(id = subId)

            showNotification(
                if (gradeResult.status == "ACCEPTED") "Assessment Submitted! Score: ${gradeResult.totalScore}/100"
                else "Assessment Submitted: Score: ${gradeResult.totalScore}/100"
            )
        }
    }

    /**
     * Admin creates a new course with assessment question and test cases
     */
    fun createCourseWithQuestions(
        courseCode: String,
        courseTitle: String,
        courseDescription: String,
        department: String,
        durationMinutes: Int,
        difficulty: String,
        questionTitle: String,
        questionDesc: String,
        constraints: String,
        sampleInput: String,
        sampleOutput: String,
        starterPython: String,
        starterJava: String,
        publicTestInput: String,
        publicTestOutput: String,
        hiddenTestInput: String,
        hiddenTestOutput: String
    ) {
        viewModelScope.launch {
            val newCourse = Course(
                code = courseCode.trim().uppercase(),
                title = courseTitle.trim(),
                description = courseDescription.trim(),
                instructor = _currentUser.value.name,
                department = department.trim(),
                totalQuestions = 1,
                durationMinutes = durationMinutes,
                difficulty = difficulty
            )
            val courseId = repository.createCourse(newCourse)

            val question = CodingQuestion(
                courseId = courseId,
                title = questionTitle.trim(),
                difficulty = difficulty.uppercase(),
                description = questionDesc.trim(),
                constraints = constraints.trim(),
                sampleInput = sampleInput.trim(),
                sampleOutput = sampleOutput.trim(),
                starterCodePython = starterPython.ifBlank {
                    """# Write your Python solution here
def solve():
    pass

solve()
"""
                },
                starterCodeJava = starterJava.ifBlank {
                    """import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Code here
    }
}
"""
                },
                starterCodeCpp = """#include <iostream>
using namespace std;

int main() {
    // Code here
    return 0;
}
""",
                maxScore = 100
            )
            val qId = repository.createQuestion(question)

            // Insert public and hidden test cases
            val testCases = mutableListOf<TestCase>()
            if (publicTestInput.isNotBlank()) {
                testCases.add(
                    TestCase(
                        questionId = qId,
                        input = publicTestInput.trim(),
                        expectedOutput = publicTestOutput.trim(),
                        isHidden = false,
                        weight = 50,
                        label = "Public Test Case 1"
                    )
                )
            }
            if (hiddenTestInput.isNotBlank()) {
                testCases.add(
                    TestCase(
                        questionId = qId,
                        input = hiddenTestInput.trim(),
                        expectedOutput = hiddenTestOutput.trim(),
                        isHidden = true,
                        weight = 50,
                        label = "Hidden Test Case 1"
                    )
                )
            }
            if (testCases.isNotEmpty()) {
                repository.addTestCases(testCases)
            }

            showNotification("Course '${newCourse.code}' created successfully!")
            _currentScreen.value = AppScreen.ADMIN_DASHBOARD
        }
    }
}

class AssessmentViewModelFactory(
    private val repository: AssessmentRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssessmentViewModel::class.java)) {
            return AssessmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

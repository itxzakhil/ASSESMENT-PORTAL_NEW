package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CodingQuestion
import com.example.data.model.Course
import com.example.data.model.Submission
import com.example.ui.theme.CodeBlue
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AssessmentViewModel

@Composable
fun StudentCoursesScreen(
    viewModel: AssessmentViewModel
) {
    val user by viewModel.currentUser.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val selectedCourse by viewModel.selectedCourse.collectAsState()
    val courseQuestions by viewModel.courseQuestions.collectAsState()
    val allSubmissions by viewModel.allSubmissions.collectAsState()

    val mySubmissions = remember(allSubmissions, user.id) {
        allSubmissions.filter { it.studentId == user.id }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Student Profile Greeting Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CodeBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Roll No: ${user.rollNumber ?: "CS2024"} • ${user.department}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Surface(
                        color = SuccessGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${mySubmissions.count { it.status == "ACCEPTED" }} Solved",
                            fontSize = 11.sp,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "AVAILABLE COURSES & CODING ASSESSMENTS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCBD5E1),
                letterSpacing = 1.sp
            )
        }

        // Course List with Questions expandable
        items(courses) { course ->
            val isSelected = selectedCourse?.id == course.id

            CourseStudentItem(
                course = course,
                isSelected = isSelected,
                questions = if (isSelected) courseQuestions else emptyList(),
                mySubmissions = mySubmissions,
                onSelectCourse = {
                    viewModel.selectCourse(course)
                },
                onSelectQuestion = { question ->
                    viewModel.selectCourse(course)
                    viewModel.selectQuestion(question)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CourseStudentItem(
    course: Course,
    isSelected: Boolean,
    questions: List<CodingQuestion>,
    mySubmissions: List<Submission>,
    onSelectCourse: () -> Unit,
    onSelectQuestion: (CodingQuestion) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectCourse() }
            .testTag("student_course_${course.code}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkSurfaceVariant else DarkSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CodeCyan.copy(alpha = 0.6f) else DarkBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = CodeBlue.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = course.code,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CodeCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when (course.difficulty) {
                            "Beginner" -> SuccessGreen.copy(alpha = 0.2f)
                            "Intermediate" -> WarningAmber.copy(alpha = 0.2f)
                            else -> Color(0xFFEC4899).copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = course.difficulty,
                            fontSize = 11.sp,
                            color = when (course.difficulty) {
                                "Beginner" -> SuccessGreen
                                "Intermediate" -> WarningAmber
                                else -> Color(0xFFF472B6)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isSelected) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = course.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = course.description,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${course.durationMinutes} mins assessment",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Text(
                    text = if (isSelected) "Tap to collapse" else "Tap to view questions (${course.totalQuestions})",
                    fontSize = 11.sp,
                    color = CodeCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Expanded Questions List
            if (isSelected) {
                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = DarkBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "CODING CHALLENGES IN THIS COURSE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCBD5E1),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (questions.isEmpty()) {
                    Text(
                        text = "Loading questions...",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    questions.forEachIndexed { index, question ->
                        val latestSub = mySubmissions.firstOrNull { it.questionId == question.id }
                        QuestionItemRow(
                            index = index + 1,
                            question = question,
                            submission = latestSub,
                            onClick = { onSelectQuestion(question) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionItemRow(
    index: Int,
    question: CodingQuestion,
    submission: Submission?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("question_item_${question.id}"),
        shape = RoundedCornerShape(8.dp),
        color = DarkBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when (submission?.status) {
                                "ACCEPTED" -> SuccessGreen.copy(alpha = 0.2f)
                                "WRONG_ANSWER" -> ErrorRed.copy(alpha = 0.2f)
                                else -> CodeBlue.copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (submission?.status == "ACCEPTED") {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "$index",
                            color = CodeCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = question.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = question.difficulty,
                            fontSize = 10.sp,
                            color = when (question.difficulty) {
                                "EASY" -> SuccessGreen
                                "MEDIUM" -> WarningAmber
                                else -> ErrorRed
                            },
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${question.maxScore} pts",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Status or Solve button
            if (submission != null) {
                Surface(
                    color = if (submission.status == "ACCEPTED") SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (submission.status == "ACCEPTED") "100/100" else "${submission.score}/100",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (submission.status == "ACCEPTED") SuccessGreen else WarningAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CodeBlue),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Solve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

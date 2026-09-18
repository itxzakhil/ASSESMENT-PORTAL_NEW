package com.example.ui.screens.admin

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AssessmentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: AssessmentViewModel
) {
    val courses by viewModel.allCourses.collectAsState()
    val submissions by viewModel.allSubmissions.collectAsState()
    var inspectedSubmission by remember { mutableStateOf<Submission?>(null) }

    val totalSubmissions = submissions.size
    val averageScore = if (submissions.isNotEmpty()) {
        submissions.map { it.score }.average().toInt()
    } else 0
    val passedCount = submissions.count { it.status == "ACCEPTED" }
    val passRate = if (totalSubmissions > 0) ((passedCount.toDouble() / totalSubmissions) * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Instructor Dashboard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Manage assessment courses, question banks, and auto-grader records",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Action: Create Course Button
        item {
            Button(
                onClick = { viewModel.navigateTo(AppScreen.ADMIN_CREATE_COURSE) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_create_course_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CodeBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create New Course & Assessment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        // Batch Performance Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Active Courses",
                    value = "${courses.size}",
                    icon = Icons.Default.Book,
                    tint = CodeCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Submissions",
                    value = "$totalSubmissions",
                    icon = Icons.Default.Code,
                    tint = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Pass Rate",
                    value = "$passRate%",
                    icon = Icons.Default.CheckCircle,
                    tint = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Courses Management Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PUBLISHED COURSES (${courses.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCBD5E1),
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_CREATE_COURSE) }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = CodeCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Course", color = CodeCyan, fontSize = 12.sp)
                }
            }
        }

        if (courses.isEmpty()) {
            item {
                EmptyStateCard("No courses found. Click '+ Create New Course' above to get started.")
            }
        } else {
            items(courses) { course ->
                CourseAdminCard(
                    course = course,
                    onOpenCourse = {
                        viewModel.selectCourse(course, AppScreen.STUDENT_COURSES)
                    }
                )
            }
        }

        // Recent Submissions Log
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "RECENT AUTO-GRADED SUBMISSIONS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCBD5E1),
                letterSpacing = 1.sp
            )
        }

        if (submissions.isEmpty()) {
            item {
                EmptyStateCard("No student submissions yet.")
            }
        } else {
            items(submissions.take(5)) { sub ->
                SubmissionRowCard(
                    submission = sub,
                    onClick = { inspectedSubmission = sub }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal to inspect code & test case summary
    inspectedSubmission?.let { sub ->
        AlertDialog(
            onDismissRequest = { inspectedSubmission = null },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Submission: ${sub.studentName}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Language: ${sub.language}", color = CodeCyan, fontSize = 12.sp)
                        Text("Score: ${sub.score}/100", color = if (sub.score >= 70) SuccessGreen else ErrorRed, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = sub.testResultsSummary,
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Source Code:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090D16))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = sub.code,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF79C0FF)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { inspectedSubmission = null }) {
                    Text("Close", color = CodeCyan)
                }
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = title, fontSize = 11.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun CourseAdminCard(
    course: Course,
    onOpenCourse: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenCourse() }
            .testTag("course_card_${course.code}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
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
                color = Color(0xFF94A3B8),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                        text = "${course.durationMinutes} mins",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${course.totalQuestions} Questions",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Text(
                    text = "View Questions →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CodeCyan
                )
            }
        }
    }
}

@Composable
fun SubmissionRowCard(
    submission: Submission,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(submission.timestamp) { dateFormat.format(Date(submission.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = submission.studentName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = submission.language,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CodeCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Surface(
                color = if (submission.status == "ACCEPTED") SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "${submission.score}/100",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (submission.status == "ACCEPTED") SuccessGreen else ErrorRed,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

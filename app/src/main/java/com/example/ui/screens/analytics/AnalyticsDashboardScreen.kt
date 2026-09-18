package com.example.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.Submission
import com.example.data.model.UserRole
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsDashboardScreen(
    viewModel: AssessmentViewModel
) {
    val user by viewModel.currentUser.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val submissions by viewModel.allSubmissions.collectAsState()

    var selectedTab by remember {
        mutableIntStateOf(if (user.role == UserRole.ADMIN) 0 else 1) // 0 = Admin/Batch, 1 = Student Personal
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Tab Selector for Analytics View
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CodeCyan,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Batch & Class Analytics", fontSize = 13.sp)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("My Grade Card", fontSize = 13.sp)
                    }
                }
            )
        }

        if (selectedTab == 0) {
            BatchAnalyticsView(courses = courses, submissions = submissions)
        } else {
            StudentPersonalAnalyticsView(
                userName = user.name,
                userRoll = user.rollNumber ?: "CS2024-042",
                submissions = submissions.filter { it.studentId == user.id }
            )
        }
    }
}

@Composable
fun BatchAnalyticsView(
    courses: List<Course>,
    submissions: List<Submission>
) {
    val totalSubmissions = submissions.size
    val averageScore = if (submissions.isNotEmpty()) {
        submissions.map { it.score }.average().toInt()
    } else 0
    val passedSubmissions = submissions.count { it.status == "ACCEPTED" }
    val passRate = if (totalSubmissions > 0) ((passedSubmissions.toDouble() / totalSubmissions) * 100).toInt() else 0

    // Leaderboard calculation
    val studentStats = remember(submissions) {
        submissions.groupBy { it.studentName }
            .map { (name, list) ->
                val highestScores = list.groupBy { it.questionId }.mapValues { entry -> entry.value.maxOf { it.score } }
                val totalScore = highestScores.values.sum()
                val solvedCount = highestScores.values.count { it == 100 }
                Triple(name, totalScore, solvedCount)
            }
            .sortedByDescending { it.second }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "BATCH PERFORMANCE OVERVIEW",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCBD5E1),
                letterSpacing = 1.sp
            )
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCardItem(
                    title = "Avg Batch Score",
                    value = "$averageScore%",
                    icon = Icons.Default.Assessment,
                    tint = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCardItem(
                    title = "Overall Pass Rate",
                    value = "$passRate%",
                    icon = Icons.Default.CheckCircle,
                    tint = CodeCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCardItem(
                    title = "Submissions",
                    value = "$totalSubmissions",
                    icon = Icons.Default.Code,
                    tint = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Student Leaderboard Section
        item {
            Text(
                text = "STUDENT LEADERBOARD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCBD5E1),
                letterSpacing = 1.sp
            )
        }

        if (studentStats.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No assessment submissions recorded yet.", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(studentStats.withIndex().toList()) { (index, stat) ->
                LeaderboardRow(
                    rank = index + 1,
                    name = stat.first,
                    totalScore = stat.second,
                    solvedCount = stat.third
                )
            }
        }

        // Course Breakdown
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "COURSE-WISE ASSESSMENT METRICS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCBD5E1),
                letterSpacing = 1.sp
            )
        }

        items(courses) { course ->
            val courseSubs = submissions.filter { it.courseId == course.id }
            val courseAvg = if (courseSubs.isNotEmpty()) courseSubs.map { it.score }.average().toInt() else 0

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = course.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            color = CodeBlue.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${courseAvg}% Avg",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CodeCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (courseAvg / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CodeCyan,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${course.totalQuestions} Coding Questions",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "${courseSubs.size} Submissions recorded",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StudentPersonalAnalyticsView(
    userName: String,
    userRoll: String,
    submissions: List<Submission>
) {
    val totalSubmissions = submissions.size
    val solvedCount = submissions.filter { it.status == "ACCEPTED" }.distinctBy { it.questionId }.size
    val avgScore = if (submissions.isNotEmpty()) submissions.map { it.score }.average().toInt() else 0
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Score Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CodeCyan.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Roll: $userRoll",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "⭐ CodeTantra Certified Candidate",
                                fontSize = 11.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Score Circle
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CodeBlue.copy(alpha = 0.2f))
                            .border(2.dp, CodeCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$avgScore%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Grade",
                                fontSize = 9.sp,
                                color = CodeCyan
                            )
                        }
                    }
                }
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCardItem(
                    title = "Problems Solved",
                    value = "$solvedCount",
                    icon = Icons.Default.CheckCircle,
                    tint = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCardItem(
                    title = "Attempts",
                    value = "$totalSubmissions",
                    icon = Icons.Default.Code,
                    tint = CodeCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCardItem(
                    title = "Rank Score",
                    value = "${submissions.sumOf { it.score }}",
                    icon = Icons.Default.EmojiEvents,
                    tint = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Submissions Log
        item {
            Text(
                text = "MY ASSESSMENT SUBMISSION HISTORY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCBD5E1),
                letterSpacing = 1.sp
            )
        }

        if (submissions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "You haven't submitted any coding assessments yet.",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(submissions) { sub ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Language: ${sub.language}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CodeCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Surface(
                                color = if (sub.status == "ACCEPTED") SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${sub.score}/100",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sub.status == "ACCEPTED") SuccessGreen else WarningAmber,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = sub.testResultsSummary,
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Time: ${sub.executionTimeMs} ms • Memory: ${sub.memoryUsedKb / 1024} MB",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = dateFormat.format(Date(sub.timestamp)),
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: Int,
    name: String,
    totalScore: Int,
    solvedCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when (rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "#$rank"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "$solvedCount Problems Solved",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Surface(
                color = CodeBlue.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "$totalScore pts",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CodeCyan,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MetricCardItem(
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
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = title, fontSize = 10.sp, color = Color(0xFF94A3B8))
        }
    }
}

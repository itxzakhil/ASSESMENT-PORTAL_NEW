package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CodingQuestion
import com.example.data.model.TestCase
import com.example.data.model.TestResultItem
import com.example.engine.AutoGradeResult
import com.example.engine.RunOutput
import com.example.ui.theme.CodeBlue
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.CodeEditorBg
import com.example.ui.theme.CodeGutter
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TerminalBlack
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AssessmentViewModel

@Composable
fun CodingEnvironmentScreen(
    viewModel: AssessmentViewModel
) {
    val question = viewModel.selectedQuestion.collectAsState().value
    val testCases by viewModel.questionTestCases.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val editorCode by viewModel.editorCode.collectAsState()
    val customInput by viewModel.customInput.collectAsState()
    val isRunningCode by viewModel.isRunningCode.collectAsState()
    val runOutput by viewModel.runOutput.collectAsState()
    val isAutoGrading by viewModel.isAutoGrading.collectAsState()
    val autoGradeResult by viewModel.autoGradeResult.collectAsState()
    val latestSubmission by viewModel.latestQuestionSubmission.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Description, 1 = Code Editor, 2 = Console / Test Run, 3 = Auto Grade
    var languageDropdownExpanded by remember { mutableStateOf(false) }
    var selectedTestCaseIdx by remember { mutableIntStateOf(0) }

    if (question == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("No question selected.", color = Color.White)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Question Header Bar
        Surface(
            color = DarkSurface,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = question.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Surface(
                                color = when (question.difficulty) {
                                    "EASY" -> SuccessGreen.copy(alpha = 0.2f)
                                    "MEDIUM" -> WarningAmber.copy(alpha = 0.2f)
                                    else -> ErrorRed.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = question.difficulty,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (question.difficulty) {
                                        "EASY" -> SuccessGreen
                                        "MEDIUM" -> WarningAmber
                                        else -> ErrorRed
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Max Score: ${question.maxScore} pts",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• Time Limit: ${question.timeLimitSeconds}s",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Language Selector
                    Box {
                        Surface(
                            onClick = { languageDropdownExpanded = true },
                            shape = RoundedCornerShape(8.dp),
                            color = CodeBlue.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CodeBlue.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = CodeCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (selectedLanguage) {
                                        "PYTHON" -> "Python 3"
                                        "JAVA" -> "Java 17"
                                        "CPP" -> "C++ 20"
                                        else -> selectedLanguage
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CodeCyan
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = languageDropdownExpanded,
                            onDismissRequest = { languageDropdownExpanded = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Python 3", color = Color.White) },
                                onClick = {
                                    viewModel.setLanguage("PYTHON")
                                    languageDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Java 17", color = Color.White) },
                                onClick = {
                                    viewModel.setLanguage("JAVA")
                                    languageDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("C++ 20", color = Color.White) },
                                onClick = {
                                    viewModel.setLanguage("CPP")
                                    languageDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Tabs Row (CodeTantra Tabs: Problem, Code Editor, Test Cases & Run, Auto Grade)
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = DarkSurfaceVariant,
            contentColor = Color.White,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = CodeCyan,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Problem", fontSize = 13.sp)
                    }
                }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Code Editor", fontSize = 13.sp)
                    }
                }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Run", fontSize = 13.sp)
                        if (runOutput != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (runOutput!!.isSuccess) SuccessGreen else ErrorRed)
                            )
                        }
                    }
                }
            )
            Tab(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Auto-Grade", fontSize = 13.sp)
                        if (autoGradeResult != null || latestSubmission != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            val score = autoGradeResult?.totalScore ?: latestSubmission?.score ?: 0
                            Surface(
                                color = if (score >= 70) SuccessGreen.copy(alpha = 0.3f) else WarningAmber.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "$score%",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (score >= 70) SuccessGreen else WarningAmber,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            )
        }

        // Tab Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                0 -> ProblemStatementTab(question, testCases)
                1 -> CodeEditorTab(
                    code = editorCode,
                    language = selectedLanguage,
                    onCodeChange = { viewModel.updateEditorCode(it) },
                    onResetCode = { viewModel.resetStarterCode() }
                )
                2 -> TestRunTab(
                    testCases = testCases,
                    customInput = customInput,
                    onCustomInputChange = { viewModel.updateCustomInput(it) },
                    selectedCaseIdx = selectedTestCaseIdx,
                    onSelectCaseIdx = { selectedTestCaseIdx = it },
                    runOutput = runOutput,
                    isRunning = isRunningCode,
                    onRunCode = { viewModel.runCode(it) }
                )
                3 -> AutoGradeTab(
                    autoGradeResult = autoGradeResult,
                    latestSubmission = latestSubmission,
                    isGrading = isAutoGrading,
                    onSubmitAssessment = { viewModel.submitAssessment() }
                )
            }
        }

        // Persistent CodeTantra Bottom Action Bar
        Surface(
            color = DarkSurface,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Run Code Button (Tests single input)
                OutlinedButton(
                    onClick = {
                        activeTab = 2
                        val currentTc = testCases.getOrNull(selectedTestCaseIdx)
                        viewModel.runCode(currentTc?.input ?: customInput)
                    },
                    enabled = !isRunningCode && !isAutoGrading,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("run_code_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CodeCyan)
                ) {
                    if (isRunningCode) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = CodeCyan,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Running...", color = CodeCyan, fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CodeCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Run Code", color = CodeCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Submit Assessment & Auto Grade Button
                Button(
                    onClick = {
                        activeTab = 3
                        viewModel.submitAssessment()
                    },
                    enabled = !isRunningCode && !isAutoGrading,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(46.dp)
                        .testTag("submit_assessment_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isAutoGrading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Auto-Grading...", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Submit & Auto Grade", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProblemStatementTab(
    question: CodingQuestion,
    testCases: List<TestCase>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Description Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PROBLEM STATEMENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CodeCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.description,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFFF1F5F9)
                )
            }
        }

        // Constraints Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CONSTRAINTS & LIMITS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningAmber,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.constraints,
                    fontSize = 13.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 20.sp
                )
            }
        }

        // Sample Input & Output
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SAMPLE TEST CASE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Sample Input:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(TerminalBlack)
                        .padding(10.dp)
                ) {
                    Text(
                        text = question.sampleInput,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFF79C0FF)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Sample Output:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(TerminalBlack)
                        .padding(10.dp)
                ) {
                    Text(
                        text = question.sampleOutput,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = SuccessGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CodeEditorTab(
    code: String,
    language: String,
    onCodeChange: (String) -> Unit,
    onResetCode: () -> Unit
) {
    val scrollState = rememberScrollState()
    val lines = code.lines()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CodeEditorBg)
    ) {
        // Editor Toolbar
        Surface(
            color = DarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Solution.${if (language == "PYTHON") "py" else if (language == "JAVA") "java" else "cpp"}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onResetCode, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Template",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text("Reset", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
            }
        }

        // Code Editor Canvas with Line Numbers
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Line numbers gutter
            Column(
                modifier = Modifier
                    .background(CodeGutter)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.End
            ) {
                for (i in 1..maxOf(lines.size, 15)) {
                    Text(
                        text = "$i",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF475569),
                        lineHeight = 20.sp
                    )
                }
            }

            // Editable Code Buffer
            BasicTextField(
                value = code,
                onValueChange = onCodeChange,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 12.dp)
                    .testTag("code_editor_field"),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 20.sp
                ),
                cursorBrush = SolidColor(CodeCyan)
            )
        }
    }
}

@Composable
fun TestRunTab(
    testCases: List<TestCase>,
    customInput: String,
    onCustomInputChange: (String) -> Unit,
    selectedCaseIdx: Int,
    onSelectCaseIdx: (Int) -> Unit,
    runOutput: RunOutput?,
    isRunning: Boolean,
    onRunCode: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Test Cases Chips Selector
        Text(
            text = "SELECT TEST CASE / CUSTOM INPUT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFCBD5E1),
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            testCases.filter { !it.isHidden }.forEachIndexed { idx, tc ->
                val isSelected = selectedCaseIdx == idx
                Surface(
                    onClick = { onSelectCaseIdx(idx) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) CodeBlue.copy(alpha = 0.3f) else DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) CodeCyan else DarkBorder
                    )
                ) {
                    Text(
                        text = "Case ${idx + 1}",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) CodeCyan else Color(0xFFCBD5E1),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Custom input chip
            val isCustomSelected = selectedCaseIdx == 999
            Surface(
                onClick = { onSelectCaseIdx(999) },
                shape = RoundedCornerShape(8.dp),
                color = if (isCustomSelected) WarningAmber.copy(alpha = 0.3f) else DarkSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCustomSelected) WarningAmber else DarkBorder
                )
            ) {
                Text(
                    text = "Custom Input",
                    fontSize = 12.sp,
                    fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCustomSelected) WarningAmber else Color(0xFFCBD5E1),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Active Input details
        val activeTestCase = if (selectedCaseIdx != 999) testCases.getOrNull(selectedCaseIdx) else null

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (activeTestCase != null) {
                    Text("Input Data:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(TerminalBlack)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = activeTestCase.input,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF79C0FF)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Expected Output:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(TerminalBlack)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = activeTestCase.expectedOutput,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = SuccessGreen
                        )
                    }
                } else {
                    Text("Custom Test Input:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customInput,
                        onValueChange = onCustomInputChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarningAmber,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    )
                }
            }
        }

        // Terminal Console Output
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = TerminalBlack),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = CodeCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EXECUTION CONSOLE (STDOUT)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (runOutput != null) {
                        Text(
                            text = "${runOutput.executionTimeMs} ms • ${runOutput.memoryUsedKb / 1024} MB",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(10.dp))

                if (isRunning) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CodeCyan, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Compiling and executing code in sandbox container...",
                            fontSize = 12.sp,
                            color = CodeCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else if (runOutput != null) {
                    if (runOutput.isSuccess) {
                        Text(
                            text = runOutput.stdout.ifEmpty { "[No output returned]" },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = Color(0xFF58A6FF)
                        )
                    } else {
                        Text(
                            text = runOutput.stderr,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = ErrorRed
                        )
                    }
                } else {
                    Text(
                        text = "Click 'Run Code' below to execute your solution against this test case.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AutoGradeTab(
    autoGradeResult: AutoGradeResult?,
    latestSubmission: com.example.data.model.Submission?,
    isGrading: Boolean,
    onSubmitAssessment: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isGrading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(44.dp), color = SuccessGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Evaluating All Test Cases...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Running Public & Hidden Edge Cases through the Auto-Grader",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        } else if (autoGradeResult != null) {
            // Grade Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (autoGradeResult.totalScore >= 70) SuccessGreen.copy(alpha = 0.5f) else WarningAmber.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = if (autoGradeResult.status == "ACCEPTED") SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = autoGradeResult.status,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (autoGradeResult.status == "ACCEPTED") SuccessGreen else WarningAmber,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${autoGradeResult.totalScore}/100",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (autoGradeResult.totalScore >= 70) SuccessGreen else WarningAmber
                    )

                    Text(
                        text = "${autoGradeResult.passedCount} of ${autoGradeResult.totalCount} Test Cases Passed",
                        fontSize = 13.sp,
                        color = Color(0xFFCBD5E1),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = autoGradeResult.feedbackMessage,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "Time: ${autoGradeResult.executionTimeMs} ms",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Memory: ${autoGradeResult.memoryUsedKb / 1024} MB",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Test Case Execution Breakdown
            Text(
                text = "INDIVIDUAL TEST CASE BREAKDOWN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCBD5E1),
                letterSpacing = 1.sp
            )

            autoGradeResult.results.forEachIndexed { index, item ->
                TestCaseResultCard(item = item, index = index + 1)
            }
        } else if (latestSubmission != null) {
            // Prior submission summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LATEST SAVED SUBMISSION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CodeCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Score: ${latestSubmission.score}/100 • Status: ${latestSubmission.status}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (latestSubmission.score >= 70) SuccessGreen else WarningAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = latestSubmission.testResultsSummary,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onSubmitAssessment,
                        colors = ButtonDefaults.buttonColors(containerColor = CodeBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Re-Evaluate & Submit Code")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = CodeCyan,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ready to Auto-Grade",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Click 'Submit & Auto Grade' below to test your solution against all public and hidden test cases.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun TestCaseResultCard(item: TestResultItem, index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.passed) SuccessGreen.copy(alpha = 0.5f) else ErrorRed.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (item.passed) Icons.Default.CheckCircle else Icons.Default.BugReport,
                        contentDescription = null,
                        tint = if (item.passed) SuccessGreen else ErrorRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (item.isHidden) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFF334155),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Hidden Edge Case",
                                fontSize = 9.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Surface(
                    color = if (item.passed) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (item.passed) "PASSED" else "FAILED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.passed) SuccessGreen else ErrorRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (!item.passed) {
                Spacer(modifier = Modifier.height(8.dp))
                if (!item.isHidden) {
                    Text(
                        text = "Expected: ${item.expectedOutput}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SuccessGreen
                    )
                    Text(
                        text = "Actual:   ${item.actualOutput.ifEmpty { "[No output]" }}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ErrorRed
                    )
                } else {
                    Text(
                        text = "Hidden edge case failed. Review corner conditions or boundary values.",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}

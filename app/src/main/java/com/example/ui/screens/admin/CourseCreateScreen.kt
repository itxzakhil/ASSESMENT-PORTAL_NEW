package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CodeBlue
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AssessmentViewModel

@Composable
fun CourseCreateScreen(
    viewModel: AssessmentViewModel
) {
    var courseCode by remember { mutableStateOf("CS205") }
    var courseTitle by remember { mutableStateOf("Applied Algorithms & Search Techniques") }
    var courseDesc by remember { mutableStateOf("Core assessments evaluating binary search, pointers, and algorithmic runtime optimization.") }
    var department by remember { mutableStateOf("Computer Science & Engineering") }
    var durationMinutes by remember { mutableStateOf("90") }
    var difficulty by remember { mutableStateOf("Intermediate") }

    // Question
    var questionTitle by remember { mutableStateOf("Binary Search on Sorted Array") }
    var questionDesc by remember {
        mutableStateOf(
            "Given an array of integers `nums` which is sorted in ascending order, and an integer `target`, write a function to search `target` in `nums`.\n\nIf `target` exists, return its index. Otherwise, return `-1`.\n\nYou must write an algorithm with `O(log n)` runtime complexity."
        )
    }
    var constraints by remember { mutableStateOf("• 1 <= nums.length <= 10^4\n• -10^4 < nums[i], target < 10^4\n• All the integers in nums are unique.\n• nums is sorted in ascending order.") }
    var sampleInput by remember { mutableStateOf("6\n-1 0 3 5 9 12\n9") }
    var sampleOutput by remember { mutableStateOf("4") }

    var starterPython by remember {
        mutableStateOf(
            """def search(nums, target):
    left, right = 0, len(nums) - 1
    while left <= right:
        mid = (left + right) // 2
        if nums[mid] == target:
            return mid
        elif nums[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return -1

# Input reader
n = int(input())
nums = list(map(int, input().split()))
target = int(input())
print(search(nums, target))
"""
        )
    }

    var starterJava by remember {
        mutableStateOf(
            """import java.util.Scanner;

public class Solution {
    public static int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        int target = sc.nextInt();
        System.out.println(search(nums, target));
    }
}
"""
        )
    }

    // Test cases
    var publicTestInput by remember { mutableStateOf("6\n-1 0 3 5 9 12\n9") }
    var publicTestOutput by remember { mutableStateOf("4") }

    var hiddenTestInput by remember { mutableStateOf("6\n-1 0 3 5 9 12\n2") }
    var hiddenTestOutput by remember { mutableStateOf("-1") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Create Course & Assessment",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Configure curriculum, coding challenge, and auto-grader test cases",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Quick template loader button
        OutlinedButton(
            onClick = {
                courseCode = "CS310"
                courseTitle = "Advanced Dynamic Programming & Graphs"
                courseDesc = "Rigorous assessments in graph traversal, memoization, and combinatorial optimization."
                questionTitle = "Climbing Stairs (DP)"
                questionDesc = "You are climbing a staircase. It takes n steps to reach the top. Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?"
                sampleInput = "3"
                sampleOutput = "3"
                publicTestInput = "3"
                publicTestOutput = "3"
                hiddenTestInput = "5"
                hiddenTestOutput = "8"
                viewModel.showNotification("Loaded DP Problem Template!")
            },
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, CodeCyan.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CodeCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("⚡ Load Preset Coding Problem Template", color = CodeCyan, fontSize = 13.sp)
        }

        // Section 1: Course Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Book, contentDescription = null, tint = CodeBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "1. Course Details",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = courseCode,
                        onValueChange = { courseCode = it },
                        label = { Text("Code (e.g. CS205)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_course_code"),
                        colors = fieldColors(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = durationMinutes,
                        onValueChange = { durationMinutes = it },
                        label = { Text("Duration (mins)") },
                        modifier = Modifier.weight(1f),
                        colors = fieldColors(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = courseTitle,
                    onValueChange = { courseTitle = it },
                    label = { Text("Course Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_course_title"),
                    colors = fieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department / Faculty") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = courseDesc,
                    onValueChange = { courseDesc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors(),
                    maxLines = 3
                )
            }
        }

        // Section 2: Coding Question
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "2. Assessment Question",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = questionTitle,
                    onValueChange = { questionTitle = it },
                    label = { Text("Question Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_question_title"),
                    colors = fieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = questionDesc,
                    onValueChange = { questionDesc = it },
                    label = { Text("Problem Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = constraints,
                    onValueChange = { constraints = it },
                    label = { Text("Constraints") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = sampleInput,
                        onValueChange = { sampleInput = it },
                        label = { Text("Sample Input") },
                        modifier = Modifier.weight(1f),
                        colors = fieldColors()
                    )
                    OutlinedTextField(
                        value = sampleOutput,
                        onValueChange = { sampleOutput = it },
                        label = { Text("Sample Output") },
                        modifier = Modifier.weight(1f),
                        colors = fieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Starter Code (Python Template)",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
                OutlinedTextField(
                    value = starterPython,
                    onValueChange = { starterPython = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors(),
                    minLines = 4,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                )
            }
        }

        // Section 3: Test Cases
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "3. Auto-Grader Test Cases",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "Public test cases are visible to students; hidden test cases are used for final score grading.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Public Test Case
                Surface(
                    color = DarkBackground,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = CodeCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Public Test Case (50% Weight)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CodeCyan)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = publicTestInput,
                            onValueChange = { publicTestInput = it },
                            label = { Text("Input") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = fieldColors()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = publicTestOutput,
                            onValueChange = { publicTestOutput = it },
                            label = { Text("Expected Output") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = fieldColors()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hidden Test Case
                Surface(
                    color = DarkBackground,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hidden Edge Case (50% Weight)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = hiddenTestInput,
                            onValueChange = { hiddenTestInput = it },
                            label = { Text("Input") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = fieldColors()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = hiddenTestOutput,
                            onValueChange = { hiddenTestOutput = it },
                            label = { Text("Expected Output") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = fieldColors()
                        )
                    }
                }
            }
        }

        // Submit & Publish Button
        Button(
            onClick = {
                val duration = durationMinutes.toIntOrNull() ?: 90
                viewModel.createCourseWithQuestions(
                    courseCode = courseCode,
                    courseTitle = courseTitle,
                    courseDescription = courseDesc,
                    department = department,
                    durationMinutes = duration,
                    difficulty = difficulty,
                    questionTitle = questionTitle,
                    questionDesc = questionDesc,
                    constraints = constraints,
                    sampleInput = sampleInput,
                    sampleOutput = sampleOutput,
                    starterPython = starterPython,
                    starterJava = starterJava,
                    publicTestInput = publicTestInput,
                    publicTestOutput = publicTestOutput,
                    hiddenTestInput = hiddenTestInput,
                    hiddenTestOutput = hiddenTestOutput
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("publish_course_button"),
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Publish Course & Assessment",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CodeCyan,
    unfocusedBorderColor = DarkBorder,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = CodeCyan,
    unfocusedLabelColor = Color(0xFF94A3B8),
    cursorColor = CodeCyan
)

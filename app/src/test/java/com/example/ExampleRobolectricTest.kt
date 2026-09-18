package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CodingQuestion
import com.example.data.model.TestCase
import com.example.engine.CodeExecutionEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CodeAssess", appName)
    }

    @Test
    fun `test code execution engine two sum auto grade`() = runBlocking {
        val question = CodingQuestion(
            courseId = 1,
            title = "Two Sum Problem",
            difficulty = "EASY",
            description = "Find indices",
            constraints = "None",
            sampleInput = "4\n2 7 11 15\n9",
            sampleOutput = "0 1",
            starterCodePython = "def twoSum(nums, target): return [0, 1]",
            starterCodeJava = "",
            starterCodeCpp = ""
        )

        val testCases = listOf(
            TestCase(
                questionId = 1,
                input = "4\n2 7 11 15\n9",
                expectedOutput = "0 1",
                isHidden = false,
                weight = 50,
                label = "Public Test Case"
            ),
            TestCase(
                questionId = 1,
                input = "3\n3 2 4\n6",
                expectedOutput = "1 2",
                isHidden = true,
                weight = 50,
                label = "Hidden Test Case"
            )
        )

        val correctCode = """
            def twoSum(nums, target):
                seen = {}
                for i, num in enumerate(nums):
                    diff = target - num
                    if diff in seen:
                        return [seen[diff], i]
                    seen[num] = i
                return []
        """.trimIndent()

        val gradeResult = CodeExecutionEngine.autoGrade(
            question = question,
            code = correctCode,
            language = "PYTHON",
            testCases = testCases
        )

        assertEquals("ACCEPTED", gradeResult.status)
        assertEquals(100, gradeResult.totalScore)
        assertEquals(2, gradeResult.passedCount)
    }
}

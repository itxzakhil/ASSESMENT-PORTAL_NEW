package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CodingQuestionDao
import com.example.data.dao.CourseDao
import com.example.data.dao.SubmissionDao
import com.example.data.dao.TestCaseDao
import com.example.data.model.CodingQuestion
import com.example.data.model.Course
import com.example.data.model.Submission
import com.example.data.model.TestCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Course::class,
        CodingQuestion::class,
        TestCase::class,
        Submission::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun codingQuestionDao(): CodingQuestionDao
    abstract fun testCaseDao(): TestCaseDao
    abstract fun submissionDao(): SubmissionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "code_assess_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val courseDao = database.courseDao()
            val questionDao = database.codingQuestionDao()
            val testCaseDao = database.testCaseDao()
            val submissionDao = database.submissionDao()

            if (courseDao.getCourseCount() > 0) return

            // Course 1: DSA in Python
            val c1Id = courseDao.insertCourse(
                Course(
                    code = "CS201",
                    title = "Data Structures & Algorithms in Python",
                    description = "Master arrays, hash maps, two pointers, strings, and computational complexity for technical interview assessments.",
                    instructor = "Prof. Arvind Sharma (Admin)",
                    department = "Dept. of Computer Science",
                    totalQuestions = 3,
                    durationMinutes = 120,
                    difficulty = "Intermediate"
                )
            )

            // Course 2: Java Programming
            val c2Id = courseDao.insertCourse(
                Course(
                    code = "CS102",
                    title = "Java Core & Object-Oriented Programming",
                    description = "Comprehensive coding assessments covering classes, recursion, string manipulations, and collection algorithms.",
                    instructor = "Dr. Radhika Sen (Admin)",
                    department = "Information Technology",
                    totalQuestions = 2,
                    durationMinutes = 90,
                    difficulty = "Beginner"
                )
            )

            // Course 3: C++ Systems Programming
            val c3Id = courseDao.insertCourse(
                Course(
                    code = "CS301",
                    title = "C++ Systems & Competitive Programming",
                    description = "Algorithmic challenges in C++ focused on memory optimization, vectors, pointers, and mathematical logic.",
                    instructor = "Prof. Arvind Sharma (Admin)",
                    department = "Dept. of Computer Science",
                    totalQuestions = 2,
                    durationMinutes = 90,
                    difficulty = "Advanced"
                )
            )

            // Questions for Course 1 (Python DSA)
            val q1Id = questionDao.insertQuestion(
                CodingQuestion(
                    courseId = c1Id,
                    title = "Two Sum Problem",
                    difficulty = "EASY",
                    timeLimitSeconds = 1.5,
                    memoryLimitMB = 256,
                    description = "Given an array of integers `nums` and an integer `target`, return the indices of the two numbers such that they add up to `target`.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.\n\nReturn the answer formatted as `[i, j]` with indices in ascending order.",
                    constraints = "• 2 <= nums.length <= 10^4\n• -10^9 <= nums[i] <= 10^9\n• -10^9 <= target <= 10^9\n• Exactly one valid answer exists.",
                    sampleInput = "4\n2 7 11 15\n9",
                    sampleOutput = "[0, 1]",
                    starterCodePython = """def two_sum(nums, target):
    # Return indices [index1, index2]
    seen = {}
    for i, num in enumerate(nums):
        diff = target - num
        if diff in seen:
            return [seen[diff], i]
        seen[num] = i
    return []

# Driver logic
n = int(input())
nums = list(map(int, input().split()))
target = int(input())
print(two_sum(nums, target))
""",
                    starterCodeJava = """import java.util.*;

public class Solution {
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] nums = new int[n];
        for(int i = 0; i < n; i++) nums[i] = sc.nextInt();
        int target = sc.nextInt();
        int[] res = twoSum(nums, target);
        System.out.println(Arrays.toString(res));
    }
}
""",
                    starterCodeCpp = """#include <iostream>
#include <vector>
#include <unordered_map>
using namespace std;

vector<int> twoSum(vector<int>& nums, int target) {
    unordered_map<int, int> mp;
    for (int i = 0; i < nums.size(); i++) {
        int diff = target - nums[i];
        if (mp.find(diff) != mp.end()) {
            return {mp[diff], i};
        }
        mp[nums[i]] = i;
    }
    return {};
}

int main() {
    int n;
    if (!(cin >> n)) return 0;
    vector<int> nums(n);
    for (int i = 0; i < n; i++) cin >> nums[i];
    int target;
    cin >> target;
    vector<int> res = twoSum(nums, target);
    cout << "[" << res[0] << ", " << res[1] << "]" << endl;
    return 0;
}
""",
                    maxScore = 100,
                    tags = "Arrays, Hash Table, CodeTantra Core"
                )
            )

            // Test cases for Question 1
            testCaseDao.insertTestCases(
                listOf(
                    TestCase(
                        questionId = q1Id,
                        input = "4\n2 7 11 15\n9",
                        expectedOutput = "[0, 1]",
                        isHidden = false,
                        weight = 25,
                        label = "Public Test Case 1"
                    ),
                    TestCase(
                        questionId = q1Id,
                        input = "3\n3 2 4\n6",
                        expectedOutput = "[1, 2]",
                        isHidden = false,
                        weight = 25,
                        label = "Public Test Case 2"
                    ),
                    TestCase(
                        questionId = q1Id,
                        input = "2\n3 3\n6",
                        expectedOutput = "[0, 1]",
                        isHidden = true,
                        weight = 25,
                        label = "Hidden Edge Case 1 (Duplicate elements)"
                    ),
                    TestCase(
                        questionId = q1Id,
                        input = "5\n1 5 8 12 19\n20",
                        expectedOutput = "[2, 3]",
                        isHidden = true,
                        weight = 25,
                        label = "Hidden Edge Case 2 (Large elements)"
                    )
                )
            )

            val q2Id = questionDao.insertQuestion(
                CodingQuestion(
                    courseId = c1Id,
                    title = "Valid Palindrome Checker",
                    difficulty = "EASY",
                    timeLimitSeconds = 1.0,
                    memoryLimitMB = 256,
                    description = "A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward.\n\nPrint `true` if it is a palindrome, or `false` otherwise.",
                    constraints = "• 1 <= s.length <= 2 * 10^5\n• s consists only of printable ASCII characters.",
                    sampleInput = "racecar",
                    sampleOutput = "true",
                    starterCodePython = """def is_palindrome(s):
    cleaned = "".join(ch.lower() for ch in s if ch.isalnum())
    return cleaned == cleaned[::-1]

text = input().strip()
print("true" if is_palindrome(text) else "false")
""",
                    starterCodeJava = """import java.util.Scanner;

public class Solution {
    public static boolean isPalindrome(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            }
        }
        String clean = sb.toString();
        String rev = sb.reverse().toString();
        return clean.equals(rev);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        System.out.println(isPalindrome(s) ? "true" : "false");
    }
}
""",
                    starterCodeCpp = """#include <iostream>
#include <string>
#include <algorithm>
using namespace std;

bool isPalindrome(string s) {
    string cleaned = "";
    for (char c : s) {
        if (isalnum(c)) cleaned += tolower(c);
    }
    string rev = cleaned;
    reverse(rev.begin(), rev.end());
    return cleaned == rev;
}

int main() {
    string line;
    getline(cin, line);
    cout << (isPalindrome(line) ? "true" : "false") << endl;
    return 0;
}
""",
                    maxScore = 100,
                    tags = "String, Two Pointers"
                )
            )

            testCaseDao.insertTestCases(
                listOf(
                    TestCase(
                        questionId = q2Id,
                        input = "racecar",
                        expectedOutput = "true",
                        isHidden = false,
                        weight = 25,
                        label = "Public Test Case 1"
                    ),
                    TestCase(
                        questionId = q2Id,
                        input = "hello world",
                        expectedOutput = "false",
                        isHidden = false,
                        weight = 25,
                        label = "Public Test Case 2"
                    ),
                    TestCase(
                        questionId = q2Id,
                        input = "A man a plan a canal Panama",
                        expectedOutput = "true",
                        isHidden = true,
                        weight = 25,
                        label = "Hidden Edge Case 1 (Spaces and casing)"
                    ),
                    TestCase(
                        questionId = q2Id,
                        input = "12321",
                        expectedOutput = "true",
                        isHidden = true,
                        weight = 25,
                        label = "Hidden Edge Case 2 (Numeric palindrome)"
                    )
                )
            )

            val q3Id = questionDao.insertQuestion(
                CodingQuestion(
                    courseId = c1Id,
                    title = "Reverse Words in a String",
                    difficulty = "MEDIUM",
                    timeLimitSeconds = 1.5,
                    memoryLimitMB = 256,
                    description = "Given an input string `s`, reverse the order of the words.\n\nA word is defined as a sequence of non-space characters. The words in `s` will be separated by at least one space.\n\nReturn a string of the words in reverse order concatenated by a single space, with no leading or trailing spaces.",
                    constraints = "• 1 <= s.length <= 10^4\n• s contains English letters (upper-case and lower-case), digits, and spaces ' '.",
                    sampleInput = "the sky is blue",
                    sampleOutput = "blue is sky the",
                    starterCodePython = """def reverse_words(s):
    words = s.strip().split()
    return " ".join(reversed(words))

text = input()
print(reverse_words(text))
""",
                    starterCodeJava = """import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine().trim();
        String[] parts = s.split("\\s+");
        List<String> list = Arrays.asList(parts);
        Collections.reverse(list);
        System.out.println(String.join(" ", list));
    }
}
""",
                    starterCodeCpp = """#include <iostream>
#include <sstream>
#include <vector>
using namespace std;

int main() {
    string line;
    getline(cin, line);
    stringstream ss(line);
    string word;
    vector<string> words;
    while(ss >> word) words.push_back(word);
    for (int i = (int)words.size() - 1; i >= 0; i--) {
        cout << words[i] << (i > 0 ? " " : "");
    }
    cout << endl;
    return 0;
}
""",
                    maxScore = 100,
                    tags = "Strings, Parsing"
                )
            )

            testCaseDao.insertTestCases(
                listOf(
                    TestCase(
                        questionId = q3Id,
                        input = "the sky is blue",
                        expectedOutput = "blue is sky the",
                        isHidden = false,
                        weight = 50,
                        label = "Public Test Case 1"
                    ),
                    TestCase(
                        questionId = q3Id,
                        input = "  hello world  ",
                        expectedOutput = "world hello",
                        isHidden = true,
                        weight = 50,
                        label = "Hidden Edge Case 1 (Multiple Whitespaces)"
                    )
                )
            )

            // Java Course Question: Fibonacci
            val q4Id = questionDao.insertQuestion(
                CodingQuestion(
                    courseId = c2Id,
                    title = "Fibonacci Sequence Generator",
                    difficulty = "EASY",
                    timeLimitSeconds = 1.0,
                    memoryLimitMB = 256,
                    description = "The Fibonacci numbers, commonly denoted `F(n)` form a sequence such that each number is the sum of the two preceding ones, starting from `0` and `1`:\n• F(0) = 0, F(1) = 1\n• F(n) = F(n - 1) + F(n - 2), for n > 1.\n\nGiven `n`, calculate `F(n)`.",
                    constraints = "• 0 <= n <= 30",
                    sampleInput = "5",
                    sampleOutput = "5",
                    starterCodePython = """def fib(n):
    if n <= 0:
        return 0
    if n == 1:
        return 1
    a, b = 0, 1
    for _ in range(2, n + 1):
        a, b = b, a + b
    return b

n = int(input())
print(fib(n))
""",
                    starterCodeJava = """import java.util.Scanner;

public class Solution {
    public static int fib(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        System.out.println(fib(n));
    }
}
""",
                    starterCodeCpp = """#include <iostream>
using namespace std;

int fib(int n) {
    if (n <= 0) return 0;
    if (n == 1) return 1;
    int a = 0, b = 1;
    for (int i = 2; i <= n; i++) {
        int c = a + b;
        a = b;
        b = c;
    }
    return b;
}

int main() {
    int n;
    cin >> n;
    cout << fib(n) << endl;
    return 0;
}
""",
                    maxScore = 100,
                    tags = "Math, Dynamic Programming"
                )
            )

            testCaseDao.insertTestCases(
                listOf(
                    TestCase(
                        questionId = q4Id,
                        input = "5",
                        expectedOutput = "5",
                        isHidden = false,
                        weight = 25,
                        label = "Public Test Case 1"
                    ),
                    TestCase(
                        questionId = q4Id,
                        input = "8",
                        expectedOutput = "21",
                        isHidden = false,
                        weight = 25,
                        label = "Public Test Case 2"
                    ),
                    TestCase(
                        questionId = q4Id,
                        input = "0",
                        expectedOutput = "0",
                        isHidden = true,
                        weight = 25,
                        label = "Hidden Edge Case 1 (Base case 0)"
                    ),
                    TestCase(
                        questionId = q4Id,
                        input = "12",
                        expectedOutput = "144",
                        isHidden = true,
                        weight = 25,
                        label = "Hidden Edge Case 2 (F(12))"
                    )
                )
            )

            // Seed initial submissions so analytics dashboard has immediate high-fidelity data
            val now = System.currentTimeMillis()
            submissionDao.insertSubmission(
                Submission(
                    questionId = q1Id,
                    courseId = c1Id,
                    studentId = "STU_RAHUL",
                    studentName = "Rahul Sharma",
                    language = "PYTHON",
                    code = "# Two sum hash map solution\ndef two_sum(nums, target):\n    seen = {}\n    for i, num in enumerate(nums):\n        diff = target - num\n        if diff in seen:\n            return [seen[diff], i]\n        seen[num] = i\n    return []",
                    status = "ACCEPTED",
                    score = 100,
                    testCasesPassed = 4,
                    totalTestCases = 4,
                    executionTimeMs = 42,
                    memoryUsedKb = 14200,
                    testResultsSummary = "All 4/4 test cases passed successfully (100%). Public: 2/2, Hidden: 2/2.",
                    timestamp = now - 3600000 * 2
                )
            )

            submissionDao.insertSubmission(
                Submission(
                    questionId = q4Id,
                    courseId = c2Id,
                    studentId = "STU_PRIYA",
                    studentName = "Priya Patel",
                    language = "JAVA",
                    code = "// Fibonacci iteration\npublic static int fib(int n) {\n    if (n <= 1) return n;\n    int a = 0, b = 1;\n    for(int i=2; i<=n; i++) {\n        int c = a + b;\n        a = b; b = c;\n    }\n    return b;\n}",
                    status = "ACCEPTED",
                    score = 100,
                    testCasesPassed = 4,
                    totalTestCases = 4,
                    executionTimeMs = 38,
                    memoryUsedKb = 18400,
                    testResultsSummary = "All 4/4 test cases passed successfully (100%). Public: 2/2, Hidden: 2/2.",
                    timestamp = now - 3600000 * 5
                )
            )

            submissionDao.insertSubmission(
                Submission(
                    questionId = q2Id,
                    courseId = c1Id,
                    studentId = "STU_AMIT",
                    studentName = "Amit Verma",
                    language = "PYTHON",
                    code = "def is_palindrome(s):\n    return s == s[::-1]",
                    status = "WRONG_ANSWER",
                    score = 50,
                    testCasesPassed = 2,
                    totalTestCases = 4,
                    executionTimeMs = 40,
                    memoryUsedKb = 13800,
                    testResultsSummary = "2/4 test cases passed (50%). Failed hidden case with spaces/casing.",
                    timestamp = now - 3600000 * 8
                )
            )
        }
    }
}

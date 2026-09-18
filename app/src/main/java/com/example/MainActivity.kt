package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.AppDatabase
import com.example.data.model.UserRole
import com.example.data.repository.AssessmentRepository
import com.example.ui.components.CodeAssessTopBar
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.CourseCreateScreen
import com.example.ui.screens.analytics.AnalyticsDashboardScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.student.CodingEnvironmentScreen
import com.example.ui.screens.student.StudentCoursesScreen
import com.example.ui.theme.CodeAssessTheme
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AssessmentViewModel
import com.example.ui.viewmodel.AssessmentViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val database = AppDatabase.getDatabase(applicationContext, appScope)
        val repository = AssessmentRepository(database)
        val factory = AssessmentViewModelFactory(repository)

        setContent {
            CodeAssessTheme {
                val viewModel: AssessmentViewModel = viewModel(factory = factory)
                AssessmentPortalApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AssessmentPortalApp(viewModel: AssessmentViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val notification by viewModel.notification.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSwitchUserDialog by remember { mutableStateOf(false) }

    LaunchedEffect(notification) {
        notification?.let { notif ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = notif.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.dismissNotification()
            }
        }
    }

    // System back handler
    BackHandler(enabled = currentScreen != AppScreen.AUTH) {
        when (currentScreen) {
            AppScreen.CODING_ENVIRONMENT -> {
                viewModel.navigateTo(
                    if (currentUser.role == UserRole.ADMIN) AppScreen.ADMIN_DASHBOARD else AppScreen.STUDENT_COURSES
                )
            }
            AppScreen.ADMIN_CREATE_COURSE -> {
                viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
            }
            AppScreen.ANALYTICS_DASHBOARD -> {
                viewModel.navigateTo(
                    if (currentUser.role == UserRole.ADMIN) AppScreen.ADMIN_DASHBOARD else AppScreen.STUDENT_COURSES
                )
            }
            AppScreen.ADMIN_DASHBOARD, AppScreen.STUDENT_COURSES -> {
                viewModel.logout()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen != AppScreen.AUTH) {
                CodeAssessTopBar(
                    currentScreen = currentScreen,
                    currentUser = currentUser,
                    onNavigate = { viewModel.navigateTo(it) },
                    onSwitchUser = { showSwitchUserDialog = true },
                    onLogout = { viewModel.logout() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    AppScreen.AUTH -> {
                        LoginScreen(viewModel = viewModel)
                    }
                    AppScreen.ADMIN_DASHBOARD -> {
                        AdminDashboardScreen(viewModel = viewModel)
                    }
                    AppScreen.ADMIN_CREATE_COURSE -> {
                        CourseCreateScreen(viewModel = viewModel)
                    }
                    AppScreen.STUDENT_COURSES -> {
                        StudentCoursesScreen(viewModel = viewModel)
                    }
                    AppScreen.CODING_ENVIRONMENT -> {
                        CodingEnvironmentScreen(viewModel = viewModel)
                    }
                    AppScreen.ANALYTICS_DASHBOARD -> {
                        AnalyticsDashboardScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Quick Switch User Modal
    if (showSwitchUserDialog) {
        AlertDialog(
            onDismissRequest = { showSwitchUserDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Switch User Role",
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Select an account to test both Admin assessment creation and Student code execution & auto-grading:",
                    color = Color(0xFFCBD5E1)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.loginAs(viewModel.adminUser)
                        showSwitchUserDialog = false
                    }
                ) {
                    Text("Admin (Prof. Sharma)", color = WarningAmber)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.loginAs(viewModel.defaultStudent)
                        showSwitchUserDialog = false
                    }
                ) {
                    Text("Student (Rahul S.)", color = CodeCyan)
                }
            }
        )
    }
}

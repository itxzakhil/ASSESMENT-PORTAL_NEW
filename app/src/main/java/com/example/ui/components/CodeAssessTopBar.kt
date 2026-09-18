package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.ui.theme.CodeBlue
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeAssessTopBar(
    currentScreen: AppScreen,
    currentUser: UserProfile,
    onNavigate: (AppScreen) -> Unit,
    onSwitchUser: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkSurface,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CodeBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "CodeAssess Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Code",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Assess",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = CodeCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = if (currentUser.role == UserRole.ADMIN) "Admin Portal" else "Student Portal",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        },
        navigationIcon = {
            if (currentScreen == AppScreen.CODING_ENVIRONMENT || currentScreen == AppScreen.ADMIN_CREATE_COURSE) {
                IconButton(
                    onClick = {
                        if (currentUser.role == UserRole.ADMIN) {
                            onNavigate(AppScreen.ADMIN_DASHBOARD)
                        } else {
                            onNavigate(AppScreen.STUDENT_COURSES)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            // Role Badge with Quick Switch
            Surface(
                onClick = onSwitchUser,
                shape = RoundedCornerShape(16.dp),
                color = if (currentUser.role == UserRole.ADMIN) WarningAmber.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f),
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (currentUser.role == UserRole.ADMIN) WarningAmber else SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentUser.role == UserRole.ADMIN) "ADMIN" else "STUDENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentUser.role == UserRole.ADMIN) WarningAmber else SuccessGreen
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch Role",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Courses Navigation
            IconButton(
                onClick = {
                    if (currentUser.role == UserRole.ADMIN) {
                        onNavigate(AppScreen.ADMIN_DASHBOARD)
                    } else {
                        onNavigate(AppScreen.STUDENT_COURSES)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Courses",
                    tint = if (currentScreen == AppScreen.ADMIN_DASHBOARD || currentScreen == AppScreen.STUDENT_COURSES) CodeCyan else Color(0xFF94A3B8)
                )
            }

            // Analytics Navigation
            IconButton(
                onClick = { onNavigate(AppScreen.ANALYTICS_DASHBOARD) }
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Analytics",
                    tint = if (currentScreen == AppScreen.ANALYTICS_DASHBOARD) CodeCyan else Color(0xFF94A3B8)
                )
            }

            // Logout
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = Color(0xFF94A3B8)
                )
            }
        }
    )
}

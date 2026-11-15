
package com.example.zentask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.ui.components.ZenBackground
import com.example.zentask.model.TaskStatus
import com.example.zentask.viewmodel.ZenTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel
) {

    val tasks by vm.tasks.collectAsState()

    val activeTasks = tasks.filter {
        it.status != TaskStatus.COMPLETED
    }

    val notificationTasks = activeTasks.filter {
        it.dueText.isNotBlank() &&
                it.dueText != "No deadline"
    }

    ZenBackground {

        Scaffold(
            containerColor = Color.Transparent,

            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),

                    title = {
                        Text(
                            text = "Notifications",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZenTaskTheme.TextPrimary
                        )
                    },

                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = ZenTaskTheme.TextPrimary
                            )
                        }
                    }
                )
            }

        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),

                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                if (notificationTasks.isEmpty()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = ZenTaskTheme.Primary,
                                modifier = Modifier.size(64.dp)
                            )

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Text(
                                text = "No notifications yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZenTaskTheme.TextPrimary
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "Your task reminders will appear here when you have active tasks with deadlines.",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 20.sp
                            )
                        }
                    }

                } else {

                    Text(
                        text = "Your Task Reminders",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZenTaskTheme.TextPrimary
                    )

                    notificationTasks.forEach { task ->

                        NotificationItem(
                            title = task.title,
                            message = "Deadline: ${task.dueText}",
                            time = when (task.status) {
                                TaskStatus.IN_PROGRESS ->
                                    "In Progress"

                                TaskStatus.NOT_STARTED ->
                                    "Not Started"

                                TaskStatus.COMPLETED ->
                                    "Completed"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    title: String,
    message: String,
    time: String
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color(0xFFFEF3C7),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = "Task deadline",
                    tint = Color(0xFFF59E0B)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color(0xFF475569)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = time,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

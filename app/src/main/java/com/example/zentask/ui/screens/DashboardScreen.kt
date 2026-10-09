package com.example.zentask.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.model.TaskItem
import com.example.zentask.model.TaskStatus
import com.example.zentask.ui.components.*
import com.example.zentask.viewmodel.ZenTaskViewModel

@Composable
fun DashboardScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel
) {
    val tasks by vm.tasks.collectAsState()
    var showAddTaskModal by remember { mutableStateOf(false) }
    var showJoinCollabModal by remember { mutableStateOf(false) }

    val completedCount = tasks.count { it.status == TaskStatus.COMPLETED }
    val inProgressCount = tasks.count { it.status == TaskStatus.IN_PROGRESS }
    val notStartedCount = tasks.count { it.status == TaskStatus.NOT_STARTED }

    ZenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                FloatingCapsuleNavBar(
                    currentRoute = "dashboard",
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
        ) { padding ->
            ProductiveDashboardContent(
                modifier = Modifier.padding(padding),
                completedCount = completedCount,
                inProgressCount = inProgressCount,
                notStartedCount = notStartedCount,
                onNewTaskClick = { showAddTaskModal = true },
                onJoinCollabClick = { showJoinCollabModal = true },
                onViewAllClick = { navController.navigate("tasks") },
                onDetailsClick = { navController.navigate("tasks") },
                onTaskToggle = { taskId -> vm.toggleTaskCompletion(taskId) }
            )
        }

        if (showAddTaskModal) {
            AddTaskBottomSheet(
                onDismiss = { showAddTaskModal = false },
                onSave = { title, cat, pri, due ->
                    vm.addTask(title, cat, pri, due)
                    showAddTaskModal = false
                }
            )
        }

        if (showJoinCollabModal) {
            JoinCollabDialog(
                onDismiss = { showJoinCollabModal = false },
                onJoin = { code ->
                    vm.joinCollabTask(code)
                    showJoinCollabModal = false
                }
            )
        }
    }
}

@Composable
fun ProductiveDashboardContent(
    modifier: Modifier = Modifier,
    completedCount: Int = 18,
    inProgressCount: Int = 5,
    notStartedCount: Int = 2,
    totalTasksToday: Int = 8,
    completedTasksToday: Int = 3,
    streakDays: Int = 12,
    onNewTaskClick: () -> Unit = {},
    onJoinCollabClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onDetailsClick: () -> Unit = {},
    onTaskToggle: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            ProductiveDashboardHeader(
                completedTasks = completedTasksToday,
                totalTasks = totalTasksToday,
                streakDays = streakDays
            )
        }

        item {
            TaskProgressAccumulationCard(
                completedCount = completedCount,
                inProgressCount = inProgressCount,
                notStartedCount = notStartedCount,
                onDetailsClick = onDetailsClick
            )
        }

        item {
            QuickActionsRow(
                onNewTaskClick = onNewTaskClick,
                onJoinCollabClick = onJoinCollabClick
            )
        }

        item {
            TodoListHeader(
                remainingCount = 2,
                onViewAllClick = onViewAllClick
            )
        }

        item {
            TaskCardUiDesign(
                onCheckToggle = { onTaskToggle("1") }
            )
        }

        item {
            TaskCardStudy(
                onCheckToggle = { onTaskToggle("2") }
            )
        }

        item {
            CompletedTodayCard()
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ProductiveDashboardHeader(
    modifier: Modifier = Modifier,
    completedTasks: Int = 3,
    totalTasks: Int = 8,
    streakDays: Int = 12
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Today's Progress",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$completedTasks of $totalTasks tasks completed today",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Streak",
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$streakDays-day streak",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
        }
    }
}

@Composable
fun TaskProgressAccumulationCard(
    modifier: Modifier = Modifier,
    completedCount: Int = 18,
    inProgressCount: Int = 5,
    notStartedCount: Int = 2,
    totalPercentage: Int = 78,
    onDetailsClick: () -> Unit = {},
    onGridClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFAF9FF),
        border = BorderStroke(1.dp, Color(0xFFEDE9FE)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WEEKLY PERFORMANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Progress Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFF1F5F9), CircleShape)
                        .clickable { onGridClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.GridView,
                        contentDescription = "Grid",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(140.dp)) {
                        val strokeWidth = 16.dp.toPx()
                        drawCircle(
                            color = Color(0xFFF1F5F9),
                            style = Stroke(strokeWidth)
                        )
                        drawArc(
                            color = Color(0xFF38BDF8),
                            startAngle = -120f,
                            sweepAngle = 40f,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = Color(0xFF10B981),
                            startAngle = -70f,
                            sweepAngle = (totalPercentage / 100f) * 360f,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalPercentage%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Completed",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskStatusBreakdownPill(
                    label = "Completed",
                    countText = "$completedCount tasks",
                    dotColor = Color(0xFF10B981),
                    bgColor = Color(0xFFE6F7ED),
                    textColor = Color(0xFF10B981)
                )
                TaskStatusBreakdownPill(
                    label = "In Progress",
                    countText = "$inProgressCount tasks",
                    dotColor = Color(0xFF38BDF8),
                    bgColor = Color(0xFFE0F2FE),
                    textColor = Color(0xFF0284C7)
                )
                TaskStatusBreakdownPill(
                    label = "Not Started",
                    countText = "$notStartedCount tasks",
                    dotColor = Color(0xFF94A3B8),
                    bgColor = Color(0xFFF1F5F9),
                    textColor = Color(0xFF64748B)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFF6E56CF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Goal",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4 more tasks to reach your weekly goal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6E56CF)
                    )
                }

                Text(
                    text = "Details →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6E56CF),
                    modifier = Modifier.clickable { onDetailsClick() }
                )
            }
        }
    }
}

@Composable
fun TaskStatusBreakdownPill(
    label: String,
    countText: String,
    dotColor: Color,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(dotColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Text(
                text = countText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun QuickActionsRow(
    modifier: Modifier = Modifier,
    onNewTaskClick: () -> Unit = {},
    onJoinCollabClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF6E56CF), Color(0xFF8B5CF6))
                    )
                )
                .clickable { onNewTaskClick() }
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = "Folder",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Quick",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New Task",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Add to personal board",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFFAFAFE))
                .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
                .clickable { onJoinCollabClick() }
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEDE9FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAdd,
                            contentDescription = "Person",
                            tint = Color(0xFF6E56CF),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF38BDF8), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Link,
                        contentDescription = "Link",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Join Collab",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Enter 6-digit code",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun TodoListHeader(
    modifier: Modifier = Modifier,
    remainingCount: Int = 2,
    onViewAllClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Today's Tasks",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = Color(0xFFEDE9FE),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$remainingCount remaining",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    color = Color(0xFF6E56CF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onViewAllClick() }
        ) {
            Text(
                text = "View All",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6E56CF)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "View All",
                tint = Color(0xFF6E56CF),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TaskCardUiDesign(
    modifier: Modifier = Modifier,
    onCheckToggle: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFF43F5E))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFEDE9FE),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "UI Design",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color(0xFF6E56CF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "High",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = Color(0xFFEF4444),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(2.dp, Color(0xFFCBD5E1), CircleShape)
                                .clickable { onCheckToggle() }
                        )
                    }
                }

                Text(
                    text = "Finalize Design System Components",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Calendar",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Today, 4:00 PM • Figma Spec",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "Time Left",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Due in 2h",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEF4444)
                        )
                    }

                    Row {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFEDE9FE), CircleShape)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AJ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6E56CF)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .offset(x = (-6).dp)
                                .size(24.dp)
                                .background(Color(0xFFE0F2FE), CircleShape)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ML",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCardStudy(
    modifier: Modifier = Modifier,
    onCheckToggle: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFF59E0B))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Study",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color(0xFFD97706),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Medium",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = Color(0xFFD97706),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(2.dp, Color(0xFFCBD5E1), CircleShape)
                                .clickable { onCheckToggle() }
                        )
                    }
                }

                Text(
                    text = "Review React 19 Server Actions Notes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Time",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Today, 6:30 PM • 45 min focus",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Book,
                            contentDescription = "Book",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Module 4: Async State Transitions",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Solo",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedTodayCard(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFAFAFE),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF10B981), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Completed Today",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "2 tasks completed",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowUp,
                    contentDescription = "Collapse",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color(0xFF10B981), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Morning 20-min Deep Breathing Meditation",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Text(
                            text = "8:15 AM",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color(0xFF10B981), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Submit Weekly Sprint Review to Team Slack",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Text(
                            text = "10:30 AM",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegendPill(label: String, count: Int, dotColor: Color, bgColor: Color) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).background(dotColor, CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 11.sp, color = ZenTaskTheme.TextPrimary)
            Spacer(modifier = Modifier.width(4.dp))
            Text("($count)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
        }
    }
}

@Composable
fun TaskCardRow(task: TaskItem, onCheckToggle: () -> Unit) {
    val isDone = task.status == TaskStatus.COMPLETED
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (task.isVital && !isDone) ZenTaskTheme.PriorityHigh.copy(alpha = 0.5f) else ZenTaskTheme.GlassBorder,
                RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        color = if (isDone) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCheckToggle, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
                    contentDescription = "Toggle",
                    tint = if (isDone) Color(0xFF10B981) else ZenTaskTheme.TextSubdued
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(task.categoryColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(task.categoryName, fontSize = 11.sp, color = ZenTaskTheme.TextSecondary)
                    if (task.isVital && !isDone) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = ZenTaskTheme.PriorityHighGlow, shape = RoundedCornerShape(6.dp)) {
                            Text("VITAL", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = ZenTaskTheme.PriorityHigh, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    task.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (isDone) ZenTaskTheme.TextSubdued else ZenTaskTheme.TextPrimary,
                    textDecoration = if (isDone) TextDecoration.LineThrough else null
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(task.dueText, fontSize = 11.sp, color = ZenTaskTheme.TextSecondary)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1000)
@Composable
fun ProductiveDashboardPreview() {
    ZenBackground {
        ProductiveDashboardContent()
    }
}

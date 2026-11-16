package com.example.zentask.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
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

// =====================================================
// MAIN DASHBOARD SCREEN
// =====================================================

@Composable
fun DashboardScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel
) {
    val tasks by vm.tasks.collectAsState()
    val categories by vm.categories.collectAsState()
    val summary by vm.productivitySummary.collectAsState()
    val analytics by vm.localAnalytics.collectAsState()

    var showAddTaskModal by remember {
        mutableStateOf(false)
    }

    var showJoinCollabModal by remember {
        mutableStateOf(false)
    }

    ZenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                FloatingCapsuleNavBar(
                    currentRoute = "dashboard",
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }
        ) { padding ->

            ProductiveDashboardContent(
                modifier = Modifier.padding(padding),
                completedCount = summary.completed,
                inProgressCount = summary.inProgress,
                notStartedCount = summary.notStarted,
                completionRate = summary.completionRate,
                analytics = analytics,
                tasks = tasks,
                onNewTaskClick = {
                    showAddTaskModal = true
                },
                onJoinCollabClick = {
                    showJoinCollabModal = true
                },
                onViewAllClick = {
                    navController.navigate("tasks")
                },
                onDetailsClick = {
                    navController.navigate("analytics_detail")
                },
                onTaskToggle = { taskId ->
                    vm.toggleTaskCompletion(taskId)
                }
            )
        }

        

        if (showAddTaskModal) {
            AddTaskBottomSheet(
                onDismiss = {
                    showAddTaskModal = false
                },

                // Backward-compatible callback.
                onSave = { title, cat, pri, due, desc, status ->
                    vm.addTask(title, cat, pri, due, desc, status)
                    showAddTaskModal = false
                },

                // Categories now come from the active
                // user's Room database.
                categories = categories,

                // Full form data, including
                // Description and Status.
                onSaveDetailed = {
                        title,
                        category,
                        priority,
                        due,
                        description,
                        status ->

                    vm.addTask(
                        title = title,
                        categoryName = category,
                        priority = priority,
                        due = due,
                        description = description,
                        status = status
                    )

                    showAddTaskModal = false
                }
            )
        }

        // =============================================
        // JOIN COLLABORATION
        // =============================================

        if (showJoinCollabModal) {
            JoinCollabDialog(
                onDismiss = {
                    showJoinCollabModal = false
                },
                onJoin = { code ->
                    vm.joinWithCode(code) { result ->
                    }
                    showJoinCollabModal = false
                }
            )
        }
    }
}

// =====================================================
// DASHBOARD CONTENT
// =====================================================

internal fun shouldUseLandscapeDashboard(widthDp: Int): Boolean {
    return widthDp >= 700
}

@Composable
fun ProductiveDashboardContent(
    modifier: Modifier = Modifier,
    completedCount: Int = 0,
    inProgressCount: Int = 0,
    notStartedCount: Int = 0,
    completionRate: Int = 0,
    analytics: com.example.zentask.analytics.LocalAnalyticsSnapshot = com.example.zentask.analytics.LocalAnalyticsSnapshot(),
    tasks: List<TaskItem> = emptyList(),
    onNewTaskClick: () -> Unit = {},
    onJoinCollabClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onDetailsClick: () -> Unit = {},
    onTaskToggle: (String) -> Unit = {}
) {
    val remainingTasks = tasks.filter {
        it.status != TaskStatus.COMPLETED
    }

    val completedTasks = tasks.filter {
        it.status == TaskStatus.COMPLETED
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val isLandscape = shouldUseLandscapeDashboard(maxWidth.value.toInt())

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            if (isLandscape) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        ProductiveDashboardHeader(
                            modifier = Modifier.weight(1f),
                            completedTasks = completedCount,
                            totalTasks = tasks.size
                        )

                        TaskProgressAccumulationCard(
                            modifier = Modifier.weight(1f),
                            completedCount = completedCount,
                            inProgressCount = inProgressCount,
                            notStartedCount = notStartedCount,
                            totalPercentage = completionRate,
                            onDetailsClick = onDetailsClick
                        )
                    }
                }
            } else {
                item {
                    ProductiveDashboardHeader(
                        completedTasks = completedCount,
                        totalTasks = tasks.size
                    )
                }

                item {
                    TaskProgressAccumulationCard(
                        completedCount = completedCount,
                        inProgressCount = inProgressCount,
                        notStartedCount = notStartedCount,
                        totalPercentage = completionRate,
                        onDetailsClick = onDetailsClick
                    )
                }
            }

            item {
                AnalyticsInsightCard(
                    snapshot = analytics,
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
                    remainingCount = remainingTasks.size,
                    onViewAllClick = onViewAllClick
                )
            }

            items(
                items = remainingTasks.take(3),
                key = { it.id }
            ) { task ->
                TaskCardRow(
                    task = task,
                    onCheckToggle = {
                        onTaskToggle(task.id)
                    }
                )
            }

            if (remainingTasks.isEmpty()) {
                item {
                    Text(
                        text = if (tasks.isEmpty()) {
                            "No tasks yet. Create your first task!"
                        } else {
                            "All tasks are completed!"
                        },
                        color = Color(0xFF64748B)
                    )
                }
            }

            if (completedTasks.isNotEmpty()) {
                item {
                    CompletedTodayCard(
                        completedTasks = completedTasks
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(80.dp)
                )
            }
        }
    }
}

// =====================================================
// DASHBOARD HEADER
// =====================================================

@Composable
fun ProductiveDashboardHeader(
    modifier: Modifier = Modifier,
    completedTasks: Int = 0,
    totalTasks: Int = 0
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Overall Progress",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "$completedTasks of $totalTasks tasks completed",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }
    }
}

// =====================================================
// PROGRESS OVERVIEW
// =====================================================

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
        border = BorderStroke(
            1.dp,
            Color(0xFFEDE9FE)
        ),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TASK PERFORMANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

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
                        .background(
                            Color(0xFFF1F5F9),
                            CircleShape
                        )
                        .clickable {
                            onGridClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.GridView,
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
                    Canvas(
                        modifier = Modifier.size(140.dp)
                    ) {
                        val strokeWidth = 16.dp.toPx()

                        drawCircle(
                            color = Color(0xFFF1F5F9),
                            style = Stroke(strokeWidth)
                        )

                        if (
                            completedCount +
                            inProgressCount +
                            notStartedCount > 0
                        ) {
                            val total =
                                completedCount +
                                        inProgressCount +
                                        notStartedCount

                            val inProgressSweep =
                                (inProgressCount.toFloat() /
                                        total) * 360f

                            val completedSweep =
                                (completedCount.toFloat() /
                                        total) * 360f

                            if (inProgressSweep > 0f) {
                                drawArc(
                                    color = Color(0xFF38BDF8),
                                    startAngle = -90f,
                                    sweepAngle = inProgressSweep,
                                    useCenter = false,
                                    style = Stroke(
                                        strokeWidth,
                                        cap = StrokeCap.Round
                                    )
                                )
                            }

                            if (completedSweep > 0f) {
                                drawArc(
                                    color = Color(0xFF10B981),
                                    startAngle =
                                        -90f + inProgressSweep,
                                    sweepAngle = completedSweep,
                                    useCenter = false,
                                    style = Stroke(
                                        strokeWidth,
                                        cap = StrokeCap.Round
                                    )
                                )
                            }
                        }
                    }

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
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

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
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
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                Color(0xFF6E56CF),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Goal",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "${inProgressCount + notStartedCount} tasks remaining to complete",
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
                    modifier = Modifier.clickable {
                        onDetailsClick()
                    }
                )
            }
        }
    }
}

// =====================================================
// STATUS BREAKDOWN PILL
// =====================================================

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
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            dotColor,
                            CircleShape
                        )
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

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

// =====================================================
// LOCAL ANALYTICS
// =====================================================

@Composable
fun AnalyticsInsightCard(
    modifier: Modifier = Modifier,
    snapshot: com.example.zentask.analytics.LocalAnalyticsSnapshot = com.example.zentask.analytics.LocalAnalyticsSnapshot(),
    onDetailsClick: () -> Unit = {}
) {
    var selectedRange by remember { mutableStateOf("Weekly") }
    val currentTrend = if (selectedRange == "Daily") snapshot.dailyTrend else snapshot.weeklyTrend
    val maxValue = currentTrend.maxOrNull()?.coerceAtLeast(1) ?: 1
    val rangeLabels = if (selectedRange == "Daily") listOf("M", "T", "W", "T", "F", "S", "S") else listOf("W1", "W2", "W3", "W4", "W5", "W6", "W7")

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color(0xFFDDD6FE),
                    spotColor = Color(0xFFDDD6FE)
                )
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFF8F5FF),
                            Color(0xFFEDE9FE),
                            Color(0xFFFDF2F8)
                        )
                    )
                )
                .border(
                    1.dp,
                    Color(0xFFE9D5FF),
                    RoundedCornerShape(28.dp)
                )
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
                            text = "LOCAL ANALYTICS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7C3AED),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Performance Snapshot",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "Focus ${snapshot.focusScore}%",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            color = Color(0xFF6D28D9),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF6D28D9),
                        shape = RoundedCornerShape(18.dp),
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Completion",
                                fontSize = 11.sp,
                                color = Color(0xFFEDE9FE)
                            )
                            Text(
                                text = "${snapshot.completionRate}%",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Top category",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = snapshot.mostActiveCategory,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C3AED)
                            )
                        }
                    }
                }

                Surface(
                    color = Color.White.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Trend",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )

                            Row(
                                modifier = Modifier
                                    .background(
                                        Color(0xFFE9D5FF),
                                        RoundedCornerShape(999.dp)
                                    )
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Daily", "Weekly").forEach { range ->
                                    val isSelected = range == selectedRange
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = if (isSelected) Color(0xFF6D28D9) else Color.Transparent,
                                        modifier = Modifier.clickable { selectedRange = range }
                                    ) {
                                        Text(
                                            text = range,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color(0xFF6D28D9)
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            currentTrend.forEachIndexed { index, value ->
                                val height = ((value.toFloat() / maxValue.toFloat()) * 58f).coerceAtLeast(8f)
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(height.dp)
                                            .background(
                                                if (value > 0) Brush.verticalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))) else Brush.verticalGradient(listOf(Color(0xFFE5E7EB), Color(0xFFE5E7EB))),
                                                RoundedCornerShape(8.dp)
                                            )
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = rangeLabels[index],
                                        fontSize = 9.sp,
                                        color = Color(0xFF6B7280),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFF5F3FF),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Streak",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "${snapshot.productivityStreak}d",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFF7ED),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Backlog",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "${snapshot.backlogPressure}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }

                Surface(
                    color = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = snapshot.weeklyInsight,
                            color = Color(0xFF1F2937),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 18.sp
                        )

                        Text(
                            text = snapshot.dailyInsight,
                            color = Color(0xFF4B5563),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Surface(
                    color = Color(0xFF6D28D9),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = snapshot.recommendedAction,
                        modifier = Modifier.padding(14.dp),
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Text(
                    text = "Details →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6D28D9),
                    modifier = Modifier.clickable { onDetailsClick() }
                )
            }
        }
    }
}

// =====================================================
// ANALYTICS DETAIL SCREEN
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDetailScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel
) {
    val analytics by vm.localAnalytics.collectAsState()
    val tasks by vm.tasks.collectAsState()
    val currentTrend = analytics.weeklyTrend
    val maxValue = currentTrend.maxOrNull()?.coerceAtLeast(1) ?: 1
    val trendLabels = listOf("W1", "W2", "W3", "W4", "W5", "W6", "W7")

    ZenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Column {
                            Text(
                                text = "LOCAL ANALYTICS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C3AED),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Detail overview",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                .border(1.dp, Color(0xFFE9D5FF), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = "Back",
                                tint = Color(0xFF6D28D9),
                                modifier = Modifier.rotate(180f)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DetailMetric(title = "Focus", value = "${analytics.focusScore}%")
                            DetailMetric(title = "Streak", value = "${analytics.productivityStreak}d")
                            DetailMetric(title = "Overdue", value = "${analytics.overdueCount}")
                            DetailMetric(title = "Risk", value = "${analytics.atRiskCount}")
                        }
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFF8F5FF),
                        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                        shadowElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Weekly trend",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                currentTrend.forEachIndexed { index, value ->
                                    val height = ((value.toFloat() / maxValue.toFloat()) * 72f).coerceAtLeast(10f)
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(height.dp)
                                                .background(
                                                    if (value > 0) Brush.verticalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))) else Brush.verticalGradient(listOf(Color(0xFFE5E7EB), Color(0xFFE5E7EB))),
                                                    RoundedCornerShape(8.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = trendLabels[index],
                                            fontSize = 9.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                        shadowElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Key signals",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )

                            DetailSignalRow(label = "Most active category", value = analytics.mostActiveCategory)
                            DetailSignalRow(label = "Completion rate", value = "${analytics.completionRate}%")
                            DetailSignalRow(label = "Backlog pressure", value = "${analytics.backlogPressure} tasks")
                            DetailSignalRow(label = "Daily insight", value = analytics.dailyInsight)
                            DetailSignalRow(label = "Weekly insight", value = analytics.weeklyInsight)
                        }
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF6D28D9),
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Smart recommendations",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            analytics.recommendations.forEach { message ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(8.dp)
                                            .background(Color.White, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = message,
                                        color = Color.White.copy(alpha = 0.95f),
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Tracked tasks: ${tasks.size}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailMetric(
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D28D9)
        )
    }
}

@Composable
private fun DetailSignalRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier.weight(2f),
            textDecoration = TextDecoration.None
        )
    }
}

// =====================================================
// QUICK ACTIONS
// =====================================================

@Composable
fun QuickActionsRow(
    modifier: Modifier = Modifier,
    onNewTaskClick: () -> Unit = {},
    onJoinCollabClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        // =============================================
        // NEW TASK
        // =============================================

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF6E56CF),
                            Color(0xFF8B5CF6)
                        )
                    )
                )
                .clickable {
                    onNewTaskClick()
                }
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color.White.copy(
                                    alpha = 0.2f
                                ),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.Folder,
                            contentDescription = "Folder",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Surface(
                        color = Color.White.copy(
                            alpha = 0.25f
                        ),
                        shape =
                            RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Quick",
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 3.dp
                            ),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text = "New Task",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Add to personal board",
                    fontSize = 11.sp,
                    color = Color.White.copy(
                        alpha = 0.8f
                    )
                )
            }
        }

        // =============================================
        // JOIN COLLAB
        // =============================================

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(
                    Color(0xFFFAFAFE)
                )
                .border(
                    1.dp,
                    Color(0xFFF1F5F9),
                    RoundedCornerShape(20.dp)
                )
                .clickable {
                    onJoinCollabClick()
                }
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color(0xFFEDE9FE),
                                CircleShape
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.PersonAdd,
                            contentDescription = "Person",
                            tint = Color(0xFF6E56CF),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                Color(0xFF38BDF8),
                                CircleShape
                            )
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.Link,
                        contentDescription = "Link",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "Join Collab",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Enter 6-digit code",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

// =====================================================
// TASK LIST HEADER
// =====================================================

@Composable
fun TodoListHeader(
    modifier: Modifier = Modifier,
    remainingCount: Int = 2,
    onViewAllClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tasks To Do",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Surface(
                color = Color(0xFFEDE9FE),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$remainingCount remaining",
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 3.dp
                    ),
                    color = Color(0xFF6E56CF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            modifier = Modifier.clickable {
                onViewAllClick()
            }
        ) {
            Text(
                text = "View All",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6E56CF)
            )

            Spacer(
                modifier = Modifier.width(2.dp)
            )

            Icon(
                imageVector =
                    Icons.Outlined.ChevronRight,
                contentDescription = "View All",
                tint = Color(0xFF6E56CF),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// =====================================================
// EXISTING UI DESIGN CARD
// =====================================================

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
        border = BorderStroke(
            1.dp,
            Color(0xFFF1F5F9)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
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
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFEDE9FE),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "UI Design",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            ),
                            color = Color(0xFF6E56CF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "High",
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                ),
                                color = Color(0xFFEF4444),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(
                                    2.dp,
                                    Color(0xFFCBD5E1),
                                    CircleShape
                                )
                                .clickable {
                                    onCheckToggle()
                                }
                        )
                    }
                }

                Text(
                    text = "Finalize Design System Components",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.CalendarToday,
                        contentDescription = "Calendar",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "Today, 4:00 PM • Figma Spec",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.Schedule,
                            contentDescription = "Time Left",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

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
                                .background(
                                    Color(0xFFEDE9FE),
                                    CircleShape
                                )
                                .border(
                                    1.5.dp,
                                    Color.White,
                                    CircleShape
                                ),
                            contentAlignment =
                                Alignment.Center
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
                                .background(
                                    Color(0xFFE0F2FE),
                                    CircleShape
                                )
                                .border(
                                    1.5.dp,
                                    Color.White,
                                    CircleShape
                                ),
                            contentAlignment =
                                Alignment.Center
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

// =====================================================
// EXISTING STUDY CARD
// =====================================================

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
        border = BorderStroke(
            1.dp,
            Color(0xFFF1F5F9)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
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
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Study",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            ),
                            color = Color(0xFFD97706),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Medium",
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                ),
                                color = Color(0xFFD97706),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(
                                    2.dp,
                                    Color(0xFFCBD5E1),
                                    CircleShape
                                )
                                .clickable {
                                    onCheckToggle()
                                }
                        )
                    }
                }

                Text(
                    text = "Review React 19 Server Actions Notes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.Schedule,
                        contentDescription = "Time",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "Today, 6:30 PM • 45 min focus",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.Book,
                            contentDescription = "Book",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

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
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 3.dp
                            ),
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

// =====================================================
// COMPLETED TASK CARD
// =====================================================

@Composable
fun CompletedTodayCard(
    modifier: Modifier = Modifier,
    completedTasks: List<TaskItem> = emptyList()
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFAFAFE),
        border = BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981)
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column {
                    Text(
                        text = "Completed Tasks",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${completedTasks.size} tasks completed",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            completedTasks.forEach { task ->
                Text(
                    text = "✓ ${task.title}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    textDecoration =
                        TextDecoration.LineThrough
                )
            }
        }
    }
}

// =====================================================
// LEGEND PILL
// =====================================================

@Composable
fun LegendPill(
    label: String,
    count: Int,
    dotColor: Color,
    bgColor: Color
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        dotColor,
                        CircleShape
                    )
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = label,
                fontSize = 11.sp,
                color = ZenTaskTheme.TextPrimary
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = "($count)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ZenTaskTheme.TextPrimary
            )
        }
    }
}

// =====================================================
// TASK CARD ROW - LIVE DATA
// =====================================================

@Composable
fun TaskCardRow(
    task: TaskItem,
    onCheckToggle: () -> Unit
) {
    val isDone =
        task.status == TaskStatus.COMPLETED

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (
                    task.isVital && !isDone
                ) {
                    ZenTaskTheme.PriorityHigh.copy(
                        alpha = 0.5f
                    )
                } else {
                    ZenTaskTheme.GlassBorder
                },
                RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        color = if (isDone) {
            Color.White.copy(alpha = 0.6f)
        } else {
            Color.White.copy(alpha = 0.9f)
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCheckToggle,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription =
                        "Toggle task completion",
                    tint = if (isDone) {
                        Color(0xFF10B981)
                    } else {
                        ZenTaskTheme.TextSubdued
                    }
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                task.categoryColor,
                                CircleShape
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = task.categoryName,
                        fontSize = 11.sp,
                        color = ZenTaskTheme.TextSecondary
                    )

                    if (
                        task.isVital && !isDone
                    ) {
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Surface(
                            color =
                                ZenTaskTheme.PriorityHighGlow,
                            shape =
                                RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "VITAL",
                                modifier =
                                    Modifier.padding(
                                        horizontal = 6.dp,
                                        vertical = 2.dp
                                    ),
                                color =
                                    ZenTaskTheme.PriorityHigh,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = task.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (isDone) {
                        ZenTaskTheme.TextSubdued
                    } else {
                        ZenTaskTheme.TextPrimary
                    },
                    textDecoration = if (isDone) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    }
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = task.dueText,
                    fontSize = 11.sp,
                    color = ZenTaskTheme.TextSecondary
                )
            }
        }
    }
}

// =====================================================
// DASHBOARD PREVIEW
// =====================================================

@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 1000
)
@Composable
fun ProductiveDashboardPreview() {
    ZenBackground {
        ProductiveDashboardContent()
    }
}

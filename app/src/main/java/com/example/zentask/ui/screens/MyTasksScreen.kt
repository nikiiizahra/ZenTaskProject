package com.example.zentask.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ui.components.*
import com.example.zentask.viewmodel.ZenTaskViewModel
import com.example.zentask.viewmodel.TaskUrgency
import com.example.zentask.model.TaskItem
import com.example.zentask.model.TaskStatus
import androidx.compose.material.icons.outlined.DocumentScanner


@Composable
fun MyTasksScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel
) {
    val tasks by vm.tasks.collectAsState()
    val urgencyResults by vm.urgencyResults.collectAsState()

    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var showAddTaskModal by remember { mutableStateOf(false) }
    var showJoinCollabModal by remember { mutableStateOf(false) }

    ZenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                FloatingCapsuleNavBar(
                    currentRoute = "tasks",
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
        ) { padding ->
            MyTasksContent(
                modifier = Modifier.padding(padding),
                tasks = tasks,
                urgencyResults = urgencyResults,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                onAddNewTaskClick = { showAddTaskModal = true },
                onScanClick = { navController.navigate("scan") },
                onJoinCollabClick = { code -> vm.joinCollabTask(code) },
                onTaskToggle = { taskId -> vm.toggleTaskCompletion(taskId) },
                onTaskClick = { navController.navigate("task_detail") },
                onNotificationClick = { navController.navigate("notifications") }
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
fun MyTasksContent(
    modifier: Modifier = Modifier,
    tasks: List<TaskItem> = emptyList(),
    urgencyResults: Map<String, TaskUrgency> = emptyMap(),
    selectedFilter: String = "All",
    onFilterSelected: (String) -> Unit = {},
    onAddNewTaskClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onJoinCollabClick: (String) -> Unit = {},
    onTaskToggle: (String) -> Unit = {},
    onTaskClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val filteredTasks = when (selectedFilter) {
        "In Progress" -> tasks.filter { it.status == TaskStatus.IN_PROGRESS }
        "Not Started" -> tasks.filter { it.status == TaskStatus.NOT_STARTED }
        "Completed" -> tasks.filter { it.status == TaskStatus.COMPLETED }
        else -> tasks
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item { MyTasksHeaderRow(onNotificationClick = onNotificationClick) }
        item {
            MyTasksTitleSection(
                activeCount = tasks.count { it.status != TaskStatus.COMPLETED }
            )
        }
        item {
            MyTasksActionButtons(
                onAddNewTaskClick = onAddNewTaskClick,
                onScanClick = onScanClick
            )
        }
        item { JoinTaskViaCodeCard(onJoinCollabClick = onJoinCollabClick) }
        item {
            DynamicTasksFilterRow(
                tasks = tasks,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected
            )
        }

        if (filteredTasks.isEmpty()) {
            item {
                Text(
                    text = "Belum ada tugas pada kategori ini.",
                    modifier = Modifier.padding(vertical = 24.dp),
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            }
        } else {
            items(filteredTasks, key = { it.id }) { task ->
                DynamicTaskCard(
                    task = task,
                    urgency = urgencyResults[task.id],
                    onCheckToggle = { onTaskToggle(task.id) },
                    onTaskClick = onTaskClick
                )
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun DynamicTasksFilterRow(
    tasks: List<TaskItem>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf(
        "All" to tasks.size,
        "In Progress" to tasks.count { it.status == TaskStatus.IN_PROGRESS },
        "Not Started" to tasks.count { it.status == TaskStatus.NOT_STARTED },
        "Completed" to tasks.count { it.status == TaskStatus.COMPLETED }
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(filters) { (filter, count) ->
            val chosen = selectedFilter == filter
            Surface(
                modifier = Modifier.clickable { onFilterSelected(filter) },
                shape = RoundedCornerShape(999.dp),
                color = if (chosen) Color(0xFF6E56CF) else Color.White,
                border = BorderStroke(
                    1.dp,
                    if (chosen) Color(0xFF6E56CF) else Color(0xFFF1F5F9)
                )
            ) {
                Text(
                    text = "$filter ($count)",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = if (chosen) Color.White else Color(0xFF64748B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DynamicTaskCard(
    task: TaskItem,
    urgency: TaskUrgency?,
    onCheckToggle: () -> Unit,
    onTaskClick: () -> Unit
) {
    val finished = task.status == TaskStatus.COMPLETED
    val statusText = when (task.status) {
        TaskStatus.COMPLETED -> "Completed"
        TaskStatus.IN_PROGRESS -> "In Progress"
        TaskStatus.NOT_STARTED -> "Not Started"
    }
    val level = urgency?.level ?: "Low"
    val levelColor = when (level) {
        "Critical" -> Color(0xFFDC2626)
        "High" -> Color(0xFFD97706)
        "Medium" -> Color(0xFF2563EB)
        else -> Color(0xFF059669)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onTaskClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.categoryName,
                    color = task.categoryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$level • ${urgency?.score ?: 0}",
                    color = levelColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = task.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (finished) Color(0xFF94A3B8) else Color(0xFF0F172A),
                textDecoration = if (finished) TextDecoration.LineThrough else null
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = task.dueText, color = Color(0xFF64748B), fontSize = 12.sp)
                    Text(text = statusText, color = Color(0xFF64748B), fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (finished) "Done" else "Mark done",
                        fontSize = 12.sp,
                        color = Color(0xFF6E56CF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (finished) Color(0xFF10B981) else Color.White)
                            .border(2.dp, if (finished) Color(0xFF10B981) else Color(0xFFCBD5E1), CircleShape)
                            .clickable(onClick = onCheckToggle),
                        contentAlignment = Alignment.Center
                    ) {
                        if (finished) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Tugas selesai",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyTasksHeaderRow(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {}
) {

    Row(
        modifier = modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.SpaceBetween,

        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Color(0xFFEDE9FE),
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        Color.White,
                        CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "AJ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6E56CF)
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = "TaskFlow",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        Box(
            modifier = Modifier
                .clickable {
                    onNotificationClick()
                }
        ) {

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Color.White,
                        CircleShape
                    )
                    .border(
                        1.dp,
                        Color(0xFFF1F5F9),
                        CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF6E56CF),
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(
                        Color(0xFFF43F5E),
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        Color.White,
                        CircleShape
                    )
                    .align(Alignment.TopEnd)
            )
        }
    }
}


@Composable
fun MyTasksTitleSection(
    modifier: Modifier = Modifier,
    activeCount: Int = 3
) {

    Row(
        modifier = modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.SpaceBetween,

        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {

            Text(
                text = "My Tasks",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "Organize your workflow & goals",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }

        Surface(
            color = Color(0xFFEDE9FE),
            shape = RoundedCornerShape(16.dp)
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                ),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            Color(0xFF10B981),
                            CircleShape
                        )
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "$activeCount Tasks",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6E56CF)
                )
            }
        }
    }
}


@Composable
fun MyTasksActionButtons(
    modifier: Modifier = Modifier,
    onAddNewTaskClick: () -> Unit = {},
    onScanClick: () -> Unit = {}
) {

    Row(
        modifier = modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

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
                    onAddNewTaskClick()
                }
                .padding(
                    vertical = 14.dp
                ),

            contentAlignment = Alignment.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "Add New Task",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onScanClick()
                },

            shape = RoundedCornerShape(20.dp),

            color = Color.White,

            border = BorderStroke(
                1.dp,
                Color(0xFFF1F5F9)
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 14.dp
                    ),

                horizontalArrangement = Arrangement.Center,

                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.DocumentScanner,
                    contentDescription = "Scan",
                    tint = Color(0xFF6E56CF),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "Scan Task",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
        }
    }
}


@Composable
fun JoinTaskViaCodeCard(
    modifier: Modifier = Modifier,
    onJoinCollabClick: (String) -> Unit = {}
) {

    var codeInput by remember {
        mutableStateOf("")
    }

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

        Column(
            modifier = Modifier.padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Color(0xFFEDE9FE),
                            CircleShape
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = "Group",
                        tint = Color(0xFF6E56CF),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = "Join Task via Code",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Text(
                text = "Enter 6-digit collab code sent to your email",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(10.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.weight(1f),

                    shape = RoundedCornerShape(14.dp),

                    color = Color(0xFFFAFAFE),

                    border = BorderStroke(
                        1.dp,
                        Color(0xFFF1F5F9)
                    )
                ) {

                    BasicTextField(
                        value = codeInput,

                        onValueChange = {
                            codeInput = it
                        },

                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 12.dp
                        ),

                        decorationBox = { innerTextField ->

                            if (codeInput.isEmpty()) {

                                Text(
                                    text = "e.g. 749210",
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            innerTextField()
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(
                            Color(0xFF6E56CF)
                        )
                        .clickable {
                            onJoinCollabClick(codeInput)
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Join Collab →",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun MyTasksFilterRow(
    modifier: Modifier = Modifier,
    selectedFilter: String = "All Tasks (3)",
    onFilterSelected: (String) -> Unit = {}
) {

    val filters = listOf(
        "All Tasks (3)",
        "In Progress (1)",
        "Not Started (1)",
        "Completed (1)"
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        items(filters) { filter ->

            val isSelected =
                selectedFilter == filter

            Surface(
                modifier = Modifier.clickable {
                    onFilterSelected(filter)
                },

                shape = RoundedCornerShape(999.dp),

                color =
                    if (isSelected)
                        Color(0xFF6E56CF)
                    else
                        Color.White,

                border = BorderStroke(
                    1.dp,

                    if (isSelected)
                        Color(0xFF6E56CF)
                    else
                        Color(0xFFF1F5F9)
                )
            ) {

                Text(
                    text = filter,

                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    ),

                    fontSize = 12.sp,

                    fontWeight = FontWeight.Bold,

                    color =
                        if (isSelected)
                            Color.White
                        else
                            Color(0xFF64748B)
                )
            }
        }
    }
}


@Composable
fun TaskCardDev(
    modifier: Modifier = Modifier,
    onCheckToggle: () -> Unit = {},
    onTaskClick: () -> Unit = {}
) {

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onTaskClick()
            },

        shape = RoundedCornerShape(20.dp),

        color = Color.White,

        shadowElevation = 2.dp,

        border = BorderStroke(
            1.dp,
            Color(0xFFF1F5F9)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),

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
                        text = "Dev",

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
                    verticalAlignment = Alignment.CenterVertically
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
                text = "API Token Integration",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Text(
                text = "Wire real-time collab webhook and auth tokens",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Date",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "Tomorrow, 10:00 AM",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    color = Color(0xFFF1F5F9),

                    shape = RoundedCornerShape(8.dp)
                ) {

                    Text(
                        text = "Not Started",

                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
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


@Composable
fun TaskCardInProgress(
    modifier: Modifier = Modifier,
    onTaskClick: () -> Unit = {}
) {

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onTaskClick()
            },

        shape = RoundedCornerShape(20.dp),

        color = Color.White,

        shadowElevation = 2.dp,

        border = BorderStroke(
            1.dp,
            Color(0xFFF1F5F9)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    color = Color(0xFFE0F2FE),

                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "Design",

                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),

                        color = Color(0xFF0284C7),

                        fontSize = 12.sp,

                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    color = Color(0xFFE0F2FE),

                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "In Progress",

                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),

                        color = Color(0xFF0284C7),

                        fontSize = 11.sp,

                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Finalize Design System Components",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Text(
                text = "Complete the remaining UI components and review consistency.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Date",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "Today, 5:00 PM",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Text(
                    text = "60%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0284C7)
                )
            }
        }
    }
}


@Composable
fun TaskCardStudyChapter(
    modifier: Modifier = Modifier,
    onCheckToggle: () -> Unit = {},
    onTaskClick: () -> Unit = {}
) {

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onTaskClick()
            },

        shape = RoundedCornerShape(20.dp),

        color = Color(0xFFFAFAFE),

        border = BorderStroke(
            1.dp,
            Color(0xFFF1F5F9)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),

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
                        text = "Study",

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
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        color = Color(0xFFD1FAE5),

                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Text(
                            text = "Done",

                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            ),

                            color = Color(0xFF10B981),

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
                            .background(
                                Color(0xFF10B981),
                                CircleShape
                            )
                            .clickable {
                                onCheckToggle()
                            },

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Text(
                text = "Cognitive Psychology Chapter 4",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                textDecoration = TextDecoration.LineThrough
            )

            Text(
                text = "Completed summary mind map and spaced cards",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Finished Yesterday",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                Surface(
                    color = Color(0xFFD1FAE5),

                    shape = RoundedCornerShape(8.dp)
                ) {

                    Text(
                        text = "Completed",

                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),

                        color = Color(0xFF10B981),

                        fontSize = 11.sp,

                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 1000
)
@Composable
fun MyTasksPreview() {

    ZenBackground {

        MyTasksContent()
    }
}

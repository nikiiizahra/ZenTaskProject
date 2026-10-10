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
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun MyTasksScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel
) {
    val tasks by vm.tasks.collectAsState()
    var selectedFilter by remember { mutableStateOf("All Tasks (12)") }
    var showAddTaskModal by remember { mutableStateOf(false) }
    var showJoinCollabModal by remember { mutableStateOf(false) }
    var pendingAttachment by remember { mutableStateOf<String?>(null) }

    // 1. tangkap hasil scan
    val scannedPath by (navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("scanned_pdf_path", null)
        ?: MutableStateFlow<String?>(null)).collectAsState()

    // 2. bereaksi saat hasilnya datang
    LaunchedEffect(scannedPath) {
        scannedPath?.let { path ->
            pendingAttachment = path
            showAddTaskModal = true
            navController.currentBackStackEntry?.savedStateHandle?.set("scanned_pdf_path", null)
        }
    }

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
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                onAddNewTaskClick = { showAddTaskModal = true },
                onTaskToggle = { taskId -> vm.toggleTaskCompletion(taskId) },
                onScanClick = { navController.navigate("scan") }
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
    selectedFilter: String = "All Tasks (12)",
    onFilterSelected: (String) -> Unit = {},
    onAddNewTaskClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
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
            MyTasksHeaderRow()
        }

        item {
            MyTasksTitleSection(activeCount = 12)
        }

        item {
            MyTasksActionButtons(
                onAddNewTaskClick = onAddNewTaskClick,
                onScanClick = onScanClick
            )
        }

        item {
            JoinTaskViaCodeCard()
        }

        item {
            MyTasksFilterRow(
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected
            )
        }

        item {
            TaskCardDev(
                onCheckToggle = { onTaskToggle("dev_1") }
            )
        }

        item {
            TaskCardStudyChapter(
                onCheckToggle = { onTaskToggle("study_1") }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MyTasksHeaderRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFEDE9FE), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AJ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6E56CF)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "TaskFlow",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        Box {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFF1F5F9), CircleShape),
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
                    .background(Color(0xFFF43F5E), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun MyTasksTitleSection(
    modifier: Modifier = Modifier,
    activeCount: Int = 12
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
            Spacer(modifier = Modifier.height(2.dp))
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
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF10B981), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$activeCount Active",
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
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF6E56CF), Color(0xFF8B5CF6))
                    )
                )
                .clickable { onAddNewTaskClick() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(6.dp))
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
                .clickable { onScanClick() },
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(6.dp))
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
    var codeInput by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFEDE9FE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = "Group",
                        tint = Color(0xFF6E56CF),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
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
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    BasicTextField(
                        value = codeInput,
                        onValueChange = { codeInput = it },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
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
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF6E56CF))
                        .clickable { onJoinCollabClick(codeInput) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
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
    selectedFilter: String = "All Tasks (12)",
    onFilterSelected: (String) -> Unit = {}
) {
    val filters = listOf("All Tasks (12)", "In Progress (5)", "Not Started (2)")

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(filters) { f ->
            val isSelected = selectedFilter == f
            Surface(
                modifier = Modifier.clickable { onFilterSelected(f) },
                shape = RoundedCornerShape(999.dp),
                color = if (isSelected) Color(0xFF6E56CF) else Color.White,
                border = BorderStroke(1.dp, if (isSelected) Color(0xFF6E56CF) else Color(0xFFF1F5F9))
            ) {
                Text(
                    text = f,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun TaskCardDev(
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
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color(0xFF6E56CF),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Date",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
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
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
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
fun TaskCardStudyChapter(
    modifier: Modifier = Modifier,
    onCheckToggle: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFAFAFE),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
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
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color(0xFF6E56CF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFD1FAE5),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Done",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color(0xFF10B981),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(Color(0xFF10B981), CircleShape)
                            .clickable { onCheckToggle() },
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
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1000)
@Composable
fun MyTasksPreview() {
    ZenBackground {
        MyTasksContent()
    }
}

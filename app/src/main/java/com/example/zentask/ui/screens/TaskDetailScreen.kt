package com.example.zentask.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.model.TaskItem
import com.example.zentask.model.TaskStatus
import com.example.zentask.ui.components.ZenBackground
import com.example.zentask.viewmodel.ZenTaskViewModel
import com.example.zentask.data.local.CollabInviteEntity
import com.example.zentask.data.local.CollabMemberInfo

@Composable
fun TaskDetailScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel,
    taskId: String
) {
    val tasks by vm.tasks.collectAsState()
    val urgencyMap by vm.urgencyResults.collectAsState()
    val progressMap by vm.taskProgress.collectAsState()
    val invite by remember(taskId) { vm.observeInvite(taskId) }.collectAsState(initial = null)
    val members by remember(taskId) { vm.observeMembers(taskId) }.collectAsState(initial = emptyList())

    val task = tasks.find { it.id == taskId }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var deleted by remember { mutableStateOf(false) }

    ZenBackground {
        Scaffold(containerColor = Color.Transparent) { padding ->
            when {
                task != null -> TaskDetailContent(
                    task = task,
                    urgencyLevel = urgencyMap[task.id]?.level ?: "Low",
                    urgencyScore = urgencyMap[task.id]?.score ?: 0,
                    progress = progressMap[task.id] ?: 0,
                    modifier = Modifier.padding(padding),
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate("edit_task/${task.id}") },
                    onDelete = { showDeleteDialog = true },
                    onStatusChange = { vm.updateTaskStatus(task.id, it) },
                    invite = invite,
                    members = members,
                    onCreateInvite = { vm.createInvite(task.id) },
                    onRevokeInvite = { vm.revokeInvite(task.id) },
                    onRemoveMember = { memberId -> vm.removeMember(task.id, memberId) },
                    onLeave = {
                        deleted = true
                        vm.leaveTask(task.id)
                        navController.popBackStack()
                    }
                )

                deleted -> Unit // sedang menutup layar setelah hapus

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp)
                ) {
                    Text("Task not found", color = ZenTaskTheme.TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            }
        }
    }

    if (showDeleteDialog && task != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete task?") },
            text = { Text("\"${task.title}\" will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    deleted = true
                    vm.deleteTask(task.id)
                    navController.popBackStack()
                }) { Text("Delete", color = Color(0xFFDC2626)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun TaskDetailContent(
    task: TaskItem,
    urgencyLevel: String,
    urgencyScore: Int,
    progress: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    invite: CollabInviteEntity?,
    members: List<CollabMemberInfo>,
    onCreateInvite: () -> Unit,
    onRevokeInvite: () -> Unit,
    onRemoveMember: (String) -> Unit,
    onLeave: () -> Unit
) {
    val finished = task.status == TaskStatus.COMPLETED

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .background(Color.White, CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, "Back",
                    tint = ZenTaskTheme.Primary, modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "Task Detail",
                modifier = Modifier.weight(1f),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ZenTaskTheme.TextPrimary
            )
            Box(
                Modifier
                    .size(38.dp)
                    .background(Color.White, CircleShape)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.DeleteOutline, "Delete task",
                    tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp)
                )
            }
        }

        // Kartu utama
        DetailCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        task.categoryName,
                        color = task.categoryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(task.categoryColor.copy(alpha = 0.12f), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                    Text(
                        task.status.label,
                        color = ZenTaskTheme.Primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(ZenTaskTheme.PrimaryContainer, RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    task.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (finished) ZenTaskTheme.TextSubdued else ZenTaskTheme.TextPrimary,
                    textDecoration = if (finished) TextDecoration.LineThrough else null
                )
                Text(
                    if (task.description.isBlank()) "No description." else task.description,
                    fontSize = 14.sp,
                    color = ZenTaskTheme.TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }

        // Informasi
        DetailCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Task Information", fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
                InfoRow("Priority", task.priority.label)
                InfoRow("Deadline", task.dueText)
                InfoRow("Time left", remainingText(task.remainingHours))
                InfoRow("Urgency", "$urgencyLevel • $urgencyScore", urgencyColor(urgencyLevel))
            }
        }

        task.attachmentPath?.takeIf { it.isNotBlank() }?.let { path ->
            AttachmentCard(path = path)
        }

        // Progress dan status
        DetailCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progress", fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
                    Text(
                        "$progress%",
                        fontWeight = FontWeight.Bold,
                        color = ZenTaskTheme.Primary
                    )
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(ZenTaskTheme.PrimaryContainer)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(progress.coerceIn(0, 100) / 100f)
                            .fillMaxHeight()
                            .background(ZenTaskTheme.Primary, RoundedCornerShape(50))
                    )
                }
                Text("Change status", fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskStatus.values().forEach { status ->
                        val selected = task.status == status
                        Text(
                            status.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else ZenTaskTheme.TextSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (selected) ZenTaskTheme.Primary else Color.White,
                                    RoundedCornerShape(50)
                                )
                                .clickable { onStatusChange(status) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        CollaborationCard(
            task = task,
            invite = invite,
            members = members,
            onCreateInvite = onCreateInvite,
            onRevokeInvite = onRevokeInvite,
            onRemoveMember = onRemoveMember,
            onLeave = onLeave
        )

        // Tombol
        if (task.isOwner) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF6E56CF),
                                Color(0xFF8B5CF6)
                            )
                        )
                    )
                    .clickable(onClick = onEdit),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Edit Task",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clickable(onClick = onDelete),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFFECACA))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "Delete Task",
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

        }
    }
}

@Composable
private fun DetailCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) { content() }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = ZenTaskTheme.TextPrimary) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = ZenTaskTheme.TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

private fun remainingText(hours: Int?): String = when {
    hours == null -> "No deadline"
    hours < 0 -> "Overdue"
    hours < 24 -> "$hours h left"
    else -> "${hours / 24} days left"
}

private fun urgencyColor(level: String): Color = when (level) {
    "Critical" -> Color(0xFFDC2626)
    "High" -> Color(0xFFD97706)
    "Medium" -> Color(0xFF2563EB)
    else -> Color(0xFF059669)
}
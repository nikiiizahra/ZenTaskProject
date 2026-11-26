package com.example.zentask.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentask.ZenTaskTheme
import com.example.zentask.data.local.CollabMemberInfo
import com.example.zentask.data.local.CollabRules
import com.example.zentask.data.local.SubtaskEntity
import com.example.zentask.model.TaskItem

@Composable
fun SubtaskCard(
    task: TaskItem,
    subtasks: List<SubtaskEntity>,
    members: List<CollabMemberInfo>,
    currentUserId: String,
    onAdd: (String, String?) -> Unit,
    onToggle: (String, Boolean) -> Unit,
    onUpdate: (String, String, String?) -> Unit,
    onDelete: (String) -> Unit
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var deletingId by rememberSaveable { mutableStateOf<String?>(null) }
    val red = Color(0xFFDC2626)
    val doneCount = subtasks.count { it.isDone }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sub tasks", fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
                if (subtasks.isNotEmpty()) {
                    Text(
                        "$doneCount/${subtasks.size} done",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZenTaskTheme.Primary
                    )
                }
            }

            if (subtasks.isEmpty()) {
                Text(
                    if (task.isOwner) "Break this task into parts and assign each part to a team member."
                    else "The owner has not added any sub tasks yet.",
                    fontSize = 13.sp,
                    color = ZenTaskTheme.TextSecondary
                )
            }

            subtasks.forEach { sub ->
                val isMine = sub.assigneeId != null && sub.assigneeId == currentUserId
                val canToggle = task.isOwner || isMine
                val assigneeName = sub.assigneeId?.let { id ->
                    members.firstOrNull { it.userId == id }?.name
                }
                val assigneeLabel = when {
                    sub.assigneeId == null -> "Unassigned"
                    assigneeName != null -> "Assigned to $assigneeName"
                    else -> "Assigned"
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = sub.isDone,
                        onCheckedChange = { onToggle(sub.id, it) },
                        enabled = canToggle,
                        colors = CheckboxDefaults.colors(checkedColor = ZenTaskTheme.Primary)
                    )
                    Column(Modifier.weight(1f)) {
                        Text(
                            sub.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (sub.isDone) ZenTaskTheme.TextSubdued else ZenTaskTheme.TextPrimary,
                            textDecoration = if (sub.isDone) TextDecoration.LineThrough else null
                        )
                        Text(assigneeLabel, fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
                    }
                    if (task.isOwner) {
                        IconButton(onClick = { editingId = sub.id }) {
                            Icon(
                                Icons.Outlined.Edit, "Edit sub task",
                                tint = ZenTaskTheme.TextSecondary, modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(onClick = { deletingId = sub.id }) {
                            Icon(
                                Icons.Outlined.DeleteOutline, "Delete sub task",
                                tint = red, modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            if (task.isOwner) {
                OutlinedButton(onClick = { showAddDialog = true }) { Text("Add sub task") }
                if (members.isEmpty()) {
                    Text(
                        "To assign sub tasks to teammates, create an invite code first.",
                        fontSize = 11.sp,
                        color = ZenTaskTheme.TextSubdued
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        SubtaskDialog(
            heading = "Add sub task",
            initialTitle = "",
            initialAssigneeId = null,
            members = members,
            onDismiss = { showAddDialog = false },
            onSave = { title, assignee ->
                onAdd(title, assignee)
                showAddDialog = false
            }
        )
    }

    val editing = subtasks.firstOrNull { it.id == editingId }
    if (editing != null) {
        SubtaskDialog(
            heading = "Edit sub task",
            initialTitle = editing.title,
            initialAssigneeId = editing.assigneeId,
            members = members,
            onDismiss = { editingId = null },
            onSave = { title, assignee ->
                onUpdate(editing.id, title, assignee)
                editingId = null
            }
        )
    }

    val deleting = subtasks.firstOrNull { it.id == deletingId }
    if (deleting != null) {
        AlertDialog(
            onDismissRequest = { deletingId = null },
            title = { Text("Delete sub task?") },
            text = { Text("\"${deleting.title}\" will be removed from this task.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(deleting.id)
                    deletingId = null
                }) { Text("Delete", color = red) }
            },
            dismissButton = { TextButton(onClick = { deletingId = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SubtaskDialog(
    heading: String,
    initialTitle: String,
    initialAssigneeId: String?,
    members: List<CollabMemberInfo>,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit
) {
    var title by rememberSaveable { mutableStateOf(initialTitle) }
    var assigneeId by rememberSaveable { mutableStateOf(initialAssigneeId) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(heading) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it.take(CollabRules.SUBTASK_TITLE_MAX)
                        error = null
                    },
                    label = { Text("Title") },
                    singleLine = true,
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } }
                )
                if (members.isNotEmpty()) {
                    Text("Assign to", fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
                    AssigneeOption("Unassigned", assigneeId == null) { assigneeId = null }
                    members.forEach { member ->
                        AssigneeOption(member.name, assigneeId == member.userId) {
                            assigneeId = member.userId
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isBlank()) error = "Title is required"
                else onSave(title.trim(), assigneeId)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AssigneeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, fontSize = 14.sp)
    }
}
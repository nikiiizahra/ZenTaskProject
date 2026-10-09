package com.example.zentask.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.zentask.ZenTaskTheme
import com.example.zentask.model.TaskPriority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    onSave: (String, String, TaskPriority, String) -> Unit
) {
    var title by remember { mutableStateOf("Design System Hifi") }
    var description by remember { mutableStateOf("Refine pastel tokens, translucent sheets & button styles") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.HIGH) }
    var selectedStatus by remember { mutableStateOf("In Progress") }
    var selectedCategory by remember { mutableStateOf("🎨 UI Design") }
    var dueText by remember { mutableStateOf("24 Oct 2026, 16:00") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .background(Color(0xFFCBD5E1), CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Create New Task",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Keep it lightweight, clear, and actionable",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFF1F5F9), CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Task Title
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Task Title",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFAFAFE),
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFF1F5F9),
                        focusedBorderColor = Color(0xFF6E56CF)
                    )
                )
            }

            // Description
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Description",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFAFAFE),
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFF1F5F9),
                        focusedBorderColor = Color(0xFF6E56CF)
                    )
                )
            }

            // Priority
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Priority",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isLow = selectedPriority == TaskPriority.LOW
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPriority = TaskPriority.LOW },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isLow) Color(0xFFF0F9FF) else Color(0xFFFAFAFE),
                        border = BorderStroke(1.dp, if (isLow) Color(0xFFBAE6FD) else Color(0xFFF1F5F9))
                    ) {
                        Text(
                            text = "Low",
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLow) Color(0xFF0284C7) else Color(0xFF64748B)
                        )
                    }

                    val isMed = selectedPriority == TaskPriority.MEDIUM
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPriority = TaskPriority.MEDIUM },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isMed) Color(0xFFFEF3C7) else Color(0xFFFAFAFE),
                        border = BorderStroke(1.dp, if (isMed) Color(0xFFFDE68A) else Color(0xFFF1F5F9))
                    ) {
                        Text(
                            text = "Medium",
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMed) Color(0xFFD97706) else Color(0xFF64748B)
                        )
                    }

                    val isHigh = selectedPriority == TaskPriority.HIGH
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPriority = TaskPriority.HIGH },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isHigh) Color(0xFFFEE2E2) else Color(0xFFFAFAFE),
                        border = BorderStroke(1.dp, if (isHigh) Color(0xFFFCA5A5) else Color(0xFFF1F5F9))
                    ) {
                        Text(
                            text = "• High",
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHigh) Color(0xFFEF4444) else Color(0xFF64748B)
                        )
                    }
                }
            }

            // Status
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Status",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isNotStarted = selectedStatus == "Not Started"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedStatus = "Not Started" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isNotStarted) Color(0xFFF1F5F9) else Color(0xFFFAFAFE),
                        border = BorderStroke(1.dp, if (isNotStarted) Color(0xFFCBD5E1) else Color(0xFFF1F5F9))
                    ) {
                        Text(
                            text = "Not Started",
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNotStarted) Color(0xFF0F172A) else Color(0xFF64748B)
                        )
                    }

                    val isInProgress = selectedStatus == "In Progress"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedStatus = "In Progress" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isInProgress) Color(0xFFEDE9FE) else Color(0xFFFAFAFE),
                        border = BorderStroke(1.dp, if (isInProgress) Color(0xFF6E56CF) else Color(0xFFF1F5F9))
                    ) {
                        Text(
                            text = "• In Progress",
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isInProgress) Color(0xFF6E56CF) else Color(0xFF64748B)
                        )
                    }

                    val isCompleted = selectedStatus == "Completed"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedStatus = "Completed" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCompleted) Color(0xFFD1FAE5) else Color(0xFFFAFAFE),
                        border = BorderStroke(1.dp, if (isCompleted) Color(0xFF10B981) else Color(0xFFF1F5F9))
                    ) {
                        Text(
                            text = "Completed",
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) Color(0xFF10B981) else Color(0xFF64748B)
                        )
                    }
                }
            }

            // Category
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Category",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFAFAFE),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCategory,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0F172A)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            tint = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Deadline (Optional)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Deadline (Optional)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                OutlinedTextField(
                    value = dueText,
                    onValueChange = { dueText = it },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Calendar",
                            tint = Color(0xFF6E56CF),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFAFAFE),
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFF1F5F9),
                        focusedBorderColor = Color(0xFF6E56CF)
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDismiss() },
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFFAFAFE),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Text(
                        text = "Cancel",
                        modifier = Modifier.padding(vertical = 14.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF6E56CF), Color(0xFF8B5CF6))
                            )
                        )
                        .clickable {
                            if (title.isNotBlank()) {
                                onSave(title, selectedCategory, selectedPriority, dueText)
                            }
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save Task →",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryBottomSheet(
    onDismiss: () -> Unit,
    onSave: (String, String, Color) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🎨") }
    var selectedColor by remember { mutableStateOf(ZenTaskTheme.Swatches.first()) }

    val emojis = listOf("🎨", "📚", "🌿", "💼", "💻", "🎯", "🏋️", "✈️", "🎵", "🧘")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text("Create New Category", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Category Name") },
                placeholder = { Text("e.g. Finance, Wellness, Project") },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text("Select Emoji Icon", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(emojis) { em ->
                    val isSel = emoji == em
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(if (isSel) ZenTaskTheme.PrimaryLight else Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .border(if (isSel) 2.dp else 0.dp, if (isSel) ZenTaskTheme.Primary else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { emoji = em },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(em, fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Pastel Color Swatch", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ZenTaskTheme.Swatches.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(color, CircleShape)
                            .border(if (selectedColor == color) 3.dp else 0.dp, Color.Black, CircleShape)
                            .clickable { selectedColor = color }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            ZenPrimaryButton(
                text = "Save Category",
                onClick = { if (name.isNotBlank()) onSave(name, emoji, selectedColor) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun JoinCollabDialog(onDismiss: () -> Unit, onJoin: (String) -> Unit) {
    var code by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("Join Shared Task", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZenTaskTheme.TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Enter the 6-digit collab invite code shared by your team member.", fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase() },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. ZEN-892") },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))
            ZenPrimaryButton(
                text = "Join Task Now →",
                onClick = { if (code.isNotBlank()) onJoin(code) }
            )
        }
    }
}

@Composable
fun ProBillingDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("ZenTask Pro", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ZenTaskTheme.TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Unlock unlimited vital filters, team sync, and custom pastel swatches.", fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = ZenTaskTheme.PrimaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, ZenTaskTheme.Primary, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Annual Plan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZenTaskTheme.TextPrimary)
                        Text("$29.99 / year (Save 40%)", fontSize = 11.sp, color = ZenTaskTheme.Primary)
                    }
                    Text("Selected", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ZenTaskTheme.Primary)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            ZenPrimaryButton(
                text = "Activate Subscription",
                onClick = onDismiss
            )
        }
    }
}

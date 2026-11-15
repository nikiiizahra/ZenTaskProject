@file:android.annotation.SuppressLint("NewApi")

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.model.TaskPriority
import com.example.zentask.ui.components.ZenBackground
import com.example.zentask.viewmodel.ZenTaskViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val deadlineFormat: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)

private fun parseDeadline(text: String): LocalDate? =
    runCatching {
        LocalDate.parse(
            text.trim(),
            DateTimeFormatter.ofPattern("d/M/uuuu", Locale.US)
        )
    }.getOrNull()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel,
    taskId: String
) {
    val tasks by vm.tasks.collectAsState()
    val categories by vm.categories.collectAsState()
    val task = tasks.find { it.id == taskId }

    var title by rememberSaveable(taskId) { mutableStateOf(task?.title.orEmpty()) }
    var description by rememberSaveable(taskId) { mutableStateOf(task?.description.orEmpty()) }
    var category by rememberSaveable(taskId) { mutableStateOf(task?.categoryName.orEmpty()) }
    var priorityName by rememberSaveable(taskId) {
        mutableStateOf((task?.priority ?: TaskPriority.MEDIUM).name)
    }
    var deadline by rememberSaveable(taskId) { mutableStateOf(task?.dueText.orEmpty()) }

    var titleError by rememberSaveable { mutableStateOf<String?>(null) }
    var formError by rememberSaveable { mutableStateOf<String?>(null) }
    var categoryExpanded by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val priority = runCatching { TaskPriority.valueOf(priorityName) }
        .getOrDefault(TaskPriority.MEDIUM)
    val selectedCategory = categories.firstOrNull {
        it.name.equals(category, ignoreCase = true)
    }

    ZenBackground {
        Scaffold(containerColor = Color.Transparent) { padding ->

            if (task == null) {
                Text(
                    text = "Task not found",
                    color = ZenTaskTheme.TextPrimary,
                    modifier = Modifier.padding(padding).padding(24.dp)
                )
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(38.dp)
                            .background(Color.White, CircleShape)
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, "Back",
                            tint = ZenTaskTheme.Primary, modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Edit Task",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ZenTaskTheme.TextPrimary
                    )
                }

                // Title
                EditLabel("Task Title")
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = null },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = { titleError?.let { Text(it) } },
                    shape = RoundedCornerShape(16.dp),
                    colors = editFieldColors()
                )

                // Description
                EditLabel("Description")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    colors = editFieldColors()
                )

                // Priority (chips, sama seperti form Add)
                EditLabel("Priority")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EditPriorityChip(
                        "Low", priority == TaskPriority.LOW,
                        Color(0xFFF0F9FF), Color(0xFFBAE6FD), Color(0xFF0284C7),
                        Modifier.weight(1f)
                    ) { priorityName = TaskPriority.LOW.name }
                    EditPriorityChip(
                        "Medium", priority == TaskPriority.MEDIUM,
                        Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFFD97706),
                        Modifier.weight(1f)
                    ) { priorityName = TaskPriority.MEDIUM.name }
                    EditPriorityChip(
                        "• High", priority == TaskPriority.HIGH,
                        Color(0xFFFEE2E2), Color(0xFFFCA5A5), Color(0xFFEF4444),
                        Modifier.weight(1f)
                    ) { priorityName = TaskPriority.HIGH.name }
                }

                // Category (dropdown, sama seperti form Add)
                EditLabel("Category")
                Box(Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryExpanded = true },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFAFAFE),
                        border = BorderStroke(
                            1.dp,
                            if (categoryExpanded) ZenTaskTheme.Primary else Color(0xFFF1F5F9)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCategory
                                    ?.let { "${it.emoji} ${it.name}" }
                                    ?: category.ifBlank { "Choose a category" },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = ZenTaskTheme.TextPrimary
                            )
                            Icon(
                                Icons.Default.ArrowDropDown, "Choose a category",
                                tint = ZenTaskTheme.TextSecondary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(option.emoji, fontSize = 18.sp)
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            option.name,
                                            fontSize = 14.sp,
                                            fontWeight = if (selectedCategory?.id == option.id)
                                                FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    category = option.name
                                    categoryExpanded = false
                                },
                                trailingIcon = {
                                    if (selectedCategory?.id == option.id) {
                                        Text("✓", color = ZenTaskTheme.Primary)
                                    }
                                }
                            )
                        }
                    }
                }

                // Deadline (kalender)
                EditLabel("Deadline")
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
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
                            text = deadline.ifBlank { "Select deadline" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZenTaskTheme.TextPrimary
                        )
                        Icon(
                            Icons.Outlined.CalendarToday, "Pick a date",
                            tint = ZenTaskTheme.Primary, modifier = Modifier.size(18.dp)
                        )
                    }
                }

                formError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }

                // Save
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF6E56CF), Color(0xFF8B5CF6))
                            )
                        )
                        .clickable {
                            titleError = if (title.isBlank()) "Task title is required" else null
                            if (titleError == null) {
                                val saved = vm.updateTaskDetails(
                                    taskId = taskId,
                                    title = title,
                                    description = description,
                                    categoryName = category,
                                    priority = priority,
                                    dueText = deadline
                                )
                                if (saved) {
                                    navController.popBackStack()
                                } else {
                                    formError = "Failed to update the task. Please check the category."
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Save Changes",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (showDatePicker) {
        val initialMillis = (parseDeadline(deadline) ?: LocalDate.now())
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        deadline = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .format(deadlineFormat)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun EditLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = ZenTaskTheme.TextPrimary
    )
}

@Composable
private fun editFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = Color(0xFFFAFAFE),
    focusedContainerColor = Color.White,
    unfocusedBorderColor = Color(0xFFF1F5F9),
    focusedBorderColor = Color(0xFF6E56CF)
)

@Composable
private fun EditPriorityChip(
    text: String,
    selected: Boolean,
    selectedBackground: Color,
    selectedBorder: Color,
    selectedText: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) selectedBackground else Color(0xFFFAFAFE),
        border = BorderStroke(1.dp, if (selected) selectedBorder else Color(0xFFF1F5F9))
    ) {
        Box(Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) selectedText else ZenTaskTheme.TextSecondary
            )
        }
    }
}
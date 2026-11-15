package com.example.zentask.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.zentask.ZenTaskTheme
import com.example.zentask.model.TaskCategory
import com.example.zentask.model.TaskPriority
import com.example.zentask.model.TaskStatus
import com.example.zentask.viewmodel.TaskFormViewModel

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

// =====================================================
// CREATE NEW TASK BOTTOM SHEET
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,

    // Kept for compatibility with existing screens.
    onSave: (String, String, TaskPriority, String, String, TaskStatus) -> Unit,
    categories: List<TaskCategory> = emptyList(),

    // New callback that sends every task field.
    // Order:
    // title, category, priority, due,
    // description, status.
    onSaveDetailed: ((
        String,
        String,
        TaskPriority,
        String,
        String,
        TaskStatus
    ) -> Unit)? = null
) {

    val formVm: TaskFormViewModel = viewModel()

    val form by formVm.state.collectAsState()

    // =================================================
    // CATEGORY OPTIONS
    // =================================================

    val defaultCategories = listOf(
        "🎨" to "Design System",
        "📚" to "College / Study",
        "🌿" to "Personal & Health",
        "💼" to "Client Projects",
        "💻" to "Mobile Dev"
    )

    val categoryOptions =
        if (categories.isNotEmpty()) {

            categories.map { category ->
                category.emoji to category.name
            }

        } else {

            defaultCategories
        }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf(
            categoryOptions
                .firstOrNull()
                ?.second
                .orEmpty()
        )
    }

    LaunchedEffect(categoryOptions) {

        val categoryExists =
            categoryOptions.any {
                it.second == selectedCategory
            }

        if (!categoryExists) {

            selectedCategory =
                categoryOptions
                    .firstOrNull()
                    ?.second
                    .orEmpty()
        }

        formVm.onCategoryChange(
            selectedCategory
        )
    }

    val selectedEmoji =
        categoryOptions.firstOrNull {
            it.second == selectedCategory
        }?.first ?: "📁"

    // =================================================
    // DATE PICKER STATE
    // =================================================

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val today = LocalDate.now()

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern(
            "dd/MM/yyyy",
            Locale.US
        )
    }

    val selectedDateFromForm =
        remember(form.dueText) {

            runCatching {

                LocalDate.parse(
                    form.dueText,
                    dateFormatter
                )

            }.getOrNull()
        }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis =
            selectedDateFromForm
                ?.toEpochDay()
                ?.times(86_400_000L),

        selectableDates = object : SelectableDates {

            override fun isSelectableDate(
                utcTimeMillis: Long
            ): Boolean {

                val selectedDay =
                    Instant.ofEpochMilli(
                        utcTimeMillis
                    )
                        .atZone(
                            ZoneOffset.UTC
                        )
                        .toLocalDate()

                return !selectedDay.isBefore(
                    LocalDate.now()
                )
            }

            override fun isSelectableYear(
                year: Int
            ): Boolean {

                return year >=
                        LocalDate.now().year
            }
        }
    )

    // =================================================
    // MAIN BOTTOM SHEET
    // =================================================

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        ),
        dragHandle = {

            Box(
                modifier = Modifier
                    .padding(
                        vertical = 10.dp
                    )
                    .width(36.dp)
                    .height(4.dp)
                    .background(
                        Color(0xFFCBD5E1),
                        CircleShape
                    )
            )
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 8.dp
                )
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    14.dp
                )
        ) {

            // =========================================
            // HEADER
            // =========================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
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
                        .background(
                            Color(0xFFF1F5F9),
                            CircleShape
                        )
                        .clickable {
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // =========================================
            // TASK TITLE
            // =========================================

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = "Task Title",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                OutlinedTextField(
                    value = form.title,
                    onValueChange = {
                        formVm.onTitleChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Enter task title")
                    },
                    singleLine = true,
                    isError =
                        form.titleError != null,
                    supportingText = {

                        form.titleError?.let {
                                error ->

                            Text(error)
                        }
                    },
                    shape =
                        RoundedCornerShape(16.dp),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor =
                                Color(0xFFFAFAFE),
                            focusedContainerColor =
                                Color.White,
                            unfocusedBorderColor =
                                Color(0xFFF1F5F9),
                            focusedBorderColor =
                                Color(0xFF6E56CF)
                        )
                )
            }

            // =========================================
            // DESCRIPTION
            // =========================================

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = "Description",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                OutlinedTextField(
                    value = form.description,
                    onValueChange = {
                        formVm.onDescriptionChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Add task details")
                    },
                    minLines = 2,
                    maxLines = 4,
                    isError =
                        form.descriptionError != null,
                    supportingText = {

                        form.descriptionError?.let {
                                error ->

                            Text(error)
                        }
                    },
                    shape =
                        RoundedCornerShape(16.dp),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor =
                                Color(0xFFFAFAFE),
                            focusedContainerColor =
                                Color.White,
                            unfocusedBorderColor =
                                Color(0xFFF1F5F9),
                            focusedBorderColor =
                                Color(0xFF6E56CF)
                        )
                )
            }

            // =========================================
            // PRIORITY
            // =========================================

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "Priority",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    TaskOptionChip(
                        text = "Low",
                        selected =
                            form.priority ==
                                    TaskPriority.LOW,
                        selectedBackground =
                            Color(0xFFF0F9FF),
                        selectedBorder =
                            Color(0xFFBAE6FD),
                        selectedText =
                            Color(0xFF0284C7),
                        modifier =
                            Modifier.weight(1f),
                        onClick = {

                            formVm.onPriorityChange(
                                TaskPriority.LOW
                            )
                        }
                    )

                    TaskOptionChip(
                        text = "Medium",
                        selected =
                            form.priority ==
                                    TaskPriority.MEDIUM,
                        selectedBackground =
                            Color(0xFFFEF3C7),
                        selectedBorder =
                            Color(0xFFFDE68A),
                        selectedText =
                            Color(0xFFD97706),
                        modifier =
                            Modifier.weight(1f),
                        onClick = {

                            formVm.onPriorityChange(
                                TaskPriority.MEDIUM
                            )
                        }
                    )

                    TaskOptionChip(
                        text = "• High",
                        selected =
                            form.priority ==
                                    TaskPriority.HIGH,
                        selectedBackground =
                            Color(0xFFFEE2E2),
                        selectedBorder =
                            Color(0xFFFCA5A5),
                        selectedText =
                            Color(0xFFEF4444),
                        modifier =
                            Modifier.weight(1f),
                        onClick = {

                            formVm.onPriorityChange(
                                TaskPriority.HIGH
                            )
                        }
                    )
                }
            }

            // =========================================
            // STATUS
            // =========================================

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "Status",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    TaskOptionChip(
                        text = "Not Started",
                        selected =
                            form.status ==
                                    "Not Started",
                        selectedBackground =
                            Color(0xFFF1F5F9),
                        selectedBorder =
                            Color(0xFFCBD5E1),
                        selectedText =
                            Color(0xFF0F172A),
                        modifier =
                            Modifier.weight(1f),
                        onClick = {

                            formVm.onStatusChange(
                                "Not Started"
                            )
                        }
                    )

                    TaskOptionChip(
                        text = "In Progress",
                        selected =
                            form.status ==
                                    "In Progress",
                        selectedBackground =
                            Color(0xFFEDE9FE),
                        selectedBorder =
                            Color(0xFF6E56CF),
                        selectedText =
                            Color(0xFF6E56CF),
                        modifier =
                            Modifier.weight(1f),
                        onClick = {

                            formVm.onStatusChange(
                                "In Progress"
                            )
                        }
                    )

                    TaskOptionChip(
                        text = "Completed",
                        selected =
                            form.status ==
                                    "Completed",
                        selectedBackground =
                            Color(0xFFD1FAE5),
                        selectedBorder =
                            Color(0xFF10B981),
                        selectedText =
                            Color(0xFF10B981),
                        modifier =
                            Modifier.weight(1f),
                        onClick = {

                            formVm.onStatusChange(
                                "Completed"
                            )
                        }
                    )
                }
            }

            // =========================================
            // CATEGORY DROPDOWN
            // =========================================

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = "Category",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                categoryExpanded = true
                            },
                        shape =
                            RoundedCornerShape(16.dp),
                        color =
                            Color(0xFFFAFAFE),
                        border = BorderStroke(
                            1.dp,
                            if (categoryExpanded) {
                                Color(0xFF6E56CF)
                            } else {
                                Color(0xFFF1F5F9)
                            }
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 14.dp
                                ),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text =
                                    "$selectedEmoji $selectedCategory",
                                fontSize = 14.sp,
                                fontWeight =
                                    FontWeight.Medium,
                                color =
                                    Color(0xFF0F172A)
                            )

                            Icon(
                                imageVector =
                                    Icons.Default.ArrowDropDown,
                                contentDescription =
                                    "Choose a category",
                                tint =
                                    Color(0xFF64748B)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = {
                            categoryExpanded = false
                        }
                    ) {

                        categoryOptions.forEach {
                                option ->

                            val emoji =
                                option.first

                            val name =
                                option.second

                            DropdownMenuItem(
                                text = {

                                    Row(
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text = emoji,
                                            fontSize = 18.sp
                                        )

                                        Spacer(
                                            Modifier.width(10.dp)
                                        )

                                        Text(
                                            text = name,
                                            fontSize = 14.sp,
                                            fontWeight =
                                                if (
                                                    selectedCategory ==
                                                    name
                                                ) {
                                                    FontWeight.Bold
                                                } else {
                                                    FontWeight.Normal
                                                }
                                        )
                                    }
                                },
                                onClick = {

                                    selectedCategory =
                                        name

                                    formVm.onCategoryChange(
                                        name
                                    )

                                    categoryExpanded =
                                        false
                                },
                                trailingIcon = {

                                    if (
                                        selectedCategory ==
                                        name
                                    ) {

                                        Text(
                                            text = "✓",
                                            color =
                                                Color(0xFF6E56CF)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                form.categoryError?.let {
                        error ->

                    Text(
                        text = error,
                        color =
                            MaterialTheme
                                .colorScheme.error,
                        fontSize = 11.sp
                    )
                }
            }

            // =========================================
            // DEADLINE
            // =========================================

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = "Deadline (Optional)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDatePicker = true
                        },
                    shape =
                        RoundedCornerShape(16.dp),
                    color =
                        Color(0xFFFAFAFE),
                    border = BorderStroke(
                        1.dp,
                        Color(0xFFF1F5F9)
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 14.dp
                        ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Outlined.CalendarToday,
                            contentDescription =
                                "Open calendar",
                            tint =
                                Color(0xFF6E56CF),
                            modifier =
                                Modifier.size(18.dp)
                        )

                        Spacer(
                            Modifier.width(12.dp)
                        )

                        Text(
                            text =
                                form.dueText.ifBlank {
                                    "Select a deadline"
                                },
                            modifier =
                                Modifier.weight(1f),
                            fontSize = 14.sp,
                            color =
                                if (
                                    form.dueText.isBlank()
                                ) {
                                    Color(0xFF94A3B8)
                                } else {
                                    Color(0xFF0F172A)
                                }
                        )

                        Icon(
                            imageVector =
                                Icons.Default.ArrowDropDown,
                            contentDescription =
                                "Open calendar",
                            tint =
                                Color(0xFF64748B)
                        )
                    }
                }

                form.dueError?.let {
                        error ->

                    Text(
                        text = error,
                        fontSize = 11.sp,
                        color =
                            MaterialTheme
                                .colorScheme.error
                    )
                }

                if (
                    form.dueText.isNotBlank()
                ) {

                    TextButton(
                        onClick = {
                            formVm.onDueTextChange(
                                ""
                            )
                        },
                        modifier =
                            Modifier.align(
                                Alignment.End
                            )
                    ) {

                        Text(
                            "Clear deadline"
                        )
                    }
                }
            }

            Spacer(
                Modifier.height(6.dp)
            )

            // =========================================
            // CANCEL AND SAVE
            // =========================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onDismiss()
                        },
                    shape =
                        RoundedCornerShape(18.dp),
                    color =
                        Color(0xFFFAFAFE),
                    border = BorderStroke(
                        1.dp,
                        Color(0xFFF1F5F9)
                    )
                ) {

                    Text(
                        text = "Cancel",
                        modifier =
                            Modifier.padding(
                                vertical = 14.dp
                            ),
                        textAlign =
                            TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .clip(
                            RoundedCornerShape(18.dp)
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

                            if (
                                selectedCategory.isBlank()
                            ) {
                                return@clickable
                            }

                            formVm.onCategoryChange(
                                selectedCategory
                            )

                            if (
                                !formVm.validateAll()
                            ) {
                                return@clickable
                            }

                            // Convert form status into
                            // the TaskStatus enum.
                            val selectedStatus =
                                when (form.status) {

                                    "In Progress" ->
                                        TaskStatus.IN_PROGRESS

                                    "Completed" ->
                                        TaskStatus.COMPLETED

                                    else ->
                                        TaskStatus.NOT_STARTED
                                }

                            if (
                                onSaveDetailed != null
                            ) {

                                // New full-data callback.
                                onSaveDetailed(
                                    form.title.trim(),
                                    selectedCategory,
                                    form.priority,
                                    form.dueText,
                                    form.description.trim(),
                                    selectedStatus
                                )

                            } else {

                                // Backward-compatible
                                // callback for other screens.
                                val status = TaskStatus.values().firstOrNull { it.label == form.status }
                                    ?: TaskStatus.NOT_STARTED
                                onSave(form.title.trim(), form.category, form.priority, form.dueText, form.description.trim(), status)
                                formVm.reset()
                            }

                            formVm.reset()
                        }
                        .padding(
                            vertical = 14.dp
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "Save Task →",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(
                Modifier.height(16.dp)
            )
        }
    }

    // =================================================
    // CALENDAR DIALOG
    // =================================================

    if (showDatePicker) {

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {

                TextButton(
                    enabled =
                        datePickerState
                            .selectedDateMillis != null,
                    onClick = {

                        datePickerState
                            .selectedDateMillis
                            ?.let { millis ->

                                val selectedDate =
                                    Instant.ofEpochMilli(
                                        millis
                                    )
                                        .atZone(
                                            ZoneOffset.UTC
                                        )
                                        .toLocalDate()

                                val formattedDate =
                                    selectedDate.format(
                                        dateFormatter
                                    )

                                formVm.onDueTextChange(
                                    formattedDate
                                )
                            }

                        showDatePicker = false
                    }
                ) {

                    Text("OK")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {

                    Text("Cancel")
                }
            }
        ) {

            DatePicker(
                state = datePickerState,
                title = {

                    Text(
                        text = "Select Deadline",
                        modifier =
                            Modifier.padding(
                                start = 24.dp,
                                top = 16.dp
                            )
                    )
                }
            )
        }
    }
}

// =====================================================
// REUSABLE PRIORITY / STATUS CHIP
// =====================================================

@Composable
private fun TaskOptionChip(
    text: String,
    selected: Boolean,
    selectedBackground: Color,
    selectedBorder: Color,
    selectedText: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier.clickable {
            onClick()
        },
        shape =
            RoundedCornerShape(12.dp),
        color =
            if (selected) {
                selectedBackground
            } else {
                Color(0xFFFAFAFE)
            },
        border = BorderStroke(
            1.dp,
            if (selected) {
                selectedBorder
            } else {
                Color(0xFFF1F5F9)
            }
        )
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                vertical = 10.dp,
                horizontal = 2.dp
            ),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color =
                if (selected) {
                    selectedText
                } else {
                    Color(0xFF64748B)
                }
        )
    }
}

// =====================================================
// CREATE NEW CATEGORY
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryBottomSheet(
    onDismiss: () -> Unit,
    onSave: (
        String,
        String,
        Color
    ) -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var emoji by remember {
        mutableStateOf("🎨")
    }

    var selectedColor by remember {
        mutableStateOf(
            ZenTaskTheme.Swatches.first()
        )
    }

    val emojis = listOf(
        "🎨", "📚", "🌿", "💼", "💻",
        "🎯", "🏋️", "✈️", "🎵", "🧘"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
        ) {

            Text(
                text = "Create New Category",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color =
                    ZenTaskTheme.TextPrimary
            )

            Spacer(
                Modifier.height(14.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Category Name")
                },
                placeholder = {
                    Text(
                        "e.g. Finance, Wellness, Project"
                    )
                },
                shape =
                    RoundedCornerShape(16.dp)
            )

            Spacer(
                Modifier.height(14.dp)
            )

            Text(
                text = "Select Emoji Icon",
                fontSize = 12.sp,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    ZenTaskTheme.TextPrimary
            )

            Spacer(
                Modifier.height(8.dp)
            )

            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(emojis) { option ->

                    val isSelected =
                        emoji == option

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (isSelected) {
                                    ZenTaskTheme.PrimaryLight
                                } else {
                                    Color(0xFFF1F5F9)
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                if (isSelected) {
                                    2.dp
                                } else {
                                    0.dp
                                },
                                if (isSelected) {
                                    ZenTaskTheme.Primary
                                } else {
                                    Color.Transparent
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                emoji = option
                            },
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = option,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(
                Modifier.height(14.dp)
            )

            Text(
                text = "Pastel Color Swatch",
                fontSize = 12.sp,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    ZenTaskTheme.TextPrimary
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                ZenTaskTheme.Swatches.forEach {
                        color ->

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                color,
                                CircleShape
                            )
                            .border(
                                if (
                                    selectedColor ==
                                    color
                                ) {
                                    3.dp
                                } else {
                                    0.dp
                                },
                                Color.Black,
                                CircleShape
                            )
                            .clickable {
                                selectedColor =
                                    color
                            }
                    )
                }
            }

            Spacer(
                Modifier.height(22.dp)
            )

            ZenPrimaryButton(
                text = "Save Category",
                onClick = {

                    if (name.isNotBlank()) {

                        onSave(
                            name.trim(),
                            emoji,
                            selectedColor
                        )
                    }
                }
            )

            Spacer(
                Modifier.height(16.dp)
            )
        }
    }
}

// =====================================================
// JOIN COLLABORATION
// =====================================================

@Composable
fun JoinCollabDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {

    var code by remember {
        mutableStateOf("")
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {

        GlassCard(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Join Shared Task",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color =
                    ZenTaskTheme.TextPrimary
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = "Enter the invite code shared by your team member.",
                fontSize = 12.sp,
                color =
                    ZenTaskTheme.TextSecondary
            )

            Spacer(
                Modifier.height(14.dp)
            )

            OutlinedTextField(
                value = code,
                onValueChange = {
                    code = it.uppercase()
                },
                modifier =
                    Modifier.fillMaxWidth(),
                placeholder = {
                    Text("e.g. ZEN-892")
                },
                shape =
                    RoundedCornerShape(16.dp)
            )

            Spacer(
                Modifier.height(18.dp)
            )

            ZenPrimaryButton(
                text = "Join Task Now →",
                onClick = {

                    if (code.isNotBlank()) {

                        onJoin(
                            code.trim()
                        )
                    }
                }
            )
        }
    }
}

// =====================================================
// PRO BILLING - DEMO MODE
// =====================================================

@Composable
fun ProBillingDialog(
    onDismiss: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {

        GlassCard(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text = "ZenTask Pro",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color =
                    ZenTaskTheme.TextPrimary
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text = "Subscription and online synchronization are not available in this demo version.",
                fontSize = 12.sp,
                color =
                    ZenTaskTheme.TextSecondary
            )

            Spacer(
                Modifier.height(14.dp)
            )

            Surface(
                color =
                    ZenTaskTheme.PrimaryContainer,
                shape =
                    RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        ZenTaskTheme.Primary,
                        RoundedCornerShape(16.dp)
                    )
            ) {

                Row(
                    modifier =
                        Modifier.padding(14.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "Demo Mode",
                            fontWeight =
                                FontWeight.Bold,
                            fontSize = 14.sp,
                            color =
                                ZenTaskTheme.TextPrimary
                        )

                        Text(
                            text = "No subscription required",
                            fontSize = 11.sp,
                            color =
                                ZenTaskTheme.Primary
                        )
                    }

                    Text(
                        text = "Active",
                        fontWeight =
                            FontWeight.Bold,
                        fontSize = 12.sp,
                        color =
                            ZenTaskTheme.Primary
                    )
                }
            }

            Spacer(
                Modifier.height(18.dp)
            )

            ZenPrimaryButton(
                text = "Close",
                onClick = onDismiss
            )
        }
    }
}

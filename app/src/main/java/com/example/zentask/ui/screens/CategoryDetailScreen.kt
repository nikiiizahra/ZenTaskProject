
package com.example.zentask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

import com.example.zentask.ZenTaskTheme
import com.example.zentask.model.TaskStatus
import com.example.zentask.ui.components.ZenBackground
import com.example.zentask.viewmodel.ZenTaskViewModel

// =====================================================
// CATEGORY DETAIL SCREEN
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    navController: NavHostController,
    categoryName: String,
    vm: ZenTaskViewModel = viewModel()
) {

    // Read live categories and tasks from
    // the Room-backed ZenTaskViewModel.
    val categories by vm.categories.collectAsState()
    val tasks by vm.tasks.collectAsState()

    // Find the selected category.
    val selectedCategory = categories.firstOrNull {
        it.name == categoryName
    }

    val categoryColor =
        selectedCategory?.color ?: ZenTaskTheme.Primary

    val categoryEmoji =
        selectedCategory?.emoji.orEmpty()

    // Filter tasks using the selected category.
    // Only active-account tasks are exposed by
    // the ViewModel's Room observers.
    val categoryTasks = tasks.filter { task ->
        task.categoryName == categoryName
    }

    // Actual statistics.
    val totalTasks = categoryTasks.size

    val completedCount = categoryTasks.count {
        it.status == TaskStatus.COMPLETED
    }

    val progressPercentage =
        if (totalTasks > 0) {
            completedCount * 100 / totalTasks
        } else {
            0
        }

    val progressValue =
        (progressPercentage / 100f)
            .coerceIn(0f, 1f)

    ZenBackground {

        Scaffold(
            containerColor = Color.Transparent,

            // =========================================
            // TOP BAR
            // =========================================

            topBar = {

                TopAppBar(
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        ),

                    title = {

                        Text(
                            text = categoryName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },

                    navigationIcon = {

                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF0F172A)
                            )
                        }
                    }
                )
            }

        ) { paddingValues ->

            // =========================================
            // CONTENT
            // =========================================

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                item {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )
                }

                // =====================================
                // CATEGORY SUMMARY
                // =====================================

                item {

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {

                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        categoryColor.copy(
                                            alpha = 0.15f
                                        ),
                                        RoundedCornerShape(15.dp)
                                    ),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                if (categoryEmoji.isNotBlank()) {

                                    Text(
                                        text = categoryEmoji,
                                        fontSize = 24.sp
                                    )

                                } else {

                                    Icon(
                                        imageVector =
                                            Icons.Outlined.Folder,
                                        contentDescription = "Category",
                                        tint = categoryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.width(14.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = categoryName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text =
                                        "$totalTasks Tasks • $completedCount Completed",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF475569)
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                LinearProgressIndicator(
                                    progress = {
                                        progressValue
                                    },

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),

                                    color = categoryColor,

                                    trackColor =
                                        categoryColor.copy(
                                            alpha = 0.15f
                                        )
                                )
                            }
                        }
                    }
                }

                // =====================================
                // TASK SECTION HEADER
                // =====================================

                item {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Tasks in $categoryName",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "$progressPercentage%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        )
                    }
                }

                // =====================================
                // EMPTY CATEGORY STATE
                // =====================================

                if (categoryTasks.isEmpty()) {

                    item {

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 1.dp
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 24.dp,
                                        vertical = 36.dp
                                    ),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.Folder,
                                    contentDescription = null,
                                    tint = categoryColor,
                                    modifier = Modifier.size(36.dp)
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                Text(
                                    text = "No tasks in this category yet",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text(
                                    text =
                                        "Create a task and choose $categoryName to see it here.",
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                } else {

                    // =================================
                    // REAL TASKS FROM ROOM
                    // =================================

                    items(
                        items = categoryTasks,
                        key = { task -> task.id }
                    ) { task ->

                        val statusLabel: String
                        val statusColor: Color
                        val statusBackground: Color

                        when (task.status) {

                            TaskStatus.COMPLETED -> {

                                statusLabel = "Completed"
                                statusColor = Color(0xFF059669)
                                statusBackground =
                                    Color(0xFFD1FAE5)
                            }

                            TaskStatus.IN_PROGRESS -> {

                                statusLabel = "In Progress"
                                statusColor = Color(0xFF0284C7)
                                statusBackground =
                                    Color(0xFFE0F2FE)
                            }

                            TaskStatus.NOT_STARTED -> {

                                statusLabel = "Not Started"
                                statusColor = Color(0xFF64748B)
                                statusBackground =
                                    Color(0xFFF1F5F9)
                            }
                        }

                        CategoryTaskCard(
                            title = task.title,

                            description =
                                task.description.ifBlank {
                                    "No description added."
                                },

                            status = statusLabel,

                            deadline =
                                task.dueText.ifBlank {
                                    "No deadline"
                                },

                            statusColor = statusColor,

                            statusBackground =
                                statusBackground,

                            onClick = {

                                // The navigation graph expects:
                                // task_detail/{taskId}
                                navController.navigate(
                                    "task_detail/${task.id}"
                                )
                            }
                        )
                    }
                }

                item {

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}

// =====================================================
// CATEGORY TASK CARD
// =====================================================

@Composable
fun CategoryTaskCard(
    title: String,
    description: String,
    status: String,
    deadline: String,
    statusColor: Color,
    statusBackground: Color,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(20.dp),

        color = Color.White,

        shadowElevation = 2.dp
    ) {

        Column(
            modifier = Modifier.padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            // =========================================
            // TASK TITLE AND STATUS
            // =========================================

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = title,

                    modifier = Modifier.weight(1f),

                    fontSize = 15.sp,

                    fontWeight = FontWeight.Bold,

                    color = Color(0xFF0F172A),

                    maxLines = 2,

                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Surface(
                    color = statusBackground,
                    shape = RoundedCornerShape(999.dp)
                ) {

                    Text(
                        text = status,

                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        ),

                        fontSize = 11.sp,

                        fontWeight = FontWeight.Bold,

                        color = statusColor
                    )
                }
            }

            // =========================================
            // TASK DESCRIPTION
            // =========================================

            Text(
                text = description,

                fontSize = 13.sp,

                lineHeight = 18.sp,

                color = Color(0xFF475569),

                maxLines = 3,

                overflow = TextOverflow.Ellipsis
            )

            // =========================================
            // TASK DEADLINE
            // =========================================

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined.CalendarToday,

                    contentDescription = "Deadline",

                    tint = Color(0xFF64748B),

                    modifier = Modifier.size(15.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = deadline,

                    fontSize = 12.sp,

                    fontWeight = FontWeight.Medium,

                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

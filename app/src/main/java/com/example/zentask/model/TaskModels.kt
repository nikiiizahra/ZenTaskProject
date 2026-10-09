package com.example.zentask.model

import androidx.compose.ui.graphics.Color

enum class TaskPriority(val label: String, val color: Color, val bg: Color) {
    LOW("LOW", Color(0xFF60A5FA), Color(0xFFEFF6FF)),
    MEDIUM("MEDIUM", Color(0xFFF59E0B), Color(0xFFFEF3C7)),
    HIGH("HIGH", Color(0xFFF43F5E), Color(0xFFFFE4E6))
}

enum class TaskStatus(val label: String, val color: Color, val bg: Color) {
    NOT_STARTED("Not Started", Color(0xFF94A3B8), Color(0xFFF1F5F9)),
    IN_PROGRESS("In Progress", Color(0xFF38BDF8), Color(0xFFE0F2FE)),
    COMPLETED("Completed", Color(0xFF10B981), Color(0xFFD1FAE5))
}

data class TaskItem(
    val id: String,
    val title: String,
    val categoryName: String,
    val categoryColor: Color,
    val priority: TaskPriority,
    val status: TaskStatus,
    val dueText: String,
    val remainingHours: Int? = null,
    val isVital: Boolean = false,
    val collabCode: String? = null
)

data class TaskCategory(
    val id: String,
    val name: String,
    val emoji: String,
    val color: Color,
    val taskCount: Int
)

data class UserProfile(
    val fullName: String,
    val email: String,
    val isPro: Boolean = false,
    val pushEnabled: Boolean = true,
    val emailNotifEnabled: Boolean = true
)

package com.example.zentask.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.zentask.model.*
import kotlinx.coroutines.flow.MutableStateFlow

class ZenTaskViewModel : ViewModel() {
    val userProfile = MutableStateFlow(UserProfile("Alex", "alex@example.com"))

    val categories = MutableStateFlow(
        listOf(
            TaskCategory("cat_1", "Design System", "🎨", Color(0xFF60A5FA), 12),
            TaskCategory("cat_2", "College / Study", "📚", Color(0xFF8B5CF6), 8),
            TaskCategory("cat_3", "Personal & Health", "🌿", Color(0xFF10B981), 5),
            TaskCategory("cat_4", "Client Projects", "💼", Color(0xFFF59E0B), 3),
            TaskCategory("cat_5", "Mobile Dev", "💻", Color(0xFF38BDF8), 6)
        )
    )

    val tasks = MutableStateFlow(
        listOf(
            TaskItem(
                id = "t1",
                title = "Finalize Client Pitch Deck & Review",
                categoryName = "Client Projects",
                categoryColor = Color(0xFFF59E0B),
                priority = TaskPriority.HIGH,
                status = TaskStatus.IN_PROGRESS,
                dueText = "Today, 4:00 PM",
                remainingHours = 14,
                isVital = true,
                collabCode = "ZEN-892"
            ),
            TaskItem(
                id = "t2",
                title = "Submit Mobile UI Spec Jetpack Compose",
                categoryName = "Design System",
                categoryColor = Color(0xFF60A5FA),
                priority = TaskPriority.HIGH,
                status = TaskStatus.NOT_STARTED,
                dueText = "Tomorrow, 11:00 AM",
                remainingHours = 32,
                isVital = true
            ),
            TaskItem(
                id = "t3",
                title = "Read Architecture Chapter 4",
                categoryName = "College / Study",
                categoryColor = Color(0xFF8B5CF6),
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.IN_PROGRESS,
                dueText = "Thursday, 8:00 PM",
                remainingHours = 60,
                isVital = false
            ),
            TaskItem(
                id = "t4",
                title = "Morning Guided Meditation (15 mins)",
                categoryName = "Personal & Health",
                categoryColor = Color(0xFF10B981),
                priority = TaskPriority.LOW,
                status = TaskStatus.COMPLETED,
                dueText = "Done Today",
                remainingHours = null,
                isVital = false
            ),
            TaskItem(
                id = "t5",
                title = "Review Kotlin Coroutines Dispatchers",
                categoryName = "Mobile Dev",
                categoryColor = Color(0xFF38BDF8),
                priority = TaskPriority.LOW,
                status = TaskStatus.COMPLETED,
                dueText = "Done Yesterday",
                remainingHours = null,
                isVital = false
            )
        )
    )

    fun toggleTaskCompletion(taskId: String) {
        tasks.value = tasks.value.map {
            if (it.id == taskId) {
                val newStatus = if (it.status == TaskStatus.COMPLETED) TaskStatus.IN_PROGRESS else TaskStatus.COMPLETED
                it.copy(status = newStatus)
            } else it
        }
    }

    fun addTask(title: String, categoryName: String, priority: TaskPriority, due: String) {
        val cat = categories.value.find { it.name == categoryName } ?: categories.value.first()
        val newTask = TaskItem(
            id = "t_${System.currentTimeMillis()}",
            title = title,
            categoryName = cat.name,
            categoryColor = cat.color,
            priority = priority,
            status = TaskStatus.NOT_STARTED,
            dueText = due.ifBlank { "In 2 days" },
            remainingHours = if (priority == TaskPriority.HIGH) 24 else 72,
            isVital = priority == TaskPriority.HIGH
        )
        tasks.value = listOf(newTask) + tasks.value
    }

    fun addCategory(name: String, emoji: String, color: Color) {
        val newCat = TaskCategory(
            id = "cat_${System.currentTimeMillis()}",
            name = name,
            emoji = emoji.ifBlank { "✨" },
            color = color,
            taskCount = 0
        )
        categories.value = categories.value + newCat
    }

    fun joinCollabTask(code: String): Boolean {
        if (code.isNotBlank()) {
            val joinedTask = TaskItem(
                id = "t_collab_${System.currentTimeMillis()}",
                title = "Shared Team Sprint: $code",
                categoryName = "Client Projects",
                categoryColor = Color(0xFFF59E0B),
                priority = TaskPriority.HIGH,
                status = TaskStatus.IN_PROGRESS,
                dueText = "Tomorrow, 5:00 PM",
                remainingHours = 28,
                isVital = true,
                collabCode = code
            )
            tasks.value = listOf(joinedTask) + tasks.value
            return true
        }
        return false
    }
}

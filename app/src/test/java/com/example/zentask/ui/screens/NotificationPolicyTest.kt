package com.example.zentask.ui.screens

import androidx.compose.ui.graphics.Color
import com.example.zentask.model.TaskItem
import com.example.zentask.model.TaskPriority
import com.example.zentask.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationPolicyTest {

    @Test
    fun filterTaskNotifications_returns_empty_when_push_notifications_are_disabled() {
        val tasks = listOf(
            TaskItem(
                id = "1",
                title = "Follow up with client",
                categoryName = "Work",
                categoryColor = Color(0xFF8B5CF6),
                priority = TaskPriority.HIGH,
                status = TaskStatus.IN_PROGRESS,
                dueText = "12/12/2026"
            )
        )

        val result = filterTaskNotifications(tasks, pushEnabled = false)

        assertEquals(0, result.size)
    }
}

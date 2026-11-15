package com.example.zentask.analytics

import androidx.compose.ui.graphics.Color
import com.example.zentask.model.TaskItem
import com.example.zentask.model.TaskPriority
import com.example.zentask.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalAnalyticsEngineTest {

    @Test
    fun summarize_tracks_completion_rate_and_top_category() {
        val tasks = listOf(
            TaskItem(
                id = "1",
                title = "Design kickoff",
                categoryName = "Design",
                categoryColor = Color(0xFF8B5CF6),
                priority = TaskPriority.HIGH,
                status = TaskStatus.COMPLETED,
                dueText = "03/12/2024"
            ),
            TaskItem(
                id = "2",
                title = "Review mockups",
                categoryName = "Design",
                categoryColor = Color(0xFF8B5CF6),
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.COMPLETED,
                dueText = "04/12/2024"
            ),
            TaskItem(
                id = "3",
                title = "Write notes",
                categoryName = "Personal",
                categoryColor = Color(0xFF10B981),
                priority = TaskPriority.LOW,
                status = TaskStatus.IN_PROGRESS,
                dueText = "05/12/2024"
            )
        )

        val snapshot = LocalAnalyticsEngine.summarize(tasks)

        assertEquals(3, snapshot.totalTasks)
        assertEquals(2, snapshot.completedCount)
        assertEquals(67, snapshot.completionRate)
        assertEquals("Design", snapshot.mostActiveCategory)
        assertEquals(2, snapshot.productivityStreak)
        assertEquals(7, snapshot.weeklyTrend.size)
        assertEquals("This week is trending upward.", snapshot.weeklyInsight)
        assertEquals(1, snapshot.backlogPressure)
    }

    @Test
    fun summarize_tracks_overdue_and_recommendation_steps() {
        val tasks = listOf(
            TaskItem(
                id = "1",
                title = "Urgent review",
                categoryName = "Work",
                categoryColor = Color(0xFF8B5CF6),
                priority = TaskPriority.HIGH,
                status = TaskStatus.IN_PROGRESS,
                dueText = "01/01/2020"
            ),
            TaskItem(
                id = "2",
                title = "Plan next sprint",
                categoryName = "Work",
                categoryColor = Color(0xFF8B5CF6),
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.NOT_STARTED,
                dueText = "12/12/2026"
            ),
            TaskItem(
                id = "3",
                title = "Inbox cleanup",
                categoryName = "Personal",
                categoryColor = Color(0xFF10B981),
                priority = TaskPriority.LOW,
                status = TaskStatus.COMPLETED,
                dueText = "05/12/2026"
            )
        )

        val snapshot = LocalAnalyticsEngine.summarize(tasks)

        assertEquals(1, snapshot.overdueCount)
        assertTrue(snapshot.recommendations.isNotEmpty())
        assertTrue(snapshot.recommendedAction.contains("quick wins") || snapshot.recommendedAction.contains("priority"))
    }

    @Test
    fun summarize_returns_empty_state_when_no_tasks_exist() {
        val snapshot = LocalAnalyticsEngine.summarize(emptyList())

        assertEquals(0, snapshot.totalTasks)
        assertEquals(0, snapshot.completedCount)
        assertEquals(0, snapshot.completionRate)
        assertEquals("No task data yet", snapshot.mostActiveCategory)
        assertEquals(0, snapshot.productivityStreak)
        assertEquals(7, snapshot.weeklyTrend.size)
        assertEquals("No task data yet", snapshot.weeklyInsight)
        assertEquals(0, snapshot.backlogPressure)
    }
}

package com.example.zentask.analytics

import androidx.compose.ui.graphics.Color
import com.example.zentask.model.TaskItem
import com.example.zentask.model.TaskPriority
import com.example.zentask.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class LocalAnalyticsEngineTest {

    private val zone = ZoneId.of("UTC")
    private val today = LocalDate.of(2026, 11, 16)
    private val dueFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)

    private fun completedAtDaysAgo(daysAgo: Long): Long =
        today.minusDays(daysAgo).atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

    private fun due(offsetDays: Long): String = today.plusDays(offsetDays).format(dueFormat)

    private fun task(
        id: String,
        category: String,
        status: TaskStatus,
        priority: TaskPriority = TaskPriority.MEDIUM,
        dueText: String = "No deadline",
        completedAt: Long? = null
    ) = TaskItem(
        id = id,
        title = "Task $id",
        categoryName = category,
        categoryColor = Color(0xFF8B5CF6),
        priority = priority,
        status = status,
        dueText = dueText,
        completedAt = completedAt
    )

    private fun summarize(tasks: List<TaskItem>) =
        LocalAnalyticsEngine.summarize(tasks, today, zone)

    @Test
    fun summarize_tracks_completion_rate_category_and_streak() {
        val snapshot = summarize(
            listOf(
                task("1", "Design", TaskStatus.COMPLETED, completedAt = completedAtDaysAgo(0)),
                task("2", "Design", TaskStatus.COMPLETED, completedAt = completedAtDaysAgo(1)),
                task("3", "Personal", TaskStatus.IN_PROGRESS)
            )
        )

        assertEquals(3, snapshot.totalTasks)
        assertEquals(2, snapshot.completedCount)
        assertEquals(67, snapshot.completionRate)
        assertEquals("Design", snapshot.mostActiveCategory)
        assertEquals(2, snapshot.productivityStreak)
        assertEquals(1, snapshot.backlogPressure)
        assertEquals(listOf(0, 0, 0, 0, 0, 1, 1), snapshot.dailyTrend)
        assertEquals(2, snapshot.weeklyTrend.last())
        assertEquals("This week is trending upward.", snapshot.weeklyInsight)
    }

    @Test
    fun trends_use_completion_date_not_due_date() {
        val snapshot = summarize(
            listOf(
                // selesai 3 hari lalu, tenggat bulan depan
                task("1", "Work", TaskStatus.COMPLETED, dueText = due(30), completedAt = completedAtDaysAgo(3)),
                // tanpa tenggat, selesai hari ini
                task("2", "Work", TaskStatus.COMPLETED, completedAt = completedAtDaysAgo(0))
            )
        )

        assertEquals(1, snapshot.dailyTrend[3])
        assertEquals(1, snapshot.dailyTrend[6])
        assertEquals(2, snapshot.dailyTrend.sum())
    }

    @Test
    fun trends_do_not_invent_data_when_completion_time_is_missing() {
        val snapshot = summarize(
            listOf(task("1", "Work", TaskStatus.COMPLETED, completedAt = null))
        )

        assertEquals(listOf(0, 0, 0, 0, 0, 0, 0), snapshot.dailyTrend)
        assertEquals(listOf(0, 0, 0, 0, 0, 0, 0), snapshot.weeklyTrend)
        assertEquals(0, snapshot.productivityStreak)
    }

    @Test
    fun weekly_trend_groups_completions_by_week() {
        val snapshot = summarize(
            listOf(
                task("1", "Work", TaskStatus.COMPLETED, completedAt = completedAtDaysAgo(2)),
                task("2", "Work", TaskStatus.COMPLETED, completedAt = completedAtDaysAgo(9)),
                task("3", "Work", TaskStatus.COMPLETED, completedAt = completedAtDaysAgo(10)),
                task("4", "Work", TaskStatus.COMPLETED, completedAt = completedAtDaysAgo(60))
            )
        )

        assertEquals(1, snapshot.weeklyTrend[6]) // 0 sampai 6 hari lalu
        assertEquals(2, snapshot.weeklyTrend[5]) // 7 sampai 13 hari lalu
        assertEquals(3, snapshot.weeklyTrend.sum()) // 60 hari lalu di luar jendela
        assertEquals("Output dipped compared to last week.", snapshot.weeklyInsight)
    }

    @Test
    fun streak_breaks_when_a_day_is_skipped() {
        val broken = summarize(
            listOf(
                task("1", "Work", TaskStatus.COMPLETED, completedAt = completedAtDaysAgo(0)),
                task("2", "Work", TaskStatus.COMPLETED, completedAt = completedAtDaysAgo(2))
            )
        )
        val idle = summarize(
            listOf(task("3", "Work", TaskStatus.COMPLETED, completedAt = completedAtDaysAgo(3)))
        )

        assertEquals(1, broken.productivityStreak)
        assertEquals(0, idle.productivityStreak)
    }

    @Test
    fun summarize_tracks_overdue_and_recommendation_steps() {
        val snapshot = summarize(
            listOf(
                task("1", "Work", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, dueText = due(-5)),
                task("2", "Work", TaskStatus.NOT_STARTED, TaskPriority.MEDIUM, dueText = due(30)),
                task("3", "Personal", TaskStatus.COMPLETED, TaskPriority.LOW, completedAt = completedAtDaysAgo(1))
            )
        )

        assertEquals(1, snapshot.overdueCount)
        assertEquals(2, snapshot.backlogPressure)
        assertTrue(snapshot.recommendations.isNotEmpty())
        assertTrue(snapshot.recommendedAction.contains("overdue"))
    }

    @Test
    fun summarize_returns_empty_state_when_no_tasks_exist() {
        assertEquals(LocalAnalyticsSnapshot(), summarize(emptyList()))
    }
}
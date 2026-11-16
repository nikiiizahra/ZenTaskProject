package com.example.zentask.analytics

import com.example.zentask.model.TaskItem
import com.example.zentask.model.TaskStatus
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.roundToInt

data class LocalAnalyticsSnapshot(
    val totalTasks: Int = 0,
    val completedCount: Int = 0,
    val completionRate: Int = 0,
    val mostActiveCategory: String = "No task data yet",
    val focusScore: Int = 0,
    val dailyTrend: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0),
    val weeklyTrend: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0),
    val productivityStreak: Int = 0,
    val weeklyInsight: String = "No task data yet",
    val dailyInsight: String = "No task data yet",
    val backlogPressure: Int = 0,
    val overdueCount: Int = 0,
    val atRiskCount: Int = 0,
    val recommendations: List<String> = listOf("Create your first task to start tracking momentum."),
    val recommendedAction: String = "Create your first task to start tracking momentum."
)

object LocalAnalyticsEngine {

    fun summarize(tasks: List<TaskItem>,
                  today: LocalDate = LocalDate.now(ZoneId.systemDefault()),
                  zone: ZoneId = ZoneId.systemDefault()): LocalAnalyticsSnapshot {
        if (tasks.isEmpty()) {
            return LocalAnalyticsSnapshot()
        }

        val totalTasks = tasks.size
        val completedCount = tasks.count { it.status == TaskStatus.COMPLETED }
        val inProgressCount = tasks.count { it.status == TaskStatus.IN_PROGRESS }
        val notStartedCount = tasks.count { it.status == TaskStatus.NOT_STARTED }

        val completionRate = if (totalTasks > 0) {
            ((completedCount.toDouble() / totalTasks.toDouble()) * 100).roundToInt()
        } else {
            0
        }

        val categoryCounts = tasks
            .groupBy { it.categoryName }
            .mapValues { (_, values) -> values.size }

        val mostActiveCategory = categoryCounts
            .maxByOrNull { it.value }
            ?.key
            ?: "No task data yet"

        val activityRatio = if (totalTasks > 0) {
            ((completedCount + inProgressCount).toDouble() / totalTasks.toDouble()) * 100
        } else {
            0.0
        }

        val focusScore = ((completionRate * 0.7) + (activityRatio * 0.3)).roundToInt().coerceIn(0, 100)
        val dailyTrend = buildTrend(tasks, 7)
        val weeklyTrend = buildTrend(tasks, 7)
        val productivityStreak = completedCount.coerceAtMost(7)
        val backlogPressure = maxOf(inProgressCount, notStartedCount)
        val overdueCount = tasks.count { it.status != TaskStatus.COMPLETED && isOverdue(it.dueText) }
        val atRiskCount = tasks.count { task ->
            task.status != TaskStatus.COMPLETED && (
                task.priority == com.example.zentask.model.TaskPriority.HIGH ||
                    isOverdue(task.dueText) ||
                    (task.remainingHours != null && task.remainingHours <= 2)
                )
        }

        val weeklyInsight = when {
            completionRate >= 60 -> "This week is trending upward."
            completionRate >= 45 -> "Momentum is steady this week."
            inProgressCount > 0 -> "The workload is still building."
            else -> "This week needs a quick reset."
        }

        val dailyInsight = when {
            completedCount > 0 && inProgressCount > 0 -> "$completedCount tasks closed this week with $inProgressCount still active."
            completedCount > 0 -> "$completedCount tasks are already completed this week."
            inProgressCount > 0 -> "$inProgressCount task(s) are still active and need closure."
            else -> "No finished work yet. Start with one quick win."
        }

        val recommendations = buildList {
            if (overdueCount > 0) {
                add("${overdueCount} task(s) are overdue. Clear the oldest ones first to reduce delivery risk.")
            }
            if (backlogPressure >= 3) {
                add("Your queue is piling up. Finish the next 2 quick wins before opening anything new.")
            }
            if (completionRate >= 60) {
                add("Momentum is strong. Keep the pace by completing one high-impact task from your current list.")
            } else {
                add("Start with the smallest task and finish it before adding more work.")
            }
            if (mostActiveCategory != "No task data yet") {
                add("Your busiest category is ${mostActiveCategory}. Protect that flow by closing one key item before switching context.")
            }
        }.ifEmpty { listOf("Create your next task and keep the progress flow moving.") }

        val recommendedAction = when {
            overdueCount > 0 -> "You have ${overdueCount} overdue task(s). Finish the highest priority item first to reduce backlog pressure."
            backlogPressure >= 3 -> "Your queue is piling up. Clear the next 2 quick wins before starting anything new."
            completionRate >= 80 -> "Momentum is strong. Keep the streak by finishing one more key task."
            completionRate >= 60 -> "You are ahead of schedule. Close one priority task to keep the rhythm."
            inProgressCount > 0 -> "${inProgressCount} task(s) are active. Finish the next milestone to increase focus."
            notStartedCount > 0 -> "Start with your simplest task to build momentum and raise your completion rate."
            else -> "Create your next task and keep the progress flow moving."
        }

        return LocalAnalyticsSnapshot(
            totalTasks = totalTasks,
            completedCount = completedCount,
            completionRate = completionRate,
            mostActiveCategory = mostActiveCategory,
            focusScore = focusScore,
            dailyTrend = dailyTrend,
            weeklyTrend = weeklyTrend,
            productivityStreak = productivityStreak,
            weeklyInsight = weeklyInsight,
            dailyInsight = dailyInsight,
            backlogPressure = backlogPressure,
            overdueCount = overdueCount,
            atRiskCount = atRiskCount,
            recommendations = recommendations,
            recommendedAction = recommendedAction
        )
    }

    private fun isOverdue(dueText: String): Boolean {
        val date = parseDueDate(dueText) ?: return false
        return date.isBefore(LocalDate.now())
    }

    private fun buildTrend(tasks: List<TaskItem>, days: Int): List<Int> {
        val completedTasks = tasks.filter { it.status == TaskStatus.COMPLETED }
        val trend = MutableList(days) { 0 }
        val today = LocalDate.now()

        completedTasks.forEach { task ->
            val parsedDate = parseDueDate(task.dueText) ?: return@forEach
            val dayOffset = java.time.temporal.ChronoUnit.DAYS.between(parsedDate, today).toInt()
            if (dayOffset in 0 until days) {
                trend[days - 1 - dayOffset] += 1
            }
        }

        if (trend.sum() == 0 && completedTasks.isNotEmpty()) {
            completedTasks.take(days).forEachIndexed { index, _ ->
                trend[index % days] += 1
            }
        }

        return trend
    }

    private fun parseDueDate(value: String): LocalDate? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return null

        val patterns = listOf(
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("M/d/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )

        for (formatter in patterns) {
            try {
                return LocalDate.parse(trimmed, formatter)
            } catch (_: DateTimeParseException) {
            }
        }

        return null
    }
}

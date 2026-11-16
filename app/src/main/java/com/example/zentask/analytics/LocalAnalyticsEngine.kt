package com.example.zentask.analytics

import com.example.zentask.model.TaskItem
import com.example.zentask.model.TaskPriority
import com.example.zentask.model.TaskStatus
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt

data class LocalAnalyticsSnapshot(
    val totalTasks: Int = 0,
    val completedCount: Int = 0,
    val completionRate: Int = 0,
    val mostActiveCategory: String = "No task data yet",
    val focusScore: Int = 0,
    // 7 hari terakhir, indeks 6 = hari ini
    val dailyTrend: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0),
    // 7 minggu terakhir (tiap batang = 7 hari), indeks 6 = 7 hari terakhir
    val weeklyTrend: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0),
    val productivityStreak: Int = 0,
    val streakAtRisk: Boolean = false,
    val weeklyInsight: String = "No task data yet",
    val dailyInsight: String = "No task data yet",
    val backlogPressure: Int = 0,
    val overdueCount: Int = 0,
    val atRiskCount: Int = 0,
    val recommendations: List<String> = listOf("Create your first task to start tracking momentum."),
    val recommendedAction: String = "Create your first task to start tracking momentum."
)

data class StreakStatus(val days: Int, val atRisk: Boolean)

object LocalAnalyticsEngine {

    private const val TREND_SIZE = 7

    // Sama dengan format yang dipakai ZenTaskViewModel.
    private val DUE_FORMATTERS = listOf(
        DateTimeFormatter.ofPattern("d/M/uuuu", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("d-M-uuuu", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.ENGLISH)
    )

    fun summarize(
        tasks: List<TaskItem>,
        today: LocalDate = LocalDate.now(),
        zone: ZoneId = ZoneId.systemDefault()
    ): LocalAnalyticsSnapshot {
        if (tasks.isEmpty()) {
            return LocalAnalyticsSnapshot()
        }

        val totalTasks = tasks.size
        val completedCount = tasks.count { it.status == TaskStatus.COMPLETED }
        val inProgressCount = tasks.count { it.status == TaskStatus.IN_PROGRESS }
        val notStartedCount = tasks.count { it.status == TaskStatus.NOT_STARTED }
        val activeCount = inProgressCount + notStartedCount

        val completionRate =
            ((completedCount.toDouble() / totalTasks.toDouble()) * 100).roundToInt()

        val mostActiveCategory = tasks
            .groupBy { it.categoryName }
            .mapValues { (_, values) -> values.size }
            .maxByOrNull { it.value }
            ?.key
            ?: "No task data yet"

        val activityRatio =
            ((completedCount + inProgressCount).toDouble() / totalTasks.toDouble()) * 100
        val focusScore =
            ((completionRate * 0.7) + (activityRatio * 0.3)).roundToInt().coerceIn(0, 100)

        // Tanggal penyelesaian sebenarnya. Task tanpa completedAt tidak dihitung
        // (lebih baik kosong daripada angka karangan).
        val completionDates: List<LocalDate> = tasks
            .filter { task -> task.status == TaskStatus.COMPLETED }
            .mapNotNull { task -> task.completedAt }
            .map { millis -> Instant.ofEpochMilli(millis).atZone(zone).toLocalDate() }

        val dailyTrend = buildDailyTrend(completionDates, today)
        val weeklyTrend = buildWeeklyTrend(completionDates, today)
        val productivityStreak = buildStreak(completionDates, today)
        val streakAtRisk = productivityStreak > 0 &&
                completionDates.none { date -> date == today }

        val backlogPressure = activeCount
        val overdueCount = tasks.count {
            it.status != TaskStatus.COMPLETED && isOverdue(it.dueText, today)
        }
        val atRiskCount = tasks.count { task ->
            task.status != TaskStatus.COMPLETED && (
                    task.priority == TaskPriority.HIGH ||
                            isOverdue(task.dueText, today) ||
                            (task.remainingHours?.let { hours -> hours <= 2 } == true)
                    )
        }

        val completedToday = dailyTrend.last()
        val completedThisWeek = weeklyTrend.last()
        val completedPrevWeek = weeklyTrend[TREND_SIZE - 2]

        val weeklyInsight = when {
            completedThisWeek > completedPrevWeek -> "This week is trending upward."
            completedThisWeek == completedPrevWeek && completedThisWeek > 0 ->
                "Momentum is steady this week."
            completedThisWeek < completedPrevWeek -> "Output dipped compared to last week."
            activeCount > 0 -> "The workload is still building."
            else -> "This week needs a quick reset."
        }

        val dailyInsight = when {
            completedToday > 0 && activeCount > 0 ->
                "$completedToday task(s) closed today, $activeCount still active."
            completedToday > 0 -> "$completedToday task(s) closed today. Nice work."
            completedThisWeek > 0 ->
                "Nothing closed today yet. $completedThisWeek task(s) were finished in the last 7 days."
            activeCount > 0 -> "$activeCount task(s) are still active and need closure."
            else -> "No finished work yet. Start with one quick win."
        }

        val recommendations = buildList {
            if (overdueCount > 0) {
                add("$overdueCount task(s) are overdue. Clear the oldest ones first to reduce delivery risk.")
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
                add("Your busiest category is $mostActiveCategory. Protect that flow by closing one key item before switching context.")
            }
        }.ifEmpty { listOf("Create your next task and keep the progress flow moving.") }

        val recommendedAction = when {
            overdueCount > 0 ->
                "You have $overdueCount overdue task(s). Finish the highest priority item first to reduce backlog pressure."
            backlogPressure >= 3 ->
                "Your queue is piling up. Clear the next 2 quick wins before starting anything new."
            completionRate >= 80 -> "Momentum is strong. Keep the streak by finishing one more key task."
            completionRate >= 60 -> "You are ahead of schedule. Close one priority task to keep the rhythm."
            inProgressCount > 0 ->
                "$inProgressCount task(s) are active. Finish the next milestone to increase focus."
            notStartedCount > 0 ->
                "Start with your simplest task to build momentum and raise your completion rate."
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
            streakAtRisk = streakAtRisk,
            weeklyInsight = weeklyInsight,
            dailyInsight = dailyInsight,
            backlogPressure = backlogPressure,
            overdueCount = overdueCount,
            atRiskCount = atRiskCount,
            recommendations = recommendations,
            recommendedAction = recommendedAction
        )
    }

    /**
     * Versi ringan untuk pekerjaan latar belakang (misalnya notifikasi),
     * yang hanya punya daftar waktu selesai, tanpa ViewModel.
     */
    fun streakStatus(
        completedAtMillis: List<Long>,
        today: LocalDate = LocalDate.now(),
        zone: ZoneId = ZoneId.systemDefault()
    ): StreakStatus {
        val dates = completedAtMillis.map { millis ->
            Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
        }
        val days = buildStreak(dates, today)
        return StreakStatus(days = days, atRisk = days > 0 && today !in dates.toSet())
    }

    private fun buildDailyTrend(dates: List<LocalDate>, today: LocalDate): List<Int> {
        val trend = MutableList(TREND_SIZE) { 0 }
        dates.forEach { date ->
            val offset = ChronoUnit.DAYS.between(date, today)
            if (offset >= 0 && offset < TREND_SIZE) {
                trend[TREND_SIZE - 1 - offset.toInt()] += 1
            }
        }
        return trend
    }

    private fun buildWeeklyTrend(dates: List<LocalDate>, today: LocalDate): List<Int> {
        val trend = MutableList(TREND_SIZE) { 0 }
        dates.forEach { date ->
            val offset = ChronoUnit.DAYS.between(date, today)
            if (offset >= 0 && offset < TREND_SIZE * 7L) {
                val weekIndex = (offset / 7).toInt()
                trend[TREND_SIZE - 1 - weekIndex] += 1
            }
        }
        return trend
    }

    /** Jumlah hari berturut-turut (sampai hari ini atau kemarin) yang punya minimal satu task selesai. */
    private fun buildStreak(dates: List<LocalDate>, today: LocalDate): Int {
        val days = dates.toSet()
        var cursor = when {
            today in days -> today
            today.minusDays(1) in days -> today.minusDays(1)
            else -> return 0
        }
        var count = 0
        while (cursor in days) {
            count++
            cursor = cursor.minusDays(1)
        }
        return count
    }

    private fun isOverdue(dueText: String, today: LocalDate): Boolean {
        val date = parseDueDate(dueText) ?: return false
        return date.isBefore(today)
    }

    private fun parseDueDate(value: String): LocalDate? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return null

        for (formatter in DUE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter)
            } catch (_: DateTimeParseException) {
                // coba format berikutnya
            }
        }
        return null
    }
}
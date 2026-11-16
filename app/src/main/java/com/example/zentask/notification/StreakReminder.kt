package com.example.zentask.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.zentask.MainActivity
import com.example.zentask.analytics.LocalAnalyticsEngine
import com.example.zentask.data.local.ZenTaskDatabase
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object StreakReminder {

    const val CHANNEL_ID = "streak_reminders"
    const val WORK_NAME = "streak_reminder"
    const val KEY_OWNER_ID = "owner_id"
    private const val NOTIFICATION_ID = 4101

    // Jam pengingat (format 24 jam). Ubah sementara untuk menguji.
    const val REMINDER_HOUR = 20
    const val REMINDER_MINUTE = 0

    /** Jadwalkan kalau pengguna login dan push menyala, batalkan kalau tidak. */
    fun sync(context: Context, ownerId: String?, enabled: Boolean) {
        if (ownerId == null || !enabled) {
            cancel(context)
        } else {
            schedule(context, ownerId)
        }
    }

    fun schedule(context: Context, ownerId: String) {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(REMINDER_HOUR, REMINDER_MINUTE)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val delay = Duration.between(now, next)

        val request = PeriodicWorkRequestBuilder<StreakReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(KEY_OWNER_ID to ownerId))
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork(WORK_NAME)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Streak reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Reminds you before your daily streak ends"
                }
            )
        }
    }

    // Izin sudah dicek lewat areNotificationsEnabled(), yang juga mencakup
    // POST_NOTIFICATIONS di Android 13 ke atas.
    @SuppressLint("MissingPermission")
    fun show(context: Context, streakDays: Int) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        ensureChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            // Ganti nanti lewat New > Image Asset > Notification Icons.
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Your $streakDays-day streak ends tonight")
            .setContentText("Finish one task before midnight to keep it going.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Finish one task before midnight to keep your $streakDays-day streak going.")
            )
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }
}

class StreakReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val ownerId = inputData.getString(StreakReminder.KEY_OWNER_ID)
            ?: return Result.success()

        // Preferensi per akun, sama dengan yang ditulis DemoAuthStore.
        val prefs = applicationContext.getSharedPreferences(
            "zentask_user_settings", Context.MODE_PRIVATE
        )
        if (!prefs.getBoolean("push_enabled_$ownerId", true)) return Result.success()

        // Kalau worker terlambat sampai lewat tengah malam, jangan kirim.
        val reminderTime = LocalTime.of(
            StreakReminder.REMINDER_HOUR, StreakReminder.REMINDER_MINUTE
        )
        if (LocalTime.now().isBefore(reminderTime.minusMinutes(30))) return Result.success()

        return try {
            val tasks = ZenTaskDatabase.getDatabase(applicationContext)
                .taskDao()
                .getTasksByOwner(ownerId)

            val completedAt = tasks
                .filter { it.status == "COMPLETED" }
                .mapNotNull { it.completedAt }

            val streak = LocalAnalyticsEngine.streakStatus(completedAt)
            if (streak.atRisk) {
                StreakReminder.show(applicationContext, streak.days)
            }
            Result.success()
        } catch (e: Exception) {
            Result.success() // coba lagi besok, jangan memicu retry berulang
        }
    }
}
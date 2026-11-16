package com.example.zentask.viewmodel

import android.content.Context
import android.content.SharedPreferences

import com.example.zentask.data.local.AuthRepository
import com.example.zentask.data.local.SessionManager
import com.example.zentask.data.local.ZenTaskDatabase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.zentask.notification.StreakReminder

// =====================================================
// USER MODEL FOR THE UI
// =====================================================

data class DemoUser(
    val fullName: String,
    val email: String,
    val pushEnabled: Boolean = true,
    val emailNotifEnabled: Boolean = false
) {

    val initials: String
        get() {

            val parts = fullName.trim()
                .split(Regex("\\s+"))
                .filter {
                    it.isNotBlank()
                }

            return when {

                parts.isEmpty() -> "?"

                parts.size == 1 -> {
                    parts.first()
                        .take(2)
                        .uppercase()
                }

                else -> {
                    (
                            parts.first().take(1) +
                                    parts.last().take(1)
                            ).uppercase()
                }
            }
        }
}

// =====================================================
// AUTHENTICATION STORE
// =====================================================

/**
 * ZenTask local authentication.
 *
 * User accounts are stored in Room Database.
 *
 * The currently authenticated user is only
 * active for the running app process.
 *
 * Opening ZenTask in a new process requires
 * the user to enter email and password again.
 *
 * Logging out does not remove accounts,
 * categories, or tasks from Room.
 */
object DemoAuthStore {

    // =================================================
    // ROOM DEPENDENCIES
    // =================================================

    private var authRepository: AuthRepository? = null

    private var sessionManager: SessionManager? = null

    private var settingsPreferences: SharedPreferences? = null

    private var appContextRef: Context? = null

    // =================================================
    // CURRENT LOGIN SESSION
    // =================================================

    /**
     * This value is only held in memory.
     *
     * After the app process ends, it becomes null.
     */
    private var activeUserId: String? = null

    private val _currentUser =
        MutableStateFlow<DemoUser?>(null)

    val currentUser: StateFlow<DemoUser?> =
        _currentUser.asStateFlow()

    // =================================================
    // INITIALIZATION
    // =================================================

    /**
     * Initializes Room authentication.
     *
     * A previous saved session is not restored.
     * The user must log in again when ZenTask
     * starts in a new app process.
     */
    fun initialize(context: Context) {

        if (
            authRepository != null &&
            sessionManager != null
        ) {
            return
        }

        val appContext =
            context.applicationContext

        appContextRef = appContext

        settingsPreferences =
            appContext.getSharedPreferences(
                "zentask_user_settings",
                Context.MODE_PRIVATE
            )

        val database =
            ZenTaskDatabase.getDatabase(
                appContext
            )

        authRepository =
            AuthRepository(database)

        sessionManager =
            SessionManager(appContext)

        // Do not automatically authenticate
        // the account from a previous app process.
        sessionManager?.clearSession()

        activeUserId = null
        _currentUser.value = null
    }

    // =================================================
    // INITIALIZATION STATUS
    // =================================================

    fun isLocalAuthReady(): Boolean {

        return authRepository != null &&
                sessionManager != null
    }

    // =================================================
    // REGISTER WITH ROOM
    // =================================================

    /**
     * Creates a persistent Room account
     * and signs in the newly registered user.
     */
    suspend fun registerLocal(
        fullName: String,
        email: String,
        password: String,
        weeklySummary: Boolean
    ): AuthRepository.AuthResult {

        val repository =
            requireRepository()

        val result =
            repository.register(
                name = fullName,
                email = email,
                password = password
            )

        if (
            result is
                    AuthRepository.AuthResult.Success
        ) {

            setActiveUser(
                userId = result.user.id,
                fullName = result.user.name,
                email = result.user.email,
                emailNotifications = weeklySummary
            )
        }

        return result
    }

    // =================================================
    // LOGIN WITH ROOM
    // =================================================

    /**
     * Validates the user's email and password
     * against the account stored in Room.
     */
    suspend fun loginLocal(
        email: String,
        password: String
    ): AuthRepository.AuthResult {

        val repository =
            requireRepository()

        val result =
            repository.login(
                email = email,
                password = password
            )

        if (
            result is
                    AuthRepository.AuthResult.Success
        ) {

            setActiveUser(
                userId = result.user.id,
                fullName = result.user.name,
                email = result.user.email,
                emailNotifications = false
            )
        }

        return result
    }

    // =================================================
    // ACTIVATE CURRENT USER
    // =================================================

    /**
     * Keep the in-memory user ID synchronized
     * with the user profile exposed to screens.
     */
    private fun setActiveUser(
        userId: String,
        fullName: String,
        email: String,
        emailNotifications: Boolean
    ) {

        val manager =
            requireSessionManager()

        // Required for compatibility with
        // the existing local session storage.
        // This ID is cleared at the next
        // fresh application initialization.
        manager.saveSession(userId)

        activeUserId = userId

        val savedPushNotification =
            settingsPreferences?.getBoolean(
                "push_enabled_$userId",
                true
            ) ?: true

        val savedEmailNotification =
            settingsPreferences?.getBoolean(
                "email_notif_enabled_$userId",
                emailNotifications
            ) ?: emailNotifications

        _currentUser.value =
            DemoUser(
                fullName = fullName,
                email = email,
                pushEnabled = savedPushNotification,
                emailNotifEnabled = savedEmailNotification
            )

        appContextRef?.let {
            StreakReminder.sync(it, userId, savedPushNotification)
        }
    }

    // =================================================
    // CURRENT ROOM USER ID
    // =================================================

    /**
     * Used by ZenTaskViewModel to retrieve
     * tasks and categories for the correct owner.
     *
     * Do not read a user ID from an old saved
     * session to bypass the Login screen.
     */
    fun getCurrentLocalUserId(): String? {

        return if (
            _currentUser.value != null
        ) {
            activeUserId
        } else {
            null
        }
    }

    // =================================================
    // PUSH NOTIFICATION PREFERENCE
    // =================================================

    /**
     * Preference is updated for the current
     * UI session only.
     *
     * Persistent notification settings
     * can be implemented later.
     */
    fun updatePushNotifications(
        enabled: Boolean
    ) {

        val user =
            _currentUser.value ?: return

        val userId =
            activeUserId ?: return

        settingsPreferences?.edit()
            ?.putBoolean(
                "push_enabled_$userId",
                enabled
            )
            ?.apply()

        _currentUser.value =
            user.copy(
                pushEnabled = enabled
            )

        appContextRef?.let {
            StreakReminder.sync(it, userId, enabled)
        }
    }

    // =================================================
    // EMAIL NOTIFICATION PREFERENCE
    // =================================================

    fun updateEmailNotifications(
        enabled: Boolean
    ) {

        val user =
            _currentUser.value ?: return

        val userId =
            activeUserId ?: return

        settingsPreferences?.edit()
            ?.putBoolean(
                "email_notif_enabled_$userId",
                enabled
            )
            ?.apply()

        _currentUser.value =
            user.copy(
                emailNotifEnabled = enabled
            )
    }

    // =================================================
    // LOGOUT
    // =================================================

    /**
     * Clears only the active login session.
     *
     * All Room accounts, tasks, and categories
     * remain stored on the device.
     */
    fun logout() {
        appContextRef?.let { StreakReminder.cancel(it) }

        // Remove the active identity first.
        activeUserId = null

        // Notify screens and ViewModels
        // that no account is signed in.
        _currentUser.value = null

        // Clear the saved local user ID.
        sessionManager?.clearSession()
    }

    // =================================================
    // PRIVATE HELPERS
    // =================================================

    private fun requireRepository():
            AuthRepository {

        return checkNotNull(
            authRepository
        ) {
            "Call DemoAuthStore.initialize(context) " +
                    "before using Room authentication."
        }
    }

    private fun requireSessionManager():
            SessionManager {

        return checkNotNull(
            sessionManager
        ) {
            "Call DemoAuthStore.initialize(context) " +
                    "before using session operations."
        }
    }
}

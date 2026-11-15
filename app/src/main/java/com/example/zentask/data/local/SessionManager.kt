package com.example.zentask.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stores the active local account session.
 *
 * This class saves only the currently signed-in
 * user's ID, not their password or password hash.
 *
 * Tasks and categories remain in Room Database
 * when a user signs out.
 *
 * NOTE:
 * This is a local demo session manager, not a
 * secure server-backed authentication system.
 */
class SessionManager(
    context: Context
) {

    companion object {

        private const val PREFS_NAME =
            "zentask_session"

        private const val KEY_USER_ID =
            "current_user_id"
    }

    // Use application context to avoid leaking
    // an Activity or Composable.
    private val preferences: SharedPreferences =
        context.applicationContext
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

    // Protect updates to SharedPreferences and
    // StateFlow from concurrent calls.
    private val lock = Any()

    // Restore the last session when the application
    // starts again.
    private val _currentUserId =
        MutableStateFlow(
            preferences.getString(
                KEY_USER_ID,
                null
            )?.takeIf {
                it.isNotBlank()
            }
        )

    /**
     * Observable active user ID.
     *
     * Screens and ViewModels can collect this Flow
     * to react when the logged-in account changes.
     */
    val currentUserId: StateFlow<String?> =
        _currentUserId.asStateFlow()

    /**
     * Returns the currently saved user ID,
     * or null if nobody is signed in.
     */
    fun getCurrentUserId(): String? {
        return _currentUserId.value
    }

    /**
     * Saves a successful local login session.
     *
     * Call this ONLY after AuthRepository.login()
     * or AuthRepository.register() succeeds.
     */
    fun saveSession(
        userId: String
    ) {
        require(userId.isNotBlank()) {
            "User ID cannot be empty."
        }

        synchronized(lock) {

            val saved = preferences.edit()
                .putString(
                    KEY_USER_ID,
                    userId
                )
                .commit()

            if (!saved) {
                throw IllegalStateException(
                    "Unable to save login session."
                )
            }

            _currentUserId.value = userId
        }
    }

    /**
     * Clears the current login session.
     *
     * IMPORTANT:
     * This does NOT delete the user's account,
     * tasks, categories, or database records.
     */
    fun clearSession() {

        synchronized(lock) {

            val cleared = preferences.edit()
                .remove(KEY_USER_ID)
                .commit()

            if (!cleared) {
                throw IllegalStateException(
                    "Unable to clear login session."
                )
            }

            _currentUserId.value = null
        }
    }

    /**
     * Checks whether the app currently has
     * a saved local user session.
     *
     * A saved ID is not proof of authentication.
     * The app should also verify that this user
     * still exists in Room Database.
     */
    fun hasActiveSession(): Boolean {
        return !getCurrentUserId().isNullOrBlank()
    }
}

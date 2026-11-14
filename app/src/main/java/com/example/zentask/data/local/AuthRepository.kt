package com.example.zentask.data.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

/**
 * Authentication repository for local ZenTask accounts.
 *
 * Responsibilities:
 * - Register new users
 * - Hash passwords before saving
 * - Verify passwords during login
 * - Prevent duplicate email registration
 * - Create default categories for new users
 *
 * This repository uses local Room storage.
 * It does not provide online authentication.
 */
class AuthRepository(
    private val database: ZenTaskDatabase
) {

    private val userDao = database.userDao()
    private val categoryDao = database.categoryDao()

    // =================================================
    // PUBLIC USER PROFILE
    // =================================================

    /**
     * Safe user information for the UI.
     *
     * Password hashes and salts are intentionally
     * excluded from this data class.
     */
    data class AuthUser(
        val id: String,
        val name: String,
        val email: String
    )

    // =================================================
    // AUTHENTICATION RESULTS
    // =================================================

    sealed interface AuthResult {

        data class Success(
            val user: AuthUser
        ) : AuthResult

        data class Error(
            val message: String
        ) : AuthResult
    }

    // =================================================
    // REGISTER
    // =================================================

    /**
     * Creates a new local user account.
     *
     * Each account receives a unique ID and
     * its own set of default categories.
     */
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {

        val cleanName = name.trim()
        val cleanEmail = normalizeEmail(email)

        // ---------------------------------------------
        // VALIDATION
        // ---------------------------------------------

        if (cleanName.length < 2) {
            return@withContext AuthResult.Error(
                "Name must contain at least 2 characters."
            )
        }

        if (!isValidEmail(cleanEmail)) {
            return@withContext AuthResult.Error(
                "Please enter a valid email address."
            )
        }

        if (password.length < 8) {
            return@withContext AuthResult.Error(
                "Password must contain at least 8 characters."
            )
        }

        // ---------------------------------------------
        // CHECK DUPLICATE EMAIL
        // ---------------------------------------------

        if (userDao.emailExists(cleanEmail)) {
            return@withContext AuthResult.Error(
                "This email is already registered."
            )
        }

        // ---------------------------------------------
        // HASH PASSWORD
        // ---------------------------------------------

        // PBKDF2 is intentionally performed outside
        // the database transaction and off the UI thread.
        val passwordData = withContext(
            Dispatchers.Default
        ) {
            PasswordHasher.hashPassword(password)
        }

        // ---------------------------------------------
        // CREATE USER
        // ---------------------------------------------

        val userId = UUID.randomUUID().toString()

        val user = UserEntity(
            id = userId,
            name = cleanName,
            email = cleanEmail,
            passwordHash = passwordData.hash,
            passwordSalt = passwordData.salt,
            createdAt = System.currentTimeMillis()
        )

        // ---------------------------------------------
        // SAVE USER AND DEFAULT CATEGORIES
        // ---------------------------------------------

        try {

            database.withTransaction {

                userDao.insertUser(user)

                categoryDao.insertCategories(
                    createDefaultCategories(userId)
                )
            }

            AuthResult.Success(
                user = AuthUser(
                    id = user.id,
                    name = user.name,
                    email = user.email
                )
            )

        } catch (exception: Exception) {

            if (isEmailConstraintError(exception)) {

                AuthResult.Error(
                    "This email is already registered."
                )

            } else {

                AuthResult.Error(
                    "Unable to create account. Please try again."
                )
            }
        }
    }

    // =================================================
    // LOGIN
    // =================================================

    /**
     * Authenticates an existing local user account.
     *
     * The password is verified against the
     * salted hash stored in Room.
     */
    suspend fun login(
        email: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {

        val cleanEmail = normalizeEmail(email)

        if (
            cleanEmail.isBlank() ||
            password.isBlank()
        ) {

            return@withContext AuthResult.Error(
                "Please enter your email and password."
            )
        }

        val user = userDao.getUserByEmail(
            cleanEmail
        )

        if (user == null) {

            return@withContext AuthResult.Error(
                "Incorrect email or password."
            )
        }

        // ---------------------------------------------
        // VERIFY PASSWORD
        // ---------------------------------------------

        val passwordMatches = withContext(
            Dispatchers.Default
        ) {

            PasswordHasher.verifyPassword(
                password = password,
                storedHash = user.passwordHash,
                storedSalt = user.passwordSalt
            )
        }

        if (!passwordMatches) {

            return@withContext AuthResult.Error(
                "Incorrect email or password."
            )
        }

        // ---------------------------------------------
        // LOGIN SUCCESSFUL
        // ---------------------------------------------

        AuthResult.Success(
            user = AuthUser(
                id = user.id,
                name = user.name,
                email = user.email
            )
        )
    }

    // =================================================
    // GET USER PROFILE
    // =================================================

    /**
     * Loads public user information using the ID
     * of the signed-in account.
     */
    suspend fun getUserById(
        userId: String
    ): AuthUser? = withContext(Dispatchers.IO) {

        val user = userDao.getUserById(
            userId
        ) ?: return@withContext null

        AuthUser(
            id = user.id,
            name = user.name,
            email = user.email
        )
    }

    // =================================================
    // EMAIL HELPERS
    // =================================================

    private fun normalizeEmail(
        email: String
    ): String {

        return email.trim().lowercase(
            Locale.ROOT
        )
    }

    private fun isValidEmail(
        email: String
    ): Boolean {

        return android.util.Patterns.EMAIL_ADDRESS
            .matcher(email)
            .matches()
    }

    // =================================================
    // DATABASE ERROR HELPER
    // =================================================

    private fun isEmailConstraintError(
        throwable: Throwable
    ): Boolean {

        var current: Throwable? = throwable

        while (current != null) {

            if (
                current is SQLiteConstraintException ||
                current is android.database.sqlite
                .SQLiteConstraintException
            ) {
                return true
            }

            current = current.cause
        }

        return false
    }

    // =================================================
    // DEFAULT CATEGORIES FOR NEW ACCOUNTS
    // =================================================

    /**
     * Each new account receives independent
     * category records with its own ownerId.
     */
    private fun createDefaultCategories(
        ownerId: String
    ): List<CategoryEntity> {

        return listOf(

            CategoryEntity(
                id = UUID.randomUUID().toString(),
                ownerId = ownerId,
                name = "Design System",
                emoji = "🎨",
                colorArgb = 0xFF8B5CF6.toInt(),
                isDefault = true
            ),

            CategoryEntity(
                id = UUID.randomUUID().toString(),
                ownerId = ownerId,
                name = "College / Study",
                emoji = "📚",
                colorArgb = 0xFFF59E0B.toInt(),
                isDefault = true
            ),

            CategoryEntity(
                id = UUID.randomUUID().toString(),
                ownerId = ownerId,
                name = "Personal & Health",
                emoji = "🌿",
                colorArgb = 0xFF10B981.toInt(),
                isDefault = true
            ),

            CategoryEntity(
                id = UUID.randomUUID().toString(),
                ownerId = ownerId,
                name = "Client Projects",
                emoji = "💼",
                colorArgb = 0xFF0EA5E9.toInt(),
                isDefault = true
            ),

            CategoryEntity(
                id = UUID.randomUUID().toString(),
                ownerId = ownerId,
                name = "Mobile Dev",
                emoji = "💻",
                colorArgb = 0xFFEC4899.toInt(),
                isDefault = true
            )
        )
    }
}

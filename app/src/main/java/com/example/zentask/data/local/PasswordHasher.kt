package com.example.zentask.data.local

import android.os.Build
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Password hashing utility for ZenTask local accounts.
 *
 * Passwords are converted into salted PBKDF2 hashes
 * before being stored in Room Database.
 *
 * This is intended for local, offline authentication.
 */
object PasswordHasher {

    // Number of PBKDF2 iterations.
    private const val ITERATIONS = 210_000

    // Password hash length in bits.
    private const val KEY_LENGTH = 256

    // Random salt length in bytes.
    private const val SALT_LENGTH = 16

    // PBKDF2WithHmacSHA1 supports older Android APIs.
    private const val ALGORITHM =
        "PBKDF2WithHmacSHA1"

    private val secureRandom = SecureRandom()

    /**
     * Represents the result of password hashing.
     *
     * Store hash and salt separately in UserEntity.
     */
    data class HashResult(
        val hash: String,
        val salt: String
    )

    /**
     * Generates a random salt and hashes the password.
     *
     * IMPORTANT:
     * Never store the original password.
     */
    fun hashPassword(
        password: String
    ): HashResult {

        require(password.isNotBlank()) {
            "Password cannot be empty."
        }

        val saltBytes = ByteArray(SALT_LENGTH)

        secureRandom.nextBytes(saltBytes)

        val hashBytes = deriveKey(
            password = password,
            salt = saltBytes
        )

        return HashResult(
            hash = encodeBase64(hashBytes),
            salt = encodeBase64(saltBytes)
        )
    }

    /**
     * Verifies a login password against
     * the stored password hash and salt.
     */
    fun verifyPassword(
        password: String,
        storedHash: String,
        storedSalt: String
    ): Boolean {

        return try {

            val saltBytes = decodeBase64(
                storedSalt
            )

            val expectedHash = decodeBase64(
                storedHash
            )

            val actualHash = deriveKey(
                password = password,
                salt = saltBytes
            )

            // Constant-time comparison helps reduce
            // information leakage through timing.
            MessageDigest.isEqual(
                expectedHash,
                actualHash
            )

        } catch (exception: Exception) {

            false
        }
    }

    /**
     * Creates a PBKDF2 password-derived key.
     */
    private fun deriveKey(
        password: String,
        salt: ByteArray
    ): ByteArray {

        val passwordChars = password.toCharArray()

        val keySpec = PBEKeySpec(
            passwordChars,
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        return try {

            val factory =
                SecretKeyFactory.getInstance(
                    ALGORITHM
                )

            factory.generateSecret(
                keySpec
            ).encoded

        } finally {

            keySpec.clearPassword()

            passwordChars.fill('\u0000')
        }
    }

    /**
     * Encodes bytes into a Base64 string.
     *
     * android.util.Base64 is supported on
     * all Android versions targeted by ZenTask.
     */
    private fun encodeBase64(
        bytes: ByteArray
    ): String {

        return android.util.Base64.encodeToString(
            bytes,
            android.util.Base64.NO_WRAP
        )
    }

    /**
     * Decodes a Base64 string into bytes.
     */
    private fun decodeBase64(
        value: String
    ): ByteArray {

        return android.util.Base64.decode(
            value,
            android.util.Base64.NO_WRAP
        )
    }
}

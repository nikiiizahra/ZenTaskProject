package com.example.zentask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // =================================================
    // REGISTER NEW USER
    // =================================================

    // Returns the new user's row ID.
    // If the email already exists, Room will throw
    // a constraint exception instead of overwriting
    // the existing account.
    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertUser(
        user: UserEntity
    ): Long

    // =================================================
    // FIND USER BY EMAIL
    // =================================================

    // Used during login and to check whether
    // an email has already been registered.
    @Query(
        """
        SELECT *
        FROM users
        WHERE email = :email
        LIMIT 1
        """
    )
    suspend fun getUserByEmail(
        email: String
    ): UserEntity?

    // =================================================
    // FIND USER BY ID
    // =================================================

    @Query(
        """
        SELECT *
        FROM users
        WHERE id = :userId
        LIMIT 1
        """
    )
    suspend fun getUserById(
        userId: String
    ): UserEntity?

    // =================================================
    // OBSERVE USER PROFILE
    // =================================================

    // Automatically emits updated profile data
    // when the user record changes.
    @Query(
        """
        SELECT *
        FROM users
        WHERE id = :userId
        LIMIT 1
        """
    )
    fun observeUserById(
        userId: String
    ): Flow<UserEntity?>

    // =================================================
    // UPDATE USER PROFILE
    // =================================================

    @Update
    suspend fun updateUser(
        user: UserEntity
    ): Int

    // =================================================
    // CHECK WHETHER EMAIL EXISTS
    // =================================================

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM users
            WHERE email = :email
        )
        """
    )
    suspend fun emailExists(
        email: String
    ): Boolean
}

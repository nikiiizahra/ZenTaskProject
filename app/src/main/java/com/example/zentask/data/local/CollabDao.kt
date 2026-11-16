package com.example.zentask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class CollabMemberInfo(
    val userId: String,
    val name: String,
    val email: String,
    val joinedAt: Long
)

@Dao
interface CollabDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInvite(invite: CollabInviteEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMember(member: CollabMemberEntity): Long

    // Tanpa filter pemilik: hanya dipakai di dalam logika join.
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTask(taskId: String): TaskEntity?

    @Query("SELECT * FROM collab_invites WHERE code = :code LIMIT 1")
    suspend fun getInvite(code: String): CollabInviteEntity?

    @Query(
        """
        SELECT * FROM collab_invites
        WHERE taskId = :taskId AND revoked = 0 AND expiresAt > :now
        ORDER BY createdAt DESC LIMIT 1
        """
    )
    suspend fun getActiveInvite(taskId: String, now: Long): CollabInviteEntity?

    @Query(
        """
        SELECT * FROM collab_invites
        WHERE taskId = :taskId AND revoked = 0
        ORDER BY createdAt DESC LIMIT 1
        """
    )
    fun observeLatestInvite(taskId: String): Flow<CollabInviteEntity?>

    @Query(
        """
        UPDATE collab_invites SET revoked = 1
        WHERE taskId = :taskId AND ownerId = :ownerId AND revoked = 0
        """
    )
    suspend fun revokeInvites(taskId: String, ownerId: String): Int

    @Query("SELECT COUNT(*) FROM collab_members WHERE taskId = :taskId AND userId = :userId")
    suspend fun countMembership(taskId: String, userId: String): Int

    @Query(
        """
        SELECT u.id AS userId, u.name AS name, u.email AS email, m.joinedAt AS joinedAt
        FROM collab_members m
        INNER JOIN users u ON u.id = m.userId
        WHERE m.taskId = :taskId
        ORDER BY m.joinedAt ASC
        """
    )
    fun observeMembers(taskId: String): Flow<List<CollabMemberInfo>>

    @Query("DELETE FROM collab_members WHERE taskId = :taskId AND userId = :userId")
    suspend fun deleteMember(taskId: String, userId: String): Int
}
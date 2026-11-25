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
    val joinedAt: Long,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
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
        SELECT u.id AS userId, u.name AS name, u.email AS email, m.joinedAt AS joinedAt,
               m.isCompleted AS isCompleted, m.completedAt AS completedAt
        FROM collab_members m
        INNER JOIN users u ON u.id = m.userId
        WHERE m.taskId = :taskId
        ORDER BY m.joinedAt ASC
        """
    )
    fun observeMembers(taskId: String): Flow<List<CollabMemberInfo>>

    @Query(
        """
        SELECT u.id AS userId, u.name AS name, u.email AS email, m.joinedAt AS joinedAt,
               m.isCompleted AS isCompleted, m.completedAt AS completedAt
        FROM collab_members m
        INNER JOIN users u ON u.id = m.userId
        WHERE m.taskId = :taskId
        ORDER BY m.joinedAt ASC
        """
    )
    suspend fun getMembersList(taskId: String): List<CollabMemberInfo>

    @Query(
        """
        UPDATE collab_members SET isCompleted = :isCompleted, completedAt = :completedAt
        WHERE taskId = :taskId AND userId = :userId
        """
    )
    suspend fun updateMemberCompletion(taskId: String, userId: String, isCompleted: Boolean, completedAt: Long?): Int

    @Query("DELETE FROM collab_members WHERE taskId = :taskId AND userId = :userId")
    suspend fun deleteMember(taskId: String, userId: String): Int

    @Query(
        """
    UPDATE tasks
    SET status = :status, progress = :progress,
        completedAt = :completedAt, updatedAt = :updatedAt
    WHERE id = :taskId
    """
    )
    suspend fun setTaskProgress(
        taskId: String, status: String, progress: Int, completedAt: Long?, updatedAt: Long
    ): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: TaskMessageEntity)

    @Query(
        """
    SELECT m.* FROM task_messages m
    WHERE m.taskId = :taskId
      AND (
        EXISTS (SELECT 1 FROM tasks t WHERE t.id = :taskId AND t.ownerId = :userId)
        OR EXISTS (SELECT 1 FROM collab_members c WHERE c.taskId = :taskId AND c.userId = :userId)
      )
    ORDER BY m.createdAt ASC
    """
    )
    fun observeMessages(taskId: String, userId: String): Flow<List<TaskMessageEntity>>
}

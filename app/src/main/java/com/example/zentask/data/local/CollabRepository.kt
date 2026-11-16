package com.example.zentask.data.local

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import java.security.SecureRandom
import java.util.Locale

sealed class JoinResult {
    data class Success(val taskTitle: String) : JoinResult()
    object InvalidCode : JoinResult()
    object Expired : JoinResult()
    object Revoked : JoinResult()
    object OwnTask : JoinResult()
    object AlreadyMember : JoinResult()
    object NotSignedIn : JoinResult()
}

fun JoinResult.toMessage(): String = when (this) {
    is JoinResult.Success -> "Joined \"$taskTitle\". It now appears in your task list."
    JoinResult.InvalidCode -> "Invite code not found. Check the 6 digits and try again."
    JoinResult.Expired -> "This invite code has expired. Ask the owner for a new one."
    JoinResult.Revoked -> "This invite code was revoked by the owner."
    JoinResult.OwnTask -> "You can't join your own task."
    JoinResult.AlreadyMember -> "You already joined this task."
    JoinResult.NotSignedIn -> "Please sign in first."
}

object CollabRules {
    const val CODE_LENGTH = 6
    const val INVITE_TTL_MILLIS = 24L * 60L * 60L * 1000L

    private val random = SecureRandom()

    fun generateCode(): String =
        String.format(Locale.US, "%06d", random.nextInt(1_000_000))

    fun normalizeCode(raw: String): String = raw.filter { it.isDigit() }.take(CODE_LENGTH)

    /** Mengembalikan alasan gagal, atau null kalau boleh bergabung. */
    fun evaluate(
        invite: CollabInviteEntity?,
        now: Long,
        userId: String,
        taskOwnerId: String?,
        alreadyMember: Boolean
    ): JoinResult? = when {
        invite == null || taskOwnerId == null -> JoinResult.InvalidCode
        invite.revoked -> JoinResult.Revoked
        invite.expiresAt <= now -> JoinResult.Expired
        taskOwnerId == userId -> JoinResult.OwnTask
        alreadyMember -> JoinResult.AlreadyMember
        else -> null
    }
}

class CollabRepository(private val database: ZenTaskDatabase) {

    private val dao = database.collabDao()

    fun observeInvite(taskId: String): Flow<CollabInviteEntity?> = dao.observeLatestInvite(taskId)

    fun observeMembers(taskId: String): Flow<List<CollabMemberInfo>> = dao.observeMembers(taskId)

    /** Memakai kode aktif yang sudah ada, atau membuat yang baru. Hanya untuk pemilik. */
    suspend fun createInvite(
        taskId: String,
        ownerId: String,
        now: Long = System.currentTimeMillis()
    ): CollabInviteEntity? = database.withTransaction {
        val task = dao.getTask(taskId)
        if (task == null || task.ownerId != ownerId) return@withTransaction null

        val existing = dao.getActiveInvite(taskId, now)
        if (existing != null) return@withTransaction existing

        for (attempt in 1..10) {
            val invite = CollabInviteEntity(
                code = CollabRules.generateCode(),
                taskId = taskId,
                ownerId = ownerId,
                createdAt = now,
                expiresAt = now + CollabRules.INVITE_TTL_MILLIS
            )
            if (dao.insertInvite(invite) != -1L) return@withTransaction invite
        }
        null
    }

    suspend fun revokeInvites(taskId: String, ownerId: String) =
        dao.revokeInvites(taskId, ownerId)

    suspend fun join(
        userId: String,
        rawCode: String,
        now: Long = System.currentTimeMillis()
    ): JoinResult = database.withTransaction {
        val code = CollabRules.normalizeCode(rawCode)
        if (code.length != CollabRules.CODE_LENGTH) return@withTransaction JoinResult.InvalidCode

        val invite = dao.getInvite(code)
        val task = invite?.let { dao.getTask(it.taskId) }
        val alreadyMember = invite != null && dao.countMembership(invite.taskId, userId) > 0

        val failure = CollabRules.evaluate(invite, now, userId, task?.ownerId, alreadyMember)
        if (failure != null) return@withTransaction failure

        val validInvite = requireNotNull(invite)
        val validTask = requireNotNull(task)
        dao.insertMember(
            CollabMemberEntity(taskId = validInvite.taskId, userId = userId, joinedAt = now)
        )
        JoinResult.Success(validTask.title)
    }

    suspend fun removeMember(taskId: String, userId: String) =
        dao.deleteMember(taskId, userId)
}
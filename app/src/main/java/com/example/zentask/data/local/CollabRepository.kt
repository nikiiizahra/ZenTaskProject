package com.example.zentask.data.local

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import java.security.SecureRandom
import java.util.Locale
import java.util.UUID

sealed class JoinResult {
    data class Success(val taskTitle: String) : JoinResult()
    object InvalidCode : JoinResult()
    object Expired : JoinResult()
    object Revoked : JoinResult()
    object OwnTask : JoinResult()
    object AlreadyMember : JoinResult()
    object NotSignedIn : JoinResult()
}
data class ProgressResult(val percent: Int, val status: String)

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
    const val SUBTASK_TITLE_MAX = 80
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

    /** Persentase dan status dari jumlah bagian yang selesai. */
    fun progressOf(done: Int, total: Int): ProgressResult {
        if (total <= 0) return ProgressResult(0, "NOT_STARTED")
        val safeDone = done.coerceIn(0, total)
        val status = when {
            safeDone == total -> "COMPLETED"
            safeDone > 0 -> "IN_PROGRESS"
            else -> "NOT_STARTED"
        }
        return ProgressResult(safeDone * 100 / total, status)
    }

    /** Pemilik boleh mencentang semua sub task, penanggung jawab hanya miliknya. */
    fun canToggleSubtask(taskOwnerId: String, assigneeId: String?, userId: String): Boolean =
        userId == taskOwnerId || (assigneeId != null && assigneeId == userId)
}

class CollabRepository(private val database: ZenTaskDatabase) {

    private val dao = database.collabDao()

    // ---------- Undangan dan anggota ----------

    fun observeInvite(taskId: String): Flow<CollabInviteEntity?> = dao.observeLatestInvite(taskId)

    fun observeMembers(taskId: String): Flow<List<CollabMemberInfo>> = dao.observeMembers(taskId)

    suspend fun getMembersList(taskId: String): List<CollabMemberInfo> = dao.getMembersList(taskId)

    /** Memakai kode aktif yang ada, atau membuat yang baru. Hanya untuk pemilik. */
    suspend fun createInvite(
        taskId: String,
        ownerId: String,
        now: Long = System.currentTimeMillis()
    ): CollabInviteEntity? = database.withTransaction {
        val task = dao.getTask(taskId)
        if (task == null || task.ownerId != ownerId) return@withTransaction null

        ensureOwnerRow(task, now)

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

        ensureOwnerRow(validTask, validInvite.createdAt)
        dao.insertMember(
            CollabMemberEntity(
                taskId = validInvite.taskId, userId = userId, joinedAt = now, role = "EDITOR"
            )
        )
        recompute(validInvite.taskId, now)

        JoinResult.Success(validTask.title)
    }

    suspend fun removeMember(taskId: String, userId: String) = database.withTransaction {
        dao.deleteMember(taskId, userId)
        dao.unassignUser(taskId, userId)
        recompute(taskId, System.currentTimeMillis())
    }

    // ---------- Progres per orang ----------

    suspend fun toggleMemberCompletion(
        taskId: String,
        userId: String,
        isCompleted: Boolean,
        now: Long = System.currentTimeMillis()
    ) = database.withTransaction {
        val task = dao.getTask(taskId) ?: return@withTransaction
        val completedAt = if (isCompleted) now else null
        val updated = dao.updateMemberCompletion(taskId, userId, isCompleted, completedAt)

        // Hanya pemilik yang boleh dibuatkan baris otomatis. Orang lain harus lewat kode undangan.
        if (updated == 0 && task.ownerId == userId) {
            dao.insertMember(
                CollabMemberEntity(
                    taskId = taskId, userId = userId, joinedAt = now, role = "OWNER",
                    isCompleted = isCompleted, completedAt = completedAt
                )
            )
        }
        recompute(taskId, now)
    }

    // Pemilik dicatat sebagai anggota. Kalau task sudah Completed sebelum ada kolaborasi,
    // bagian pemilik dianggap selesai supaya angkanya tetap konsisten.
    private suspend fun ensureOwnerRow(task: TaskEntity, now: Long) {
        val done = task.status == "COMPLETED"
        dao.insertMember(
            CollabMemberEntity(
                taskId = task.id, userId = task.ownerId, joinedAt = now, role = "OWNER",
                isCompleted = done, completedAt = if (done) task.completedAt ?: now else null
            )
        )
    }

    private suspend fun recompute(taskId: String, now: Long) {
        val task = dao.getTask(taskId) ?: return
        val subtasks = dao.getSubtasks(taskId)

        val (done, total) = if (subtasks.isNotEmpty()) {
            subtasks.count { it.isDone } to subtasks.size
        } else {
            // Tanpa sub task: kembali ke "tanda selesai per orang".
            val members = dao.getMembersList(taskId)
            if (members.isEmpty()) return
            members.count { it.isCompleted } to members.size
        }

        val result = CollabRules.progressOf(done, total)
        val completedAt = if (result.status == "COMPLETED") (task.completedAt ?: now) else null
        dao.setTaskProgress(taskId, result.status, result.percent, completedAt, now)
    }

    // ---------- Chat ----------

    fun observeMessages(taskId: String, userId: String): Flow<List<TaskMessageEntity>> =
        dao.observeMessages(taskId, userId)

    suspend fun sendMessage(
        taskId: String,
        userId: String,
        userName: String,
        text: String,
        now: Long = System.currentTimeMillis()
    ): Boolean = database.withTransaction {
        val task = dao.getTask(taskId) ?: return@withTransaction false
        val allowed = task.ownerId == userId || dao.countMembership(taskId, userId) > 0
        if (!allowed) return@withTransaction false

        dao.insertMessage(
            TaskMessageEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId, senderId = userId, senderName = userName,
                message = text.trim().take(500), createdAt = now
            )
        )
        true
    }

    suspend fun addSystemLog(taskId: String, text: String, now: Long = System.currentTimeMillis()) =
        dao.insertMessage(
            TaskMessageEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId, senderId = "system", senderName = "System",
                message = text, createdAt = now, isSystemLog = true
            )
        )

    // ---------- Sub task ----------

    fun observeSubtasks(taskId: String, userId: String): Flow<List<SubtaskEntity>> =
        dao.observeSubtasks(taskId, userId)

    fun observeSubtaskCounts(userId: String): Flow<List<SubtaskCount>> =
        dao.observeSubtaskCounts(userId)

    suspend fun hasSubtasks(taskId: String): Boolean = dao.countSubtasks(taskId) > 0

    private suspend fun isAssignable(task: TaskEntity, userId: String): Boolean =
        userId == task.ownerId || dao.countMembership(task.id, userId) > 0

    suspend fun addSubtask(
        taskId: String,
        userId: String,
        title: String,
        assigneeId: String?,
        now: Long = System.currentTimeMillis()
    ): Boolean = database.withTransaction {
        val task = dao.getTask(taskId) ?: return@withTransaction false
        val clean = title.trim().take(CollabRules.SUBTASK_TITLE_MAX)
        if (task.ownerId != userId || clean.isBlank()) return@withTransaction false
        if (assigneeId != null && !isAssignable(task, assigneeId)) return@withTransaction false

        dao.insertSubtask(
            SubtaskEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId, title = clean, assigneeId = assigneeId, createdAt = now
            )
        )
        recompute(taskId, now)
        true
    }

    /** Mengembalikan sub task yang diubah, atau null kalau tidak diizinkan. */
    suspend fun toggleSubtask(
        subtaskId: String,
        userId: String,
        isDone: Boolean,
        now: Long = System.currentTimeMillis()
    ): SubtaskEntity? = database.withTransaction {
        val sub = dao.getSubtask(subtaskId) ?: return@withTransaction null
        val task = dao.getTask(sub.taskId) ?: return@withTransaction null
        if (!CollabRules.canToggleSubtask(task.ownerId, sub.assigneeId, userId)) {
            return@withTransaction null
        }

        dao.setSubtaskDone(subtaskId, isDone, if (isDone) now else null)
        recompute(sub.taskId, now)
        sub.copy(isDone = isDone)
    }

    suspend fun updateSubtask(
        subtaskId: String,
        userId: String,
        title: String,
        assigneeId: String?
    ): Boolean = database.withTransaction {
        val sub = dao.getSubtask(subtaskId) ?: return@withTransaction false
        val task = dao.getTask(sub.taskId) ?: return@withTransaction false
        val clean = title.trim().take(CollabRules.SUBTASK_TITLE_MAX)
        if (task.ownerId != userId || clean.isBlank()) return@withTransaction false
        if (assigneeId != null && !isAssignable(task, assigneeId)) return@withTransaction false

        dao.updateSubtask(subtaskId, clean, assigneeId)
        true
    }

    suspend fun deleteSubtask(
        subtaskId: String,
        userId: String,
        now: Long = System.currentTimeMillis()
    ): Boolean = database.withTransaction {
        val sub = dao.getSubtask(subtaskId) ?: return@withTransaction false
        val task = dao.getTask(sub.taskId) ?: return@withTransaction false
        if (task.ownerId != userId) return@withTransaction false

        dao.deleteSubtask(subtaskId)
        recompute(sub.taskId, now)
        true
    }
}
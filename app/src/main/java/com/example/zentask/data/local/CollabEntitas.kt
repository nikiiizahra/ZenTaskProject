package com.example.zentask.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "collab_invites",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId"])]
)
data class CollabInviteEntity(
    @PrimaryKey val code: String,
    val taskId: String,
    val ownerId: String,
    val createdAt: Long,
    val expiresAt: Long,
    val revoked: Boolean = false
)

@Entity(
    tableName = "collab_members",
    primaryKeys = ["taskId", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class CollabMemberEntity(
    val taskId: String,
    val userId: String,
    val joinedAt: Long,
    val role: String = "EDITOR",
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

@Entity(
    tableName = "task_messages",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId"])]
)
data class TaskMessageEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val createdAt: Long,
    val isSystemLog: Boolean = false
)

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId"]), Index(value = ["assigneeId"])]
)
data class SubtaskEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val title: String,
    val assigneeId: String? = null,
    val isDone: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long
)

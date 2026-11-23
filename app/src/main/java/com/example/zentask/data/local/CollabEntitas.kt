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


package com.example.zentask.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",

    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["ownerId"]),
        Index(value = ["categoryId"]),
        Index(value = ["ownerId", "status"]),
        Index(value = ["ownerId", "dueEpochDay"])
    ]
)
data class TaskEntity(

    @PrimaryKey
    val id: String,

    // The account that owns this task.
    // Example: Alyaa's ID or Ochi's ID.
    val ownerId: String,

    // Task information.
    val title: String,

    val description: String = "",

    // Values: LOW, MEDIUM, HIGH
    val priority: String = "MEDIUM",

    // Values: NOT_STARTED, IN_PROGRESS, COMPLETED
    val status: String = "NOT_STARTED",

    // Category information.
    val categoryId: String? = null,

    val categoryName: String = "Design System",

    // Optional deadline.
    // Format displayed to users: DD/MM/YYYY.
    val dueText: String = "",

    // Date stored as epoch day for accurate
    // sorting and deadline calculations.
    val dueEpochDay: Long? = null,

    // Task progress from 0 to 100.
    val progress: Int = 0,

    // Time when the task was created.
    val createdAt: Long =
        System.currentTimeMillis(),

    // Time when the task was last updated.
    val updatedAt: Long =
        System.currentTimeMillis(),

    // Completion time, if completed.
    val completedAt: Long? = null
)

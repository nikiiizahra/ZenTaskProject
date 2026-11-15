
package com.example.zentask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // =================================================
    // CREATE TASK
    // =================================================

    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertTask(
        task: TaskEntity
    )

    // =================================================
    // OBSERVE ALL TASKS BELONGING TO ONE USER
    // =================================================

    // Automatically updates the UI when a task changes.
    @Query(
        """
        SELECT *
        FROM tasks
        WHERE ownerId = :ownerId
        ORDER BY createdAt DESC
        """
    )
    fun observeTasksByOwner(
        ownerId: String
    ): Flow<List<TaskEntity>>

    // =================================================
    // GET ALL TASKS BELONGING TO ONE USER
    // =================================================

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE ownerId = :ownerId
        ORDER BY createdAt DESC
        """
    )
    suspend fun getTasksByOwner(
        ownerId: String
    ): List<TaskEntity>

    // =================================================
    // GET ONE TASK BY ID AND OWNER
    // =================================================

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE id = :taskId
          AND ownerId = :ownerId
        LIMIT 1
        """
    )
    suspend fun getTaskById(
        taskId: String,
        ownerId: String
    ): TaskEntity?

    // =================================================
    // OBSERVE ONE TASK
    // =================================================

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE id = :taskId
          AND ownerId = :ownerId
        LIMIT 1
        """
    )
    fun observeTaskById(
        taskId: String,
        ownerId: String
    ): Flow<TaskEntity?>

    // =================================================
    // UPDATE TASK
    // =================================================

    // Updates only if the task belongs to this user.
    @Query(
        """
        UPDATE tasks
        SET title = :title,
            description = :description,
            priority = :priority,
            status = :status,
            categoryId = :categoryId,
            categoryName = :categoryName,
            dueText = :dueText,
            dueEpochDay = :dueEpochDay,
            progress = :progress,
            completedAt = :completedAt,
            updatedAt = :updatedAt
        WHERE id = :taskId
          AND ownerId = :ownerId
        """
    )
    suspend fun updateTaskDetails(
        taskId: String,
        ownerId: String,
        title: String,
        description: String,
        priority: String,
        status: String,
        categoryId: String?,
        categoryName: String,
        dueText: String,
        dueEpochDay: Long?,
        progress: Int,
        completedAt: Long?,
        updatedAt: Long = System.currentTimeMillis()
    ): Int

    // =================================================
    // UPDATE TASK STATUS
    // =================================================

    @Query(
        """
        UPDATE tasks
        SET status = :status,
            progress = :progress,
            completedAt = :completedAt,
            updatedAt = :updatedAt
        WHERE id = :taskId
          AND ownerId = :ownerId
        """
    )
    suspend fun updateTaskStatus(
        taskId: String,
        ownerId: String,
        status: String,
        progress: Int,
        completedAt: Long?,
        updatedAt: Long = System.currentTimeMillis()
    ): Int

    // =================================================
    // DELETE TASK
    // =================================================

    // Only delete a task owned by the current user.
    @Query(
        """
        DELETE FROM tasks
        WHERE id = :taskId
          AND ownerId = :ownerId
        """
    )
    suspend fun deleteTask(
        taskId: String,
        ownerId: String
    ): Int

    // =================================================
    // TASK COUNTS FOR DASHBOARD
    // =================================================

    @Query(
        """
        SELECT COUNT(*)
        FROM tasks
        WHERE ownerId = :ownerId
        """
    )
    fun observeTotalTaskCount(
        ownerId: String
    ): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM tasks
        WHERE ownerId = :ownerId
          AND status = 'COMPLETED'
        """
    )
    fun observeCompletedTaskCount(
        ownerId: String
    ): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM tasks
        WHERE ownerId = :ownerId
          AND status = 'IN_PROGRESS'
        """
    )
    fun observeInProgressTaskCount(
        ownerId: String
    ): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM tasks
        WHERE ownerId = :ownerId
          AND status = 'NOT_STARTED'
        """
    )
    fun observeNotStartedTaskCount(
        ownerId: String
    ): Flow<Int>

    // =================================================
    // GET UPCOMING TASKS
    // =================================================

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE ownerId = :ownerId
          AND status != 'COMPLETED'
          AND dueEpochDay IS NOT NULL
        ORDER BY dueEpochDay ASC
        LIMIT :limit
        """
    )
    fun observeUpcomingTasks(
        ownerId: String,
        limit: Int = 10
    ): Flow<List<TaskEntity>>
}

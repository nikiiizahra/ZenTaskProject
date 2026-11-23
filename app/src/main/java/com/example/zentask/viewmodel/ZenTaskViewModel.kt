@file:android.annotation.SuppressLint("NewApi")

package com.example.zentask.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zentask.analytics.LocalAnalyticsEngine
import com.example.zentask.analytics.LocalAnalyticsSnapshot
import com.example.zentask.data.local.CategoryEntity
import com.example.zentask.data.local.TaskEntity
import com.example.zentask.data.local.ZenTaskDatabase
import com.example.zentask.model.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.UUID
import com.example.zentask.data.local.CollabInviteEntity
import com.example.zentask.data.local.CollabMemberInfo
import com.example.zentask.data.local.CollabRepository
import com.example.zentask.data.local.JoinResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

// =====================================================
// PRODUCTIVITY MODEL
// =====================================================

data class ProductivitySummary(
    val total: Int = 0,
    val completed: Int = 0,
    val inProgress: Int = 0,
    val notStarted: Int = 0,
    val completionRate: Int = 0
)

// =====================================================
// TASK URGENCY MODEL
// =====================================================

data class TaskUrgency(
    val score: Int = 0,
    val level: String = "Low"
)

// =====================================================
// ZENTASK VIEWMODEL
// =====================================================

class ZenTaskViewModel(
    application: Application
) : AndroidViewModel(application) {

    // =================================================
    // ROOM DATABASE
    // =================================================

    private val database =
        ZenTaskDatabase.getDatabase(application)

    private val taskDao =
        database.taskDao()

    private val categoryDao =
        database.categoryDao()

    private val collabRepository = CollabRepository(database)

    // Only this user's database records may be loaded.
    private var activeOwnerId: String? = null
    val activeUserId: String get() = activeOwnerId.orEmpty()

    // =================================================
    // PROFILE STATE
    // =================================================

    private val _userProfile =
        MutableStateFlow(
            UserProfile("", "")
        )

    val userProfile: StateFlow<UserProfile> =
        _userProfile.asStateFlow()

    // =================================================
    // CATEGORY STATE
    // =================================================

    private val _categories =
        MutableStateFlow<List<TaskCategory>>(
            emptyList()
        )

    val categories: StateFlow<List<TaskCategory>> =
        _categories.asStateFlow()

    // =================================================
    // TASK STATE
    // =================================================

    private val _tasks =
        MutableStateFlow<List<TaskItem>>(
            emptyList()
        )

    val tasks: StateFlow<List<TaskItem>> =
        _tasks.asStateFlow()

    // =================================================
    // TASK PROGRESS STATE
    // =================================================

    private val _taskProgress =
        MutableStateFlow<Map<String, Int>>(
            emptyMap()
        )

    val taskProgress: StateFlow<Map<String, Int>> =
        _taskProgress.asStateFlow()

    // =================================================
    // TASK URGENCY STATE
    // =================================================

    private val _urgencyResults =
        MutableStateFlow<Map<String, TaskUrgency>>(
            emptyMap()
        )

    val urgencyResults:
            StateFlow<Map<String, TaskUrgency>> =
        _urgencyResults.asStateFlow()

    // =================================================
    // VITAL TASK STATE
    // =================================================

    private val _vitalTasks =
        MutableStateFlow<List<TaskItem>>(
            emptyList()
        )

    val vitalTasks: StateFlow<List<TaskItem>> =
        _vitalTasks.asStateFlow()

    // =================================================
    // PRODUCTIVITY STATE
    // =================================================

    private val _productivitySummary =
        MutableStateFlow(
            ProductivitySummary()
        )

    val productivitySummary:
            StateFlow<ProductivitySummary> =
        _productivitySummary.asStateFlow()

    // =================================================
    // ANALYTICS STATE
    // =================================================

    private val _localAnalytics =
        MutableStateFlow(
            LocalAnalyticsSnapshot()
        )

    val localAnalytics:
            StateFlow<LocalAnalyticsSnapshot> =
        _localAnalytics.asStateFlow()

    // =================================================
    // INITIALIZATION
    // =================================================

    init {

        // Observe the currently authenticated user.
        // On logout, stop observing their database.
        // On account switch, load the new owner's data.

        viewModelScope.launch {

            DemoAuthStore.currentUser.collectLatest {
                    user ->

                val ownerId =
                    if (user != null) {
                        DemoAuthStore
                            .getCurrentLocalUserId()
                    } else {
                        null
                    }

                // Immediately remove the previous
                // account's visible workspace.
                activeOwnerId = ownerId

                _tasks.value = emptyList()
                _categories.value = emptyList()
                _taskProgress.value = emptyMap()

                _userProfile.value =
                    if (user != null) {
                        UserProfile(
                            user.fullName,
                            user.email
                        )
                    } else {
                        UserProfile("", "")
                    }

                refreshCalculatedData()

                if (ownerId != null) {

                    // Both Flow collectors are cancelled
                    // when the active account changes.
                    observeOwnerWorkspace(ownerId)
                }
            }
        }
    }

    // =================================================
    // ROOM FLOW OBSERVERS
    // =================================================

    private suspend fun observeOwnerWorkspace(
        ownerId: String
    ) = coroutineScope {

        // Observe categories belonging to this user.
        launch {

            categoryDao
                .observeCategoriesByOwner(ownerId)
                .collect { categoryEntities ->

                    if (
                        activeOwnerId != ownerId
                    ) {
                        return@collect
                    }

                    _categories.value =
                        categoryEntities.map {
                                entity ->
                            entity.toTaskCategory()
                        }

                    refreshCalculatedData()
                }
        }

        // Observe tasks belonging to this user.
        launch {

            taskDao
                .observeVisibleTasks(ownerId)
                .collect { taskEntities ->

                    if (
                        activeOwnerId != ownerId
                    ) {
                        return@collect
                    }

                    _tasks.value =
                        taskEntities.map {
                                entity ->
                            entity.toTaskItem()
                        }

                    _taskProgress.value =
                        taskEntities.associate {
                                entity ->

                            entity.id to
                                    entity.progress
                                        .coerceIn(0, 100)
                        }

                    refreshCalculatedData()
                }
        }
    }

    // =================================================
    // MAPPING ROOM CATEGORY TO UI CATEGORY
    // =================================================

    private fun CategoryEntity.toTaskCategory():
            TaskCategory {

        return TaskCategory(
            id = id,
            name = name,
            emoji = emoji,
            color = Color(colorArgb),
            taskCount = 0
        )
    }

    // =================================================
    // MAPPING ROOM TASK TO UI TASK
    // =================================================

    private fun TaskEntity.toTaskItem():
            TaskItem {

        val category =
            _categories.value.firstOrNull {
                it.id == categoryId
            } ?: _categories.value.firstOrNull {
                it.name == categoryName
            }

        val safePriority =
            runCatching {
                TaskPriority.valueOf(priority)
            }.getOrDefault(
                TaskPriority.MEDIUM
            )

        val safeStatus =
            runCatching {
                TaskStatus.valueOf(status)
            }.getOrDefault(
                TaskStatus.NOT_STARTED
            )

        val hoursRemaining =
            calculateRemainingHours(
                dueEpochDay
            )

        return TaskItem(
            id = id,
            title = title,
            description = description,
            categoryName = categoryName,
            categoryColor =
                category?.color
                    ?: Color(0xFF8B5CF6),
            priority = safePriority,
            status = safeStatus,
            dueText =
                dueText.ifBlank {
                    "No deadline"
                },
            remainingHours = hoursRemaining,
            isVital = false,
            completedAt = completedAt,
            isOwner = ownerId == activeOwnerId
        )
    }

    // =================================================
    // DATE HELPERS
    // =================================================

    private fun parseDeadlineDate(
        text: String
    ): LocalDate? {

        val cleanText = text.trim()

        if (
            cleanText.isBlank() ||
            cleanText.equals(
                "No deadline",
                ignoreCase = true
            )
        ) {
            return null
        }

        val patterns = listOf(
            "d/M/uuuu",
            "d-M-uuuu",
            "d MMMM uuuu"
        )

        for (pattern in patterns) {

            try {

                val formatter =
                    DateTimeFormatter
                        .ofPattern(
                            pattern,
                            Locale.ENGLISH
                        )

                return LocalDate.parse(
                    cleanText,
                    formatter
                )

            } catch (
                _: DateTimeParseException
            ) {
                // Try another supported format.
            }
        }

        return null
    }

    private fun calculateRemainingHours(
        dueEpochDay: Long?
    ): Int? {

        if (dueEpochDay == null) {
            return null
        }

        val dueDate =
            LocalDate.ofEpochDay(
                dueEpochDay
            )

        val hours =
            Duration.between(
                LocalDateTime.now(),
                dueDate.atTime(23, 59)
            ).toHours()

        return hours.coerceIn(
            Int.MIN_VALUE.toLong(),
            Int.MAX_VALUE.toLong()
        ).toInt()
    }

    private fun displayDeadline(
        date: LocalDate?
    ): String {

        if (date == null) {
            return "No deadline"
        }

        return date.format(
            DateTimeFormatter.ofPattern(
                "dd/MM/yyyy",
                Locale.US
            )
        )
    }

    // =================================================
    // TASK EXISTENCE
    // =================================================

    private fun hasTask(
        taskId: String
    ): Boolean {

        return _tasks.value.any {
            it.id == taskId
        }
    }

    // =================================================
    // TASK PROGRESS
    // =================================================

    fun getProgress(
        taskId: String
    ): Int {

        return _taskProgress.value[taskId]
            ?: 0
    }

    fun updateTaskProgress(
        taskId: String,
        progress: Int
    ) {

        val ownerId =
            activeOwnerId ?: return

        val task =
            _tasks.value.firstOrNull {
                it.id == taskId
            } ?: return

        val safeProgress =
            progress.coerceIn(0, 100)

        val newStatus =
            when {
                safeProgress == 100 ->
                    TaskStatus.COMPLETED

                safeProgress == 0 ->
                    TaskStatus.NOT_STARTED

                else ->
                    TaskStatus.IN_PROGRESS
            }

        viewModelScope.launch {

            taskDao.updateTaskStatus(
                taskId = task.id,
                ownerId = ownerId,
                status = newStatus.name,
                progress = safeProgress,
                completedAt =
                    if (
                        newStatus ==
                        TaskStatus.COMPLETED
                    ) {
                        System.currentTimeMillis()
                    } else {
                        null
                    }
            )
        }
    }

    // =================================================
    // URGENCY SCORING
    // =================================================

    fun calculateUrgency(
        task: TaskItem
    ): TaskUrgency {

        if (
            task.status ==
            TaskStatus.COMPLETED
        ) {
            return TaskUrgency(
                score = 0,
                level = "Low"
            )
        }

        val deadlineHours =
            task.remainingHours

        val deadlineScore =
            when {
                deadlineHours == null -> 0
                deadlineHours <= 24 -> 4
                deadlineHours <= 48 -> 3
                deadlineHours <= 168 -> 2
                else -> 1
            }

        val priorityScore =
            when (task.priority) {
                TaskPriority.HIGH -> 3
                TaskPriority.MEDIUM -> 2
                TaskPriority.LOW -> 1
            }

        val progress =
            getProgress(task.id)

        val progressScore =
            when {
                progress < 25 -> 2
                progress <= 75 -> 1
                else -> 0
            }

        val score =
            deadlineScore +
                    priorityScore +
                    progressScore

        val level =
            when {
                score >= 7 -> "Critical"
                score >= 5 -> "High"
                score >= 3 -> "Medium"
                else -> "Low"
            }

        return TaskUrgency(
            score = score,
            level = level
        )
    }

    fun getTaskUrgency(
        taskId: String
    ): TaskUrgency {

        return _urgencyResults.value[taskId]
            ?: TaskUrgency()
    }

    // =================================================
    // AUTOMATIC VITAL TASK DETECTION
    // =================================================

    private fun detectVitalTasks():
            List<TaskItem> {

        return _tasks.value.filter { task ->

            val hasDeadline =
                task.remainingHours != null

            if (
                task.status ==
                TaskStatus.COMPLETED ||
                !hasDeadline
            ) {
                false
            } else {

                val urgency =
                    calculateUrgency(task)

                val highUrgency =
                    urgency.level == "Critical" ||
                            urgency.level == "High"

                val nearDeadline =
                    task.remainingHours != null &&
                            task.remainingHours <= 48

                highUrgency || nearDeadline
            }
        }
    }

    // =================================================
    // PRODUCTIVITY CALCULATION
    // =================================================

    private fun calculateProductivity():
            ProductivitySummary {

        val allTasks =
            _tasks.value

        val total =
            allTasks.size

        val completed =
            allTasks.count {
                it.status ==
                        TaskStatus.COMPLETED
            }

        val inProgress =
            allTasks.count {
                it.status ==
                        TaskStatus.IN_PROGRESS
            }

        val notStarted =
            allTasks.count {
                it.status ==
                        TaskStatus.NOT_STARTED
            }

        val completionRate =
            if (total > 0) {
                completed * 100 / total
            } else {
                0
            }

        return ProductivitySummary(
            total = total,
            completed = completed,
            inProgress = inProgress,
            notStarted = notStarted,
            completionRate =
                completionRate
        )
    }

    // =================================================
    // RECOMPUTE UI SUMMARY
    // =================================================

    private fun refreshCalculatedData() {

        // Refresh urgency values.
        _urgencyResults.value =
            _tasks.value.associate {
                    task ->

                task.id to
                        calculateUrgency(task)
            }

        // Refresh vital status.
        val vitalIds =
            detectVitalTasks()
                .map {
                    it.id
                }
                .toSet()

        _tasks.value =
            _tasks.value.map { task ->
                val categoryColor = _categories.value
                    .firstOrNull { it.name == task.categoryName }
                    ?.color

                task.copy(
                    isVital = task.id in vitalIds,
                    categoryColor = categoryColor ?: task.categoryColor
                )
            }

        _vitalTasks.value =
            _tasks.value.filter {
                it.isVital
            }

        // Refresh productivity.
        _productivitySummary.value =
            calculateProductivity()

        _localAnalytics.value =
            LocalAnalyticsEngine.summarize(
                _tasks.value
            )

        // Refresh category counts.
        _categories.value =
            _categories.value.map {
                    category ->

                category.copy(
                    taskCount =
                        _tasks.value.count {
                                task ->

                            task.categoryName ==
                                    category.name
                        }
                )
            }
    }

    // =================================================
    // ADD TASK
    // =================================================

    /**
     * Existing four-argument calls still work.
     *
     * New screens can also pass description
     * and status using the optional arguments.
     */
    fun addTask(
        title: String,
        categoryName: String,
        priority: TaskPriority,
        due: String,
        description: String = "",
        status: TaskStatus =
            TaskStatus.NOT_STARTED
    ) {

        val ownerId =
            activeOwnerId ?: return

        val cleanTitle =
            title.trim()

        if (cleanTitle.isBlank()) {
            return
        }

        val category =
            _categories.value.firstOrNull {
                it.name.equals(
                    categoryName,
                    ignoreCase = true
                )
            } ?: return

        val dueDate =
            parseDeadlineDate(due)

        val deadlineText =
            displayDeadline(dueDate)

        val initialProgress =
            when (status) {
                TaskStatus.NOT_STARTED -> 0
                TaskStatus.IN_PROGRESS -> 25
                TaskStatus.COMPLETED -> 100
            }

        val newTask =
            TaskEntity(
                id =
                    "t_${UUID.randomUUID()}",
                ownerId = ownerId,
                title = cleanTitle,
                description =
                    description.trim(),
                categoryId =
                    category.id,
                categoryName =
                    category.name,
                priority =
                    priority.name,
                status =
                    status.name,
                dueText =
                    deadlineText,
                dueEpochDay =
                    dueDate?.toEpochDay(),
                progress =
                    initialProgress,
                completedAt =
                    if (
                        status ==
                        TaskStatus.COMPLETED
                    ) {
                        System.currentTimeMillis()
                    } else {
                        null
                    }
            )

        viewModelScope.launch {

            taskDao.insertTask(
                newTask
            )
        }
    }

    // =================================================
    // TOGGLE TASK COMPLETION
    // =================================================

    fun toggleTaskCompletion(
        taskId: String
    ) {

        val task =
            _tasks.value.firstOrNull {
                it.id == taskId
            } ?: return

        val newStatus =
            if (
                task.status ==
                TaskStatus.COMPLETED
            ) {
                TaskStatus.IN_PROGRESS
            } else {
                TaskStatus.COMPLETED
            }

        updateTaskStatus(
            taskId,
            newStatus
        )
    }

    // =================================================
    // UPDATE TASK STATUS
    // =================================================

    fun updateTaskStatus(
        taskId: String,
        status: TaskStatus
    ) {
        val ownerId = activeOwnerId ?: return
        if (!hasTask(taskId)) return

        viewModelScope.launch {
            val members = collabRepository.getMembersList(taskId)
            val hasCollabMembers = members.isNotEmpty()

            val newProgress = when (status) {
                TaskStatus.COMPLETED -> {
                    if (hasCollabMembers) {
                        members.forEach { member ->
                            collabRepository.toggleMemberCompletion(taskId, member.userId, true)
                        }
                    }
                    100
                }
                TaskStatus.NOT_STARTED -> {
                    if (hasCollabMembers) {
                        members.forEach { member ->
                            collabRepository.toggleMemberCompletion(taskId, member.userId, false)
                        }
                    }
                    0
                }
                TaskStatus.IN_PROGRESS -> {
                    if (hasCollabMembers) {
                        val doneCount = members.count { it.isCompleted }
                        if (doneCount > 0) {
                            ((doneCount.toDouble() / members.size.toDouble()) * 100).toInt()
                        } else {
                            25
                        }
                    } else {
                        val oldProgress = getProgress(taskId)
                        if (oldProgress <= 0 || oldProgress >= 100) 50 else oldProgress
                    }
                }
            }

            taskDao.updateTaskStatus(
                taskId = taskId,
                ownerId = ownerId,
                status = status.name,
                progress = newProgress,
                completedAt = if (status == TaskStatus.COMPLETED) System.currentTimeMillis() else null
            )

            _taskProgress.value = _taskProgress.value + (taskId to newProgress)
            _tasks.value = _tasks.value.map { task ->
                if (task.id == taskId) {
                    task.copy(
                        status = status,
                        completedAt = if (status == TaskStatus.COMPLETED) System.currentTimeMillis() else null
                    )
                } else task
            }

            refreshCalculatedData()
        }
    }

    // =================================================
    // UPDATE TASK DEADLINE
    // =================================================

    fun updateTaskDeadline(
        taskId: String,
        remainingHours: Int
    ) {

        val ownerId =
            activeOwnerId ?: return

        if (!hasTask(taskId)) {
            return
        }

        viewModelScope.launch {

            val existing =
                taskDao.getTaskById(
                    taskId,
                    ownerId
                ) ?: return@launch

            val newDeadline =
                LocalDateTime.now()
                    .plusHours(
                        remainingHours.toLong()
                    )
                    .toLocalDate()

            taskDao.updateTaskDetails(
                taskId = taskId,
                ownerId = ownerId,
                title = existing.title,
                description =
                    existing.description,
                priority =
                    existing.priority,
                status =
                    existing.status,
                categoryId =
                    existing.categoryId,
                categoryName =
                    existing.categoryName,
                dueText =
                    displayDeadline(
                        newDeadline
                    ),
                dueEpochDay =
                    newDeadline.toEpochDay(),
                progress =
                    existing.progress,
                completedAt =
                    existing.completedAt
            )
        }
    }

    // =================================================
    // EDIT TASK
    // =================================================

    /**
     * Return value indicates that the request
     * passed local validation.
     *
     * Room writes happen asynchronously.
     */
    fun updateTaskDetails(
        taskId: String,
        title: String,
        description: String,
        categoryName: String,
        priority: TaskPriority,
        dueText: String
    ): Boolean {

        val ownerId =
            activeOwnerId ?: return false

        if (
            title.isBlank() ||
            categoryName.isBlank() ||
            !hasTask(taskId)
        ) {
            return false
        }

        val category =
            _categories.value.firstOrNull {
                it.name.equals(
                    categoryName.trim(),
                    ignoreCase = true
                )
            } ?: return false

        val cleanDueText =
            dueText.trim()

        val parsedDate =
            parseDeadlineDate(
                cleanDueText
            )

        if (
            cleanDueText.isNotBlank() &&
            !cleanDueText.equals(
                "No deadline",
                ignoreCase = true
            ) &&
            parsedDate == null
        ) {
            return false
        }

        viewModelScope.launch {

            val existing =
                taskDao.getTaskById(
                    taskId,
                    ownerId
                ) ?: return@launch

            taskDao.updateTaskDetails(
                taskId = taskId,
                ownerId = ownerId,
                title =
                    title.trim(),
                description =
                    description.trim(),
                priority =
                    priority.name,
                status =
                    existing.status,
                categoryId =
                    category.id,
                categoryName =
                    category.name,
                dueText =
                    displayDeadline(
                        parsedDate
                    ),
                dueEpochDay =
                    parsedDate?.toEpochDay(),
                progress =
                    existing.progress,
                completedAt =
                    existing.completedAt
            )
        }

        return true
    }

    // =================================================
    // DELETE TASK
    // =================================================

    fun deleteTask(
        taskId: String
    ) {

        val ownerId =
            activeOwnerId ?: return

        if (!hasTask(taskId)) {
            return
        }

        viewModelScope.launch {

            taskDao.deleteTask(
                taskId,
                ownerId
            )
        }
    }

    // =================================================
    // ADD CATEGORY
    // =================================================

    fun addCategory(
        name: String,
        emoji: String,
        color: Color
    ) {

        val ownerId =
            activeOwnerId ?: return

        val cleanName =
            name.trim()

        if (cleanName.isBlank()) {
            return
        }

        val duplicate =
            _categories.value.any {
                it.name.equals(
                    cleanName,
                    ignoreCase = true
                )
            }

        if (duplicate) {
            return
        }

        val newCategory =
            CategoryEntity(
                id =
                    "cat_${UUID.randomUUID()}",
                ownerId =
                    ownerId,
                name =
                    cleanName,
                emoji =
                    emoji.ifBlank {
                        "✨"
                    },
                colorArgb =
                    color.toArgb(),
                isDefault = false
            )

        viewModelScope.launch {

            try {

                // Double-check against the database.
                // Another operation might have created
                // this category in the meantime.
                val existing =
                    categoryDao.getCategoryByName(
                        ownerId,
                        cleanName
                    )

                if (existing == null) {
                    categoryDao.insertCategory(
                        newCategory
                    )
                }

            } catch (exception: CancellationException) {
                throw exception

            } catch (_: Exception) {
                // A duplicate-name constraint can
                // reject a concurrent insert.
                // The existing category remains safe.
            }
        }
    }

    // =================================================
    // COLLABORATION (LOCAL DEMO, SAME DEVICE)
    // =================================================

    fun observeInvite(taskId: String): Flow<CollabInviteEntity?> =
        collabRepository.observeInvite(taskId)

    fun observeMembers(taskId: String): Flow<List<CollabMemberInfo>> =
        collabRepository.observeMembers(taskId)

    fun createInvite(taskId: String) {
        val ownerId = activeOwnerId ?: return
        if (_tasks.value.firstOrNull { it.id == taskId }?.isOwner != true) return
        viewModelScope.launch { collabRepository.createInvite(taskId, ownerId) }
    }

    fun revokeInvite(taskId: String) {
        val ownerId = activeOwnerId ?: return
        viewModelScope.launch { collabRepository.revokeInvites(taskId, ownerId) }
    }

    fun joinWithCode(code: String, onResult: (JoinResult) -> Unit) {
        val userId = activeOwnerId
        if (userId == null) {
            onResult(JoinResult.NotSignedIn)
            return
        }
        viewModelScope.launch { onResult(collabRepository.join(userId, code)) }
    }

    fun leaveTask(taskId: String) {
        val userId = activeOwnerId ?: return
        val task = _tasks.value.firstOrNull { it.id == taskId } ?: return
        if (task.isOwner) return
        viewModelScope.launch { collabRepository.removeMember(taskId, userId) }
    }

    fun removeMember(taskId: String, memberId: String) {
        if (_tasks.value.firstOrNull { it.id == taskId }?.isOwner != true) return
        viewModelScope.launch { collabRepository.removeMember(taskId, memberId) }
    }

    // =================================================
    // TASK DISCUSSION & ACTIVITY CHAT
    // =================================================

    private val _taskComments = MutableStateFlow<Map<String, List<TaskComment>>>(
        mapOf(
            "t1" to listOf(
                TaskComment(
                    id = "c1",
                    taskId = "t1",
                    senderId = "owner_alex",
                    senderName = "Alex Johnson",
                    message = "I've uploaded the draft metrics slides to Figma.",
                    timestamp = "10:15 AM"
                ),
                TaskComment(
                    id = "c2",
                    taskId = "t1",
                    senderId = "collab_nikita",
                    senderName = "Nikita Zahra",
                    message = "Looks great! I'll complete the prototype video export.",
                    timestamp = "10:30 AM"
                ),
                TaskComment(
                    id = "c3",
                    taskId = "t1",
                    senderId = "",
                    senderName = "System",
                    message = "Nikita updated status to In Progress",
                    timestamp = "10:32 AM",
                    isSystemLog = true
                )
            ),
            "t2" to listOf(
                TaskComment(
                    id = "c4",
                    taskId = "t2",
                    senderId = "owner_alex",
                    senderName = "Alex Johnson",
                    message = "Please review with lead architect before sprint freeze tomorrow.",
                    timestamp = "09:00 AM"
                )
            )
        )
    )

    fun observeTaskComments(taskId: String): Flow<List<TaskComment>> {
        return combine(_taskComments, _userProfile) { commentMap, profile ->
            val activeUserId = activeOwnerId.orEmpty()
            val activeUserName = profile.fullName.ifBlank { "You" }

            val comments = commentMap[taskId] ?: emptyList()
            comments.map { comment ->
                if (comment.isSystemLog) {
                    comment.copy(isCurrentUser = false)
                } else {
                    val isMe = (comment.senderId.isNotBlank() && comment.senderId == activeUserId) ||
                            (comment.senderName.equals(activeUserName, ignoreCase = true) && activeUserName.isNotBlank())
                    comment.copy(isCurrentUser = isMe)
                }
            }
        }
    }

    fun sendTaskComment(taskId: String, message: String) {
        val cleanMsg = message.trim()
        if (cleanMsg.isBlank()) return

        val activeUserId = activeOwnerId.orEmpty()
        val currentUser = _userProfile.value.fullName.ifBlank { "You" }
        val nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US))

        val newComment = TaskComment(
            taskId = taskId,
            senderId = activeUserId,
            senderName = currentUser,
            message = cleanMsg,
            timestamp = nowTime
        )

        val currentMap = _taskComments.value.toMutableMap()
        val list = (currentMap[taskId] ?: emptyList()) + newComment
        currentMap[taskId] = list
        _taskComments.value = currentMap
    }

    fun toggleMemberCompletion(taskId: String, isCompleted: Boolean) {
        val userId = activeOwnerId ?: return
        viewModelScope.launch {
            collabRepository.toggleMemberCompletion(taskId, userId, isCompleted)

            val members = collabRepository.getMembersList(taskId)
            val total = members.size
            val completedCount = members.count { it.isCompleted }

            if (total > 0) {
                val pct = ((completedCount.toDouble() / total.toDouble()) * 100).toInt()
                val newStatus = when {
                    completedCount == total -> TaskStatus.COMPLETED
                    completedCount > 0 -> TaskStatus.IN_PROGRESS
                    else -> TaskStatus.NOT_STARTED
                }

                if (hasTask(taskId)) {
                    taskDao.updateTaskStatus(
                        taskId = taskId,
                        ownerId = userId,
                        status = newStatus.name,
                        progress = pct,
                        completedAt = if (newStatus == TaskStatus.COMPLETED) System.currentTimeMillis() else null
                    )
                }

                _taskProgress.value = _taskProgress.value + (taskId to pct)
                _tasks.value = _tasks.value.map { task ->
                    if (task.id == taskId) {
                        task.copy(
                            status = newStatus,
                            completedAt = if (newStatus == TaskStatus.COMPLETED) System.currentTimeMillis() else null
                        )
                    } else task
                }
            }

            val userName = _userProfile.value.fullName.ifBlank { "A team member" }
            val statusText = if (isCompleted) "marked their portion as Completed ✓" else "reopened their portion"
            val nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US))

            val logComment = TaskComment(
                taskId = taskId,
                senderId = "system",
                senderName = "System",
                message = "$userName $statusText ($completedCount/$total portion done)",
                timestamp = nowTime,
                isSystemLog = true
            )

            val currentMap = _taskComments.value.toMutableMap()
            val list = (currentMap[taskId] ?: emptyList()) + logComment
            currentMap[taskId] = list
            _taskComments.value = currentMap

            refreshCalculatedData()
        }
    }
}

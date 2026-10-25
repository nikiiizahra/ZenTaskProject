
package com.example.zentask.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.zentask.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProductivitySummary(
    val total: Int = 0,
    val completed: Int = 0,
    val inProgress: Int = 0,
    val notStarted: Int = 0,
    val completionRate: Int = 0
)

data class TaskUrgency(
    val score: Int = 0,
    val level: String = "Low"
)

class ZenTaskViewModel : ViewModel() {

    // =============================================
    // USER PROFILE
    // =============================================

    private val _userProfile = MutableStateFlow(
        UserProfile("Alex", "alex@example.com")
    )
    val userProfile: StateFlow<UserProfile> =
        _userProfile.asStateFlow()

    // =============================================
    // CATEGORIES
    // =============================================

    private val _categories = MutableStateFlow(
        listOf(
            TaskCategory(
                "cat_1", "Design System", "🎨",
                Color(0xFF60A5FA), 12
            ),
            TaskCategory(
                "cat_2", "College / Study", "📚",
                Color(0xFF8B5CF6), 8
            ),
            TaskCategory(
                "cat_3", "Personal & Health", "🌿",
                Color(0xFF10B981), 5
            ),
            TaskCategory(
                "cat_4", "Client Projects", "💼",
                Color(0xFFF59E0B), 3
            ),
            TaskCategory(
                "cat_5", "Mobile Dev", "💻",
                Color(0xFF38BDF8), 6
            )
        )
    )

    val categories: StateFlow<List<TaskCategory>> =
        _categories.asStateFlow()

    // =============================================
    // TASK DATA
    // =============================================

    private val _tasks = MutableStateFlow(
        listOf(
            TaskItem(
                id = "t1",
                title = "Finalize Client Pitch Deck & Review",
                categoryName = "Client Projects",
                categoryColor = Color(0xFFF59E0B),
                priority = TaskPriority.HIGH,
                status = TaskStatus.IN_PROGRESS,
                dueText = "Today, 4:00 PM",
                remainingHours = 14,
                isVital = true,
                collabCode = "ZEN-892"
            ),
            TaskItem(
                id = "t2",
                title = "Submit Mobile UI Spec Jetpack Compose",
                categoryName = "Design System",
                categoryColor = Color(0xFF60A5FA),
                priority = TaskPriority.HIGH,
                status = TaskStatus.NOT_STARTED,
                dueText = "Tomorrow, 11:00 AM",
                remainingHours = 32,
                isVital = true
            ),
            TaskItem(
                id = "t3",
                title = "Read Architecture Chapter 4",
                categoryName = "College / Study",
                categoryColor = Color(0xFF8B5CF6),
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.IN_PROGRESS,
                dueText = "Thursday, 8:00 PM",
                remainingHours = 60,
                isVital = false
            ),
            TaskItem(
                id = "t4",
                title = "Morning Guided Meditation (15 mins)",
                categoryName = "Personal & Health",
                categoryColor = Color(0xFF10B981),
                priority = TaskPriority.LOW,
                status = TaskStatus.COMPLETED,
                dueText = "Done Today",
                remainingHours = null,
                isVital = false
            ),
            TaskItem(
                id = "t5",
                title = "Review Kotlin Coroutines Dispatchers",
                categoryName = "Mobile Dev",
                categoryColor = Color(0xFF38BDF8),
                priority = TaskPriority.LOW,
                status = TaskStatus.COMPLETED,
                dueText = "Done Yesterday",
                remainingHours = null,
                isVital = false
            )
        )
    )

    val tasks: StateFlow<List<TaskItem>> =
        _tasks.asStateFlow()

    // =============================================
    // PROGRESS STATE
    // =============================================

    private val _taskProgress = MutableStateFlow(
        mapOf(
            "t1" to 20,
            "t2" to 0,
            "t3" to 50,
            "t4" to 100,
            "t5" to 100
        )
    )

    val taskProgress: StateFlow<Map<String, Int>> =
        _taskProgress.asStateFlow()

    fun getProgress(taskId: String): Int {
        return _taskProgress.value[taskId] ?: 0
    }

    fun updateTaskProgress(taskId: String, progress: Int) {
        if (_tasks.value.none { it.id == taskId }) return

        val safeProgress = progress.coerceIn(0, 100)

        _taskProgress.value =
            _taskProgress.value + (taskId to safeProgress)

        // Perbarui tingkat urgensi jika progress berubah
        refreshCalculatedData()
    }

    // =============================================
    // FUNGSI 1:
    // SMART TASK PRIORITY & URGENCY SCORING
    // =============================================

    fun calculateUrgency(task: TaskItem): TaskUrgency {

        if (task.status == TaskStatus.COMPLETED) {
            return TaskUrgency(
                score = 0,
                level = "Low"
            )
        }

        val deadlineHours = task.remainingHours

        val deadlineScore = when {
            deadlineHours == null -> 0
            deadlineHours <= 24 -> 4
            deadlineHours <= 48 -> 3
            deadlineHours <= 168 -> 2
            else -> 1
        }

        val priorityScore = when (task.priority) {
            TaskPriority.HIGH -> 3
            TaskPriority.MEDIUM -> 2
            TaskPriority.LOW -> 1
        }

        val progress = getProgress(task.id)

        val progressScore = when {
            progress < 25 -> 2
            progress <= 75 -> 1
            else -> 0
        }

        val urgencyScore =
            deadlineScore + priorityScore + progressScore

        val urgencyLevel = when {
            urgencyScore >= 7 -> "Critical"
            urgencyScore >= 5 -> "High"
            urgencyScore >= 3 -> "Medium"
            else -> "Low"
        }

        return TaskUrgency(
            score = urgencyScore,
            level = urgencyLevel
        )
    }

    private val _urgencyResults =
        MutableStateFlow<Map<String, TaskUrgency>>(emptyMap())

    val urgencyResults: StateFlow<Map<String, TaskUrgency>> =
        _urgencyResults.asStateFlow()

    fun getTaskUrgency(taskId: String): TaskUrgency {
        return _urgencyResults.value[taskId] ?: TaskUrgency()
    }

    // =============================================
    // FUNGSI 2:
    // AUTOMATIC VITAL TASK DETECTION
    // =============================================

    private val _vitalTasks =
        MutableStateFlow<List<TaskItem>>(emptyList())

    val vitalTasks: StateFlow<List<TaskItem>> =
        _vitalTasks.asStateFlow()

    private fun detectVitalTasks(): List<TaskItem> {

        return _tasks.value.filter { task ->

            if (task.status == TaskStatus.COMPLETED) {
                false
            } else {

                val urgency = calculateUrgency(task)

                val urgentLevel =
                    urgency.level == "Critical" ||
                            urgency.level == "High"

                val nearDeadline =
                    task.remainingHours != null &&
                            task.remainingHours <= 48

                urgentLevel || nearDeadline
            }
        }
    }

    // =============================================
    // FUNGSI 3:
    // SMART PRODUCTIVITY SUMMARY
    // =============================================

    private val _productivitySummary =
        MutableStateFlow(ProductivitySummary())

    val productivitySummary: StateFlow<ProductivitySummary> =
        _productivitySummary.asStateFlow()

    private fun calculateProductivity(): ProductivitySummary {

        val allTasks = _tasks.value
        val total = allTasks.size

        val completed = allTasks.count {
            it.status == TaskStatus.COMPLETED
        }

        val inProgress = allTasks.count {
            it.status == TaskStatus.IN_PROGRESS
        }

        val notStarted = allTasks.count {
            it.status == TaskStatus.NOT_STARTED
        }

        val completionRate =
            if (total > 0) {
                (completed * 100) / total
            } else {
                0
            }

        return ProductivitySummary(
            total = total,
            completed = completed,
            inProgress = inProgress,
            notStarted = notStarted,
            completionRate = completionRate
        )
    }

    // =============================================
    // REFRESH ALL CALCULATED DATA
    // =============================================

    private fun refreshCalculatedData() {

        val calculatedUrgency = _tasks.value.associate { task ->
            task.id to calculateUrgency(task)
        }

        _urgencyResults.value = calculatedUrgency

        val detectedVital = detectVitalTasks()
        val vitalIds = detectedVital.map { it.id }.toSet()

        // Sinkronkan nilai isVital pada model task
        _tasks.value = _tasks.value.map { task ->
            task.copy(
                isVital = task.id in vitalIds
            )
        }

        _vitalTasks.value =
            _tasks.value.filter { it.isVital }

        _productivitySummary.value =
            calculateProductivity()
    }

    // =============================================
    // TASK ACTIONS
    // =============================================

    fun toggleTaskCompletion(taskId: String) {

        _tasks.value = _tasks.value.map { task ->

            if (task.id == taskId) {

                val newStatus =
                    if (task.status == TaskStatus.COMPLETED) {
                        TaskStatus.IN_PROGRESS
                    } else {
                        TaskStatus.COMPLETED
                    }

                task.copy(status = newStatus)

            } else {
                task
            }
        }

        val updatedTask =
            _tasks.value.find { it.id == taskId }

        if (updatedTask?.status == TaskStatus.COMPLETED) {
            _taskProgress.value =
                _taskProgress.value + (taskId to 100)
        } else if (updatedTask != null) {
            _taskProgress.value =
                _taskProgress.value + (taskId to 50)
        }

        refreshCalculatedData()
    }

    fun addTask(
        title: String,
        categoryName: String,
        priority: TaskPriority,
        due: String
    ) {

        val category = _categories.value.find {
            it.name == categoryName
        } ?: _categories.value.first()

        val newTask = TaskItem(
            id = "t_${System.currentTimeMillis()}",
            title = title,
            categoryName = category.name,
            categoryColor = category.color,
            priority = priority,
            status = TaskStatus.NOT_STARTED,
            dueText = due.ifBlank { "In 2 days" },
            remainingHours =
                if (priority == TaskPriority.HIGH) 24 else 72,
            isVital = false
        )

        _tasks.value = listOf(newTask) + _tasks.value

        _taskProgress.value =
            _taskProgress.value + (newTask.id to 0)

        refreshCalculatedData()
    }

    fun updateTaskStatus(
        taskId: String,
        status: TaskStatus
    ) {

        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(status = status)
            } else {
                task
            }
        }

        if (status == TaskStatus.COMPLETED) {
            _taskProgress.value =
                _taskProgress.value + (taskId to 100)
        }

        refreshCalculatedData()
    }

    fun updateTaskDeadline(
        taskId: String,
        remainingHours: Int
    ) {

        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(
                    remainingHours = remainingHours
                )
            } else {
                task
            }
        }

        refreshCalculatedData()
    }

    fun deleteTask(taskId: String) {

        _tasks.value = _tasks.value.filterNot {
            it.id == taskId
        }

        _taskProgress.value =
            _taskProgress.value - taskId

        refreshCalculatedData()
    }

    // =============================================
    // CATEGORY ACTIONS
    // =============================================

    fun addCategory(
        name: String,
        emoji: String,
        color: Color
    ) {

        val newCategory = TaskCategory(
            id = "cat_${System.currentTimeMillis()}",
            name = name,
            emoji = emoji.ifBlank { "✨" },
            color = color,
            taskCount = 0
        )

        _categories.value =
            _categories.value + newCategory
    }

    // =============================================
    // COLLABORATION SIMULATION
    // =============================================

    fun joinCollabTask(code: String): Boolean {

        if (code.isBlank()) return false

        val joinedTask = TaskItem(
            id = "t_collab_${System.currentTimeMillis()}",
            title = "Shared Team Sprint: $code",
            categoryName = "Client Projects",
            categoryColor = Color(0xFFF59E0B),
            priority = TaskPriority.HIGH,
            status = TaskStatus.IN_PROGRESS,
            dueText = "Tomorrow, 5:00 PM",
            remainingHours = 28,
            isVital = false,
            collabCode = code
        )

        _tasks.value = listOf(joinedTask) + _tasks.value

        _taskProgress.value =
            _taskProgress.value + (joinedTask.id to 0)

        refreshCalculatedData()

        return true
    }

    // =============================================
    // INITIAL CALCULATION
    // =============================================

    init {
        refreshCalculatedData()
    }
}

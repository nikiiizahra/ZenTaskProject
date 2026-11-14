
package com.example.zentask.viewmodel

import androidx.lifecycle.ViewModel
import com.example.zentask.model.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

data class TaskFormState(
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: String = "Not Started",
    val category: String = "Design System",
    val dueText: String = "",
    val titleError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val dueError: String? = null
)

class TaskFormViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        TaskFormState()
    )

    val state: StateFlow<TaskFormState> =
        _state.asStateFlow()

    // ============================================
    // TASK TITLE
    // ============================================

    fun onTitleChange(value: String) {
        _state.update {
            it.copy(
                title = value,
                titleError = validateTitle(value)
            )
        }
    }

    // ============================================
    // DESCRIPTION
    // ============================================

    fun onDescriptionChange(value: String) {
        _state.update {
            it.copy(
                description = value,
                descriptionError =
                    validateDescription(value)
            )
        }
    }

    // ============================================
    // PRIORITY
    // ============================================

    fun onPriorityChange(value: TaskPriority) {
        _state.update {
            it.copy(priority = value)
        }
    }

    // ============================================
    // STATUS
    // ============================================

    fun onStatusChange(value: String) {
        val validStatuses = setOf(
            "Not Started",
            "In Progress",
            "Completed"
        )

        if (value !in validStatuses) return

        _state.update {
            it.copy(status = value)
        }
    }

    // ============================================
    // CATEGORY
    // ============================================

    fun onCategoryChange(value: String) {
        _state.update {
            it.copy(
                category = value,
                categoryError =
                    validateCategory(value)
            )
        }
    }

    // ============================================
    // DEADLINE
    // ============================================

    fun onDueTextChange(value: String) {
        _state.update {
            it.copy(
                dueText = value,
                dueError = validateDue(value)
            )
        }
    }

    // ============================================
    // VALIDATE ALL FIELDS
    // ============================================

    fun validateAll(): Boolean {
        val current = _state.value

        val updated = current.copy(
            titleError =
                validateTitle(current.title),

            descriptionError =
                validateDescription(
                    current.description
                ),

            categoryError =
                validateCategory(
                    current.category
                ),

            dueError =
                validateDue(current.dueText)
        )

        _state.value = updated

        return updated.titleError == null &&
                updated.descriptionError == null &&
                updated.categoryError == null &&
                updated.dueError == null
    }

    // ============================================
    // RESET FORM
    // ============================================

    fun reset() {
        _state.value = TaskFormState()
    }

    // ============================================
    // TITLE VALIDATION
    // ============================================

    private fun validateTitle(
        value: String
    ): String? = when {

        value.isBlank() ->
            "Task title is required."

        value.trim().length < 3 ->
            "Task title must be at least 3 characters."

        else -> null
    }

    // ============================================
    // DESCRIPTION VALIDATION
    // ============================================

    private fun validateDescription(
        value: String
    ): String? = when {

        value.length > 200 ->
            "Description cannot exceed 200 characters."

        else -> null
    }

    // ============================================
    // CATEGORY VALIDATION
    // ============================================

    private fun validateCategory(
        value: String
    ): String? = when {

        value.isBlank() ->
            "Please select a category."

        else -> null
    }

    // ============================================
    // DEADLINE VALIDATION
    // ============================================

    private fun validateDue(
        value: String
    ): String? {

        // Deadline is optional.
        if (value.isBlank()) {
            return null
        }

        val formatter = DateTimeFormatter
            .ofPattern("d/M/uuuu", Locale.US)

        return try {
            LocalDate.parse(
                value.trim(),
                formatter
            )

            null

        } catch (
            exception: DateTimeParseException
        ) {
            "Please use DD/MM/YYYY (e.g. 15/11/2026)."
        }
    }
}

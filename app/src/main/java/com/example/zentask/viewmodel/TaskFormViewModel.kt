package com.example.zentask.viewmodel

import androidx.lifecycle.ViewModel
import com.example.zentask.model.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TaskFormState(
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: String = "Not Started",
    val category: String = "",
    val dueText: String = "",
    val titleError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val dueError: String? = null
)

class TaskFormViewModel : ViewModel() {

    private val _state = MutableStateFlow(TaskFormState())
    val state: StateFlow<TaskFormState> = _state.asStateFlow()

    fun onTitleChange(v: String) =
        _state.update { it.copy(title = v, titleError = validateTitle(v)) }

    fun onDescriptionChange(v: String) =
        _state.update { it.copy(description = v, descriptionError = validateDescription(v)) }

    fun onPriorityChange(v: TaskPriority) = _state.update { it.copy(priority = v) }

    fun onStatusChange(v: String) = _state.update { it.copy(status = v) }

    fun onCategoryChange(v: String) =
        _state.update { it.copy(category = v, categoryError = validateCategory(v)) }

    fun onDueTextChange(v: String) = _state.update { it.copy(dueText = v, dueError = null) }

    /** Validasi semua field sekaligus. true kalau semuanya valid. */
    fun validateAll(): Boolean {
        val s = _state.value
        val updated = s.copy(
            titleError = validateTitle(s.title),
            descriptionError = validateDescription(s.description),
            categoryError = validateCategory(s.category),
            dueError = null // deadline bersifat opsional
        )
        _state.value = updated
        return updated.titleError == null &&
                updated.descriptionError == null &&
                updated.categoryError == null
    }

    fun reset() {
        _state.value = TaskFormState()
    }

    private fun validateTitle(v: String): String? = when {
        v.isBlank() -> "Title is required"
        v.trim().length < 3 -> "Title must be at least 3 characters"
        else -> null
    }

    private fun validateDescription(v: String): String? =
        if (v.length > 200) "Description must be 200 characters or fewer" else null

    private fun validateCategory(v: String): String? =
        if (v.isBlank()) "Please choose a category" else null
}
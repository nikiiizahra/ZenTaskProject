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
    val priority: TaskPriority = TaskPriority.HIGH,
    val status: String = "In Progress",
    val category: String = "🎨 UI Design",
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

    fun onDueTextChange(v: String) =
        _state.update { it.copy(dueText = v, dueError = validateDue(v)) }

    /** Validasi semua field sekaligus. true kalau semuanya valid. */
    fun validateAll(): Boolean {
        val s = _state.value
        val updated = s.copy(
            titleError = validateTitle(s.title),
            descriptionError = validateDescription(s.description),
            categoryError = validateCategory(s.category),
            dueError = validateDue(s.dueText)
        )
        _state.value = updated
        return updated.titleError == null &&
                updated.descriptionError == null &&
                updated.categoryError == null &&
                updated.dueError == null
    }

    fun reset() {
        _state.value = TaskFormState()
    }

    private fun validateTitle(v: String): String? = when {
        v.isBlank() -> "Judul tidak boleh kosong"
        v.trim().length < 3 -> "Judul minimal 3 karakter"
        else -> null
    }

    private fun validateDescription(v: String): String? =
        if (v.length > 200) "Deskripsi maksimal 200 karakter" else null

    private fun validateCategory(v: String): String? =
        if (v.isBlank()) "Kategori wajib dipilih" else null

    private fun validateDue(v: String): String? =
        if (v.isBlank()) "Tenggat tidak boleh kosong" else null
}
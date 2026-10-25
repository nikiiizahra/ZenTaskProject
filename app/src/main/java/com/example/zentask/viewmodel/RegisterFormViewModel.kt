package com.example.zentask.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterFormState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val fullName: String? = null      // hasil gabungan nama depan + nama belakang
)

class RegisterFormViewModel : ViewModel() {

    private val _state = MutableStateFlow(RegisterFormState())
    val state: StateFlow<RegisterFormState> = _state.asStateFlow()

    fun onFirstNameChange(v: String) =
        _state.update { it.copy(firstName = v, firstNameError = validateName(v, "Nama depan")) }

    fun onLastNameChange(v: String) =
        _state.update { it.copy(lastName = v, lastNameError = validateName(v, "Nama belakang")) }

    fun onEmailChange(v: String) =
        _state.update { it.copy(email = v, emailError = validateEmail(v)) }

    fun onPasswordChange(v: String) =
        _state.update { it.copy(password = v, passwordError = validatePassword(v)) }

    /** Submit register: semua field divalidasi, nama digabung kalau valid. */
    fun submit(): Boolean {
        val s = _state.value
        val updated = s.copy(
            firstNameError = validateName(s.firstName, "Nama depan"),
            lastNameError = validateName(s.lastName, "Nama belakang"),
            emailError = validateEmail(s.email),
            passwordError = validatePassword(s.password)
        )
        val valid = listOf(
            updated.firstNameError, updated.lastNameError,
            updated.emailError, updated.passwordError
        ).all { it == null }

        _state.value = if (valid) {
            updated.copy(fullName = "${s.firstName.trim()} ${s.lastName.trim()}")
        } else {
            updated.copy(fullName = null)
        }
        return valid
    }

    /** Submit login: hanya email dan password yang diperiksa. */
    fun submitLogin(): Boolean {
        val s = _state.value
        val updated = s.copy(
            emailError = validateEmail(s.email),
            passwordError = validatePassword(s.password)
        )
        _state.value = updated
        return updated.emailError == null && updated.passwordError == null
    }

    fun clearResult() = _state.update { it.copy(fullName = null) }

    fun reset() {
        _state.value = RegisterFormState()
    }

    private fun validateName(v: String, label: String): String? = when {
        v.isBlank() -> "$label tidak boleh kosong"
        v.trim().length < 2 -> "$label minimal 2 huruf"
        !v.trim().all { it.isLetter() || it == ' ' } -> "$label hanya boleh huruf"
        else -> null
    }

    private fun validateEmail(v: String): String? = when {
        v.isBlank() -> "Email tidak boleh kosong"
        !Patterns.EMAIL_ADDRESS.matcher(v.trim()).matches() -> "Format email tidak valid"
        else -> null
    }

    private fun validatePassword(v: String): String? = when {
        v.isBlank() -> "Password tidak boleh kosong"
        v.length < 8 -> "Password minimal 8 karakter"
        !v.any { it.isLetter() } -> "Password harus mengandung huruf"
        !v.any { it.isDigit() } -> "Password harus mengandung angka"
        else -> null
    }
}
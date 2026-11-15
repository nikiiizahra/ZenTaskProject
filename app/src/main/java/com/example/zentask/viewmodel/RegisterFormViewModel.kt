
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
    val fullName: String? = null
)

class RegisterFormViewModel : ViewModel() {

    private val _state =
        MutableStateFlow(RegisterFormState())

    val state: StateFlow<RegisterFormState> =
        _state.asStateFlow()

    fun onFirstNameChange(value: String) {
        _state.update {
            it.copy(
                firstName = value,
                firstNameError = validateName(
                    value,
                    "First name"
                )
            )
        }
    }

    fun onLastNameChange(value: String) {
        _state.update {
            it.copy(
                lastName = value,
                lastNameError = validateName(
                    value,
                    "Last name"
                )
            )
        }
    }

    fun onEmailChange(value: String) {
        _state.update {
            it.copy(
                email = value,
                emailError = validateEmail(value)
            )
        }
    }

    fun onPasswordChange(value: String) {
        _state.update {
            it.copy(
                password = value,
                passwordError = validatePassword(value)
            )
        }
    }

    // Validate registration form.
    fun submit(): Boolean {
        val current = _state.value

        val updated = current.copy(
            firstNameError = validateName(
                current.firstName,
                "First name"
            ),
            lastNameError = validateName(
                current.lastName,
                "Last name"
            ),
            emailError = validateEmail(
                current.email
            ),
            passwordError = validatePassword(
                current.password
            )
        )

        val valid = listOf(
            updated.firstNameError,
            updated.lastNameError,
            updated.emailError,
            updated.passwordError
        ).all { it == null }

        _state.value = if (valid) {
            updated.copy(
                fullName = listOf(
                    current.firstName.trim(),
                    current.lastName.trim()
                ).joinToString(" ")
            )
        } else {
            updated.copy(fullName = null)
        }

        return valid
    }

    // Validate login input format.
    // Actual demo credential matching is handled
    // separately by DemoAuthStore.
    fun submitLogin(): Boolean {
        val current = _state.value

        val updated = current.copy(
            emailError = validateEmail(
                current.email
            ),
            passwordError = validatePassword(
                current.password
            ),
            fullName = null
        )

        _state.value = updated

        return updated.emailError == null &&
                updated.passwordError == null
    }

    fun clearResult() {
        _state.update {
            it.copy(fullName = null)
        }
    }

    fun reset() {
        _state.value = RegisterFormState()
    }

    private fun validateName(
        value: String,
        label: String
    ): String? = when {
        value.isBlank() ->
            "$label is required."

        value.trim().length < 2 ->
            "$label must contain at least 2 letters."

        !value.trim().all {
            it.isLetter() || it == ' '
        } ->
            "$label can only contain letters."

        else -> null
    }

    private fun validateEmail(
        value: String
    ): String? = when {
        value.isBlank() ->
            "Email address is required."

        !Patterns.EMAIL_ADDRESS.matcher(
            value.trim()
        ).matches() ->
            "Please enter a valid email address."

        else -> null
    }

    private fun validatePassword(
        value: String
    ): String? = when {
        value.isBlank() ->
            "Password is required."

        value.length < 8 ->
            "Password must be at least 8 characters."

        !value.any { it.isLetter() } ->
            "Password must contain at least one letter."

        !value.any { it.isDigit() } ->
            "Password must contain at least one number."

        else -> null
    }
}

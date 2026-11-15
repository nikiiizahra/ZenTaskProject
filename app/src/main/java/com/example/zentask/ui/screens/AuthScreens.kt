package com.example.zentask.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zentask.ZenTaskTheme
import com.example.zentask.data.local.AuthRepository
import com.example.zentask.ui.components.*
import com.example.zentask.viewmodel.DemoAuthStore
import com.example.zentask.viewmodel.RegisterFormViewModel
import kotlinx.coroutines.launch

// =====================================================
// COLORS
// =====================================================

private val AuthPurple = Color(0xFF6E56CF)
private val AuthPurpleLight = Color(0xFF8B5CF6)
private val AuthText = Color(0xFF0F172A)
private val AuthSecondary = Color(0xFF64748B)
private val AuthBorder = Color(0xFFF1F5F9)

// =====================================================
// AUTH SCREEN
// =====================================================

@Composable
fun AuthScreen(
    navController: NavHostController,
    initialRegister: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val formVm: RegisterFormViewModel = viewModel()
    val form by formVm.state.collectAsState()

    var isRegisterTab by rememberSaveable(
        initialRegister
    ) {
        mutableStateOf(initialRegister)
    }

    var acceptCookies by rememberSaveable {
        mutableStateOf(true)
    }

    var weeklySummary by rememberSaveable {
        mutableStateOf(false)
    }

    var rememberMe by rememberSaveable {
        mutableStateOf(true)
    }

    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var authReady by remember {
        mutableStateOf(false)
    }

    var messageTitle by remember {
        mutableStateOf<String?>(null)
    }

    var messageBody by remember {
        mutableStateOf("")
    }

    var showRegisterConfirmation by remember {
        mutableStateOf(false)
    }

    // =================================================
    // INITIALIZE ROOM AUTHENTICATION
    // =================================================

    LaunchedEffect(context) {
        try {
            DemoAuthStore.initialize(
                context.applicationContext
            )

            authReady = true

        } catch (exception: Exception) {
            authReady = false

            messageTitle = "Initialization Error"
            messageBody =
                exception.message
                    ?: "Unable to initialize local storage."
        }
    }

    // =================================================
    // NAVIGATION
    // =================================================

    fun goToDashboard() {
        navController.navigate("dashboard") {
            popUpTo("auth") {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    // =================================================
    // LOCAL LOGIN
    // =================================================

    fun signIn() {
        if (isLoading || !authReady) return

        val valid = formVm.submitLogin()
        if (!valid) return

        val email = form.email.trim()
        val password = form.password

        scope.launch {
            isLoading = true

            try {
                val result = DemoAuthStore.loginLocal(
                    email = email,
                    password = password
                )

                when (result) {
                    is AuthRepository.AuthResult.Success -> {
                        formVm.reset()
                        goToDashboard()
                    }

                    is AuthRepository.AuthResult.Error -> {
                        messageTitle = "Sign In Failed"
                        messageBody = result.message
                    }
                }

            } catch (exception: Exception) {
                messageTitle = "Sign In Failed"
                messageBody =
                    exception.message
                        ?: "Unable to sign in. Please try again."

            } finally {
                isLoading = false
            }
        }
    }

    // =================================================
    // LOCAL REGISTRATION
    // =================================================

    fun registerAccount(
        fullName: String
    ) {
        if (isLoading || !authReady) return

        val email = form.email.trim()
        val password = form.password
        val weeklySummaryValue = weeklySummary

        showRegisterConfirmation = false

        scope.launch {
            isLoading = true

            try {
                val result = DemoAuthStore.registerLocal(
                    fullName = fullName,
                    email = email,
                    password = password,
                    weeklySummary = weeklySummaryValue
                )

                when (result) {
                    is AuthRepository.AuthResult.Success -> {
                        formVm.reset()
                        goToDashboard()
                    }

                    is AuthRepository.AuthResult.Error -> {
                        formVm.clearResult()
                        messageTitle = "Registration Failed"
                        messageBody = result.message
                    }
                }

            } catch (exception: Exception) {
                formVm.clearResult()

                messageTitle = "Registration Failed"
                messageBody =
                    exception.message
                        ?: "Unable to create your account."

            } finally {
                isLoading = false
            }
        }
    }

    // =================================================
    // SCREEN CONTENT
    // =================================================

    ZenBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(12.dp))

            // =========================================
            // LOGO
            // =========================================

            Box {

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    AuthPurple,
                                    AuthPurpleLight
                                )
                            ),
                            CircleShape
                        )
                        .shadow(
                            10.dp,
                            CircleShape,
                            spotColor = Color(0x446E56CF)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.AutoAwesome,
                        contentDescription = "ZenTask logo",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            Color(0xFFF43F5E),
                            CircleShape
                        )
                        .border(
                            1.5.dp,
                            Color.White,
                            CircleShape
                        )
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "ZenTask",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuthText
                )

                if (!isRegisterTab) {

                    Spacer(Modifier.width(6.dp))

                    Surface(
                        color = Color(0xFFEDE9FE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "v2.4",
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical = 2.dp
                            ),
                            color = AuthPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(2.dp))

            Text(
                text = "Focus. Plan. Achieve with Mindful Ease.",
                fontSize = 13.sp,
                color = AuthSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // =========================================
            // LOGIN AND REGISTER TABS
            // =========================================

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFFFAFAFE),
                border = BorderStroke(
                    1.dp,
                    AuthBorder
                )
            ) {

                Row(
                    modifier = Modifier.padding(4.dp)
                ) {

                    AuthTabItem(
                        text = "Login",
                        selected = !isRegisterTab,
                        icon = {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (!isLoading) {
                                isRegisterTab = false
                                formVm.clearResult()
                                showRegisterConfirmation = false
                            }
                        }
                    )

                    AuthTabItem(
                        text = "Register",
                        selected = isRegisterTab,
                        icon = {
                            Icon(
                                Icons.Outlined.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (!isLoading) {
                                isRegisterTab = true
                                formVm.clearResult()
                                showRegisterConfirmation = false
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // =========================================
            // MAIN AUTH FORM
            // =========================================

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(
                    1.dp,
                    AuthBorder
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    Column {

                        Text(
                            text =
                                if (isRegisterTab) {
                                    "Create Your Account"
                                } else {
                                    "Welcome Back"
                                },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuthText
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text =
                                if (isRegisterTab) {
                                    "Begin your mindful task and study journey in seconds."
                                } else {
                                    "Enter your credentials to access your flow workspace."
                                },
                            fontSize = 12.sp,
                            color = AuthSecondary
                        )
                    }

                    // =================================
                    // REGISTER NAME FIELDS
                    // =================================

                    if (isRegisterTab) {

                        AuthInputField(
                            label = "FIRST NAME",
                            value = form.firstName,
                            onValueChange =
                                formVm::onFirstNameChange,
                            icon = {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier =
                                        Modifier.size(18.dp)
                                )
                            },
                            error = form.firstNameError
                        )

                        AuthInputField(
                            label = "LAST NAME",
                            value = form.lastName,
                            onValueChange =
                                formVm::onLastNameChange,
                            icon = {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier =
                                        Modifier.size(18.dp)
                                )
                            },
                            error = form.lastNameError
                        )
                    }

                    // =================================
                    // EMAIL
                    // =================================

                    AuthInputField(
                        label = "EMAIL ADDRESS",
                        value = form.email,
                        onValueChange = formVm::onEmailChange,
                        icon = {
                            Icon(
                                Icons.Outlined.Email,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        error = form.emailError,
                        keyboardType = KeyboardType.Email
                    )

                    // =================================
                    // PASSWORD
                    // =================================

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {

                        Text(
                            text = "PASSWORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuthSecondary,
                            letterSpacing = 0.5.sp
                        )

                        OutlinedTextField(
                            value = form.password,
                            onValueChange =
                                formVm::onPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        isPasswordVisible =
                                            !isPasswordVisible
                                    }
                                ) {
                                    Icon(
                                        imageVector =
                                            if (isPasswordVisible) {
                                                Icons.Outlined.VisibilityOff
                                            } else {
                                                Icons.Outlined.Visibility
                                            },
                                        contentDescription =
                                            "Show or hide password",
                                        tint = Color(0xFF94A3B8),
                                        modifier =
                                            Modifier.size(18.dp)
                                    )
                                }
                            },
                            isError =
                                form.passwordError != null,
                            supportingText = {
                                form.passwordError?.let {
                                    Text(it)
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            visualTransformation =
                                if (isPasswordVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            colors =
                                OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor =
                                        Color(0xFFFAFAFE),
                                    focusedContainerColor =
                                        Color.White,
                                    unfocusedBorderColor =
                                        AuthBorder,
                                    focusedBorderColor =
                                        AuthPurple
                                )
                        )
                    }

                    // =================================
                    // REGISTER OPTIONS
                    // =================================

                    if (isRegisterTab) {

                        val passwordOk =
                            form.password.length >= 8 &&
                                    form.password.any {
                                        it.isLetter()
                                    } &&
                                    form.password.any {
                                        it.isDigit()
                                    }

                        val hintColor =
                            if (passwordOk) {
                                Color(0xFF10B981)
                            } else {
                                Color(0xFF94A3B8)
                            }

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = null,
                                tint = hintColor,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(Modifier.width(6.dp))

                            Text(
                                text = "Minimum 8 characters with letters & numbers",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = hintColor
                            )
                        }

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = acceptCookies,
                                onCheckedChange = {
                                    acceptCookies = it
                                },
                                colors =
                                    CheckboxDefaults.colors(
                                        checkedColor = AuthPurple
                                    )
                            )

                            Text(
                                text = "I agree to local account storage on this device.",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = AuthSecondary
                            )
                        }

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = weeklySummary,
                                onCheckedChange = {
                                    weeklySummary = it
                                },
                                colors =
                                    CheckboxDefaults.colors(
                                        checkedColor = AuthPurple
                                    )
                            )

                            Text(
                                text = "Send me weekly productivity summaries & focus tips.",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = AuthSecondary
                            )
                        }

                    } else {

                        // =================================
                        // LOGIN OPTIONS
                        // =================================

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = {
                                        rememberMe = it
                                    },
                                    colors =
                                        CheckboxDefaults.colors(
                                            checkedColor = AuthPurple
                                        )
                                )

                                Text(
                                    text = "Remember me",
                                    fontSize = 12.sp,
                                    color = AuthSecondary
                                )
                            }

                            Text(
                                text = "Forgot Password?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuthPurple,
                                modifier = Modifier.clickable {
                                    messageTitle =
                                        "Forgot Password"
                                    messageBody =
                                        "Password reset is not available for local accounts in this version."
                                }
                            )
                        }
                    }

                    // =================================
                    // REGISTER / SIGN IN BUTTON
                    // =================================

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(
                                RoundedCornerShape(20.dp)
                            )
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        AuthPurple,
                                        AuthPurpleLight
                                    )
                                )
                            )
                            .clickable(
                                enabled = !isLoading && authReady
                            ) {

                                if (isRegisterTab) {

                                    val valid = formVm.submit()

                                    if (valid) {

                                        if (!acceptCookies) {

                                            formVm.clearResult()

                                            messageTitle =
                                                "Consent Required"
                                            messageBody =
                                                "Please accept local storage preferences to register."

                                        } else {
                                            // submit() exposes a fullName
                                            // when validation succeeds.
                                            showRegisterConfirmation = true
                                        }
                                    }

                                } else {
                                    signIn()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )

                        } else {

                            Text(
                                text =
                                    if (!authReady) {
                                        "Preparing local storage..."
                                    } else if (isRegisterTab) {
                                        "Create Account →"
                                    } else {
                                        "Sign In →"
                                    },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // =========================================
            // SWITCH AUTH MODE
            // =========================================

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(
                    1.dp,
                    AuthBorder
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    Color(0xFFEDE9FE),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                Icons.AutoMirrored.Outlined.Login,
                                contentDescription = null,
                                tint = AuthPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(Modifier.width(10.dp))

                        Column {

                            Text(
                                text =
                                    if (isRegisterTab) {
                                        "Already have an account?"
                                    } else {
                                        "Need a new account?"
                                    },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuthText
                            )

                            Text(
                                text =
                                    if (isRegisterTab) {
                                        "Sign in to your local workspace."
                                    } else {
                                        "Create your local account in seconds."
                                    },
                                fontSize = 11.sp,
                                color = AuthSecondary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.clickable(
                            enabled = !isLoading
                        ) {
                            isRegisterTab = !isRegisterTab
                            formVm.clearResult()
                            showRegisterConfirmation = false
                        },
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White,
                        border = BorderStroke(
                            1.dp,
                            AuthPurple
                        )
                    ) {

                        Text(
                            text =
                                if (isRegisterTab) {
                                    "Sign In"
                                } else {
                                    "Sign Up"
                                },
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuthPurple
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text =
                    if (isRegisterTab) {
                        "OR REGISTER WITH"
                    } else {
                        "OR CONTINUE WITH"
                    },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(12.dp))

            // =========================================
            // GOOGLE SIGN-IN (NOT INTEGRATED)
            // =========================================

            AuthSocialButton(
                text = "Continue with Google",
                symbol = "G",
                symbolColor = Color(0xFFEA4335),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    messageTitle = "Google Sign-In"
                    messageBody =
                        "Google Sign-In is not available in this version. Please use email registration and sign-in."
                }
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Local accounts • Account data is stored on this device",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))
        }

        // =============================================
        // REGISTRATION CONFIRMATION
        // =============================================

        if (
            isRegisterTab &&
            showRegisterConfirmation
        ) {

            val fullName =
                form.fullName
                    ?: listOf(
                        form.firstName.trim(),
                        form.lastName.trim()
                    )
                        .filter { it.isNotBlank() }
                        .joinToString(" ")

            AlertDialog(
                onDismissRequest = {
                    if (!isLoading) {
                        showRegisterConfirmation = false
                        formVm.clearResult()
                    }
                },
                title = {
                    Text("Create Your Account")
                },
                text = {
                    Text(
                        "Welcome, $fullName!\n\n" +
                                "Your account will be saved locally on this device. Continue to create your ZenTask workspace."
                    )
                },
                confirmButton = {
                    TextButton(
                        enabled = !isLoading && authReady,
                        onClick = {
                            registerAccount(fullName)
                        }
                    ) {

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Continue")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        enabled = !isLoading,
                        onClick = {
                            showRegisterConfirmation = false
                            formVm.clearResult()
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        // =============================================
        // INFORMATION / ERROR DIALOG
        // =============================================

        if (messageTitle != null) {

            AlertDialog(
                onDismissRequest = {
                    messageTitle = null
                },
                title = {
                    Text(
                        messageTitle.orEmpty()
                    )
                },
                text = {
                    Text(messageBody)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            messageTitle = null
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

// =====================================================
// LOGIN / REGISTER TAB
// =====================================================

@Composable
private fun AuthTabItem(
    text: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(
                RoundedCornerShape(999.dp)
            )
            .background(
                if (selected) {
                    AuthPurple
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        CompositionLocalProvider(
            LocalContentColor provides
                    if (selected) {
                        Color.White
                    } else {
                        AuthSecondary
                    }
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                icon()

                Spacer(Modifier.width(6.dp))

                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// =====================================================
// AUTH INPUT FIELD
// =====================================================

@Composable
private fun AuthInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: @Composable () -> Unit,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text
) {

    Column(
        verticalArrangement =
            Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AuthSecondary,
            letterSpacing = 0.5.sp
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = icon,
            isError = error != null,
            supportingText = {
                error?.let {
                    Text(it)
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor =
                    Color(0xFFFAFAFE),
                focusedContainerColor =
                    Color.White,
                unfocusedBorderColor =
                    AuthBorder,
                focusedBorderColor =
                    AuthPurple
            )
        )
    }
}

// =====================================================
// GOOGLE AUTH BUTTON
// =====================================================

@Composable
private fun AuthSocialButton(
    text: String,
    symbol: String,
    symbolColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier.clickable(
            onClick = onClick
        ),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(
            1.dp,
            AuthBorder
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = symbol,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = symbolColor
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AuthText
            )
        }
    }
}

// =====================================================
// OTP INFORMATION SCREEN
// =====================================================

// OTP is not connected to a real email service.
@Composable
fun OtpVerificationScreen(
    navController: NavHostController
) {

    ZenBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ZenTaskTheme.TextPrimary
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Color.White,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    Icons.Outlined.Email,
                    contentDescription = null,
                    tint = ZenTaskTheme.Primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Verification Code",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ZenTaskTheme.TextPrimary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Email verification is not available in this version. No verification code has been sent.",
                fontSize = 13.sp,
                color = ZenTaskTheme.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text("Back to Login")
            }
        }
    }
}

// =====================================================
// PREVIEWS
// =====================================================

@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 1100
)
@Composable
fun AuthRegisterScreenPreview() {

    AuthScreen(
        navController = NavHostController(
            LocalContext.current
        ),
        initialRegister = true
    )
}

@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 1000
)
@Composable
fun AuthLoginScreenPreview() {

    AuthScreen(
        navController = NavHostController(
            LocalContext.current
        ),
        initialRegister = false
    )
}

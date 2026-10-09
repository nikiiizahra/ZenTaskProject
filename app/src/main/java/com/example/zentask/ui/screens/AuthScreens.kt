package com.example.zentask.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.ui.components.*

@Composable
fun AuthScreen(
    navController: NavHostController,
    initialRegister: Boolean = false
) {
    var isRegisterTab by remember { mutableStateOf(initialRegister) }
    var email by remember { mutableStateOf("nikitazahra@example.com") }
    var password by remember { mutableStateOf("nikita123") }
    var fullName by remember { mutableStateOf("niki") }
    var acceptCookies by remember { mutableStateOf(true) }
    var weeklySummary by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    ZenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Logo Badge
            Box {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF6E56CF), Color(0xFF8B5CF6))
                            ),
                            CircleShape
                        )
                        .shadow(10.dp, CircleShape, spotColor = Color(0x446E56CF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFFF43F5E), CircleShape)
                        .border(1.5.dp, Color.White, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Brand Title & Tagline
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ZenTask",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                if (!isRegisterTab) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFFEDE9FE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "v2.4",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color(0xFF6E56CF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Focus. Plan. Achieve with Mindful Ease.",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFFFAFAFE),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (!isRegisterTab) Color(0xFF6E56CF) else Color.Transparent)
                            .clickable { isRegisterTab = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Login",
                                tint = if (!isRegisterTab) Color.White else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Login",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (!isRegisterTab) Color.White else Color(0xFF64748B)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isRegisterTab) Color(0xFF6E56CF) else Color.Transparent)
                            .clickable { isRegisterTab = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.PersonAdd,
                                contentDescription = "Register",
                                tint = if (isRegisterTab) Color.White else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Register",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isRegisterTab) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Form Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column {
                        Text(
                            text = if (isRegisterTab) "Create Your Account" else "Welcome Back",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isRegisterTab)
                                "Begin your mindful task and study journey in seconds."
                            else
                                "Enter your credentials to access your flow workspace.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    if (isRegisterTab) {
                        // Full Name
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "FULL NAME",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = "Name",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color(0xFFFAFAFE),
                                    focusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color(0xFFF1F5F9),
                                    focusedBorderColor = Color(0xFF6E56CF)
                                )
                            )
                        }
                    }

                    // Email Address
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "EMAIL ADDRESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = "Email",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFFAFAFE),
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = Color(0xFFF1F5F9),
                                focusedBorderColor = Color(0xFF6E56CF)
                            )
                        )
                    }

                    // Password
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "PASSWORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = "Password",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = "Toggle Password",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFFAFAFE),
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = Color(0xFFF1F5F9),
                                focusedBorderColor = Color(0xFF6E56CF)
                            )
                        )
                    }

                    if (isRegisterTab) {
                        // Password Requirement Check
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Checked",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Minimum 8 characters with letters & numbers",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }

                        // Checkbox 1: Cookies
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = acceptCookies,
                                onCheckedChange = { acceptCookies = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6E56CF))
                            )
                            Text(
                                text = "I accept cookie storage preferences for seamless login and sync.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 16.sp
                            )
                        }

                        // Checkbox 2: Summary
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = weeklySummary,
                                onCheckedChange = { weeklySummary = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6E56CF))
                            )
                            Text(
                                text = "Send me weekly productivity summaries & focus tips.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 16.sp
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6E56CF))
                                )
                                Text(
                                    text = "Remember me",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Text(
                                text = "Forgot Password?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6E56CF),
                                modifier = Modifier.clickable { }
                            )
                        }
                    }

                    // CTA Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF6E56CF), Color(0xFF8B5CF6))
                                )
                            )
                            .clickable {
                                navController.navigate("dashboard") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isRegisterTab) "Create Account →" else "Sign In →",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Switch Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFEDE9FE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Login,
                                contentDescription = "Switch",
                                tint = Color(0xFF6E56CF),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = if (isRegisterTab) "Already have an account?" else "Need a new account?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = if (isRegisterTab) "Log in to sync" else "Register in seconds with seamless sync.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.clickable { isRegisterTab = !isRegisterTab },
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFF6E56CF))
                    ) {
                        Text(
                            text = if (isRegisterTab) "Sign In" else "Sign Up",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6E56CF)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Social Section Divider
            Text(
                text = if (isRegisterTab) "OR REGISTER WITH" else "OR CONTINUE WITH",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Social Login Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate("dashboard") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "G",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEA4335)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate("dashboard") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Apple",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }


@Composable
fun OtpVerificationScreen(navController: NavHostController) {
    var otpDigits by remember { mutableStateOf(listOf("4", "8", "2", "9", "1", "6")) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    ZenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ZenTaskTheme.TextPrimary)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(64.dp).background(Color.White, CircleShape).shadow(10.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Mail",
                        tint = ZenTaskTheme.Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Verification Code", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text("We sent a 6-digit code to alex.johnson@example.com", fontSize = 13.sp, color = ZenTaskTheme.TextSecondary, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(28.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    otpDigits.forEach { digit ->
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color.White, RoundedCornerShape(14.dp))
                                .border(1.5.dp, ZenTaskTheme.Primary, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(digit, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZenTaskTheme.TextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Resend code in 48s", fontSize = 12.sp, color = ZenTaskTheme.TextSubdued)
            }

            ZenPrimaryButton(
                text = "Verify & Activate Account →",
                onClick = { showSuccessDialog = true }
            )
        }

        if (showSuccessDialog) {
            Dialog(onDismissRequest = { showSuccessDialog = false }) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(ZenTaskTheme.StatusCompletedBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Success",
                                tint = ZenTaskTheme.StatusCompleted,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Account Activated", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Welcome to ZenTask. Your workspace is unlocked and ready.", fontSize = 13.sp, color = ZenTaskTheme.TextSecondary, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(20.dp))
                        ZenPrimaryButton(
                            text = "Go to Dashboard →",
                            onClick = {
                                showSuccessDialog = false
                                navController.navigate("dashboard") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1100)
@Composable
fun AuthRegisterScreenPreview() {
    ZenBackground {
        AuthScreen(
            navController = NavHostController(androidx.compose.ui.platform.LocalContext.current),
            initialRegister = true
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1000)
@Composable
fun AuthLoginScreenPreview() {
    ZenBackground {
        AuthScreen(
            navController = NavHostController(androidx.compose.ui.platform.LocalContext.current),
            initialRegister = false
        )
    }
}

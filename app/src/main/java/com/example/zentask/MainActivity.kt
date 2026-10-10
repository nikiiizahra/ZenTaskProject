package com.example.zentask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zentask.ui.screens.*
import com.example.zentask.viewmodel.ZenTaskViewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

object ZenTaskTheme {
    val Primary = Color(0xFF6E56CF)
    val PrimaryLight = Color(0xFFE1D9FF)
    val PrimaryContainer = Color(0xFFECE6FF)
    val Secondary = Color(0xFF38BDF8)
    val SecondaryContainer = Color(0xFFE0F2FE)

    // Background Gradient stops
    val BgStop1 = Color(0xFFF8F6FE)
    val BgStop2 = Color(0xFFF2EDFF)
    val BgStop3 = Color(0xFFFFF6FC)

    // Glass Card Surface & Border
    val GlassSurface = Color(0xD8FFFFFF)
    val GlassBorder = Color(0xEFFFFFFF)
    val GlassCardElevation = 8.dp

    // Glass Gradient Brushes for Buttons
    val ButtonPrimaryGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF6E56CF),
            Color(0xFF8B5CF6),
            Color(0xFF9E7AFF)
        )
    )

    val GlassButtonBorderBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0x99FFFFFF),
            Color(0x40FFFFFF)
        )
    )

    val SecondaryGlassGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xF2FFFFFF),
            Color(0xD9F3EEFF)
        )
    )

    // Text hierarchy
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF64748B)
    val TextSubdued = Color(0xFF94A3B8)

    // Statuses
    val StatusCompleted = Color(0xFF10B981)
    val StatusCompletedBg = Color(0xFFD1FAE5)
    val StatusInProgress = Color(0xFF38BDF8)
    val StatusInProgressBg = Color(0xFFE0F2FE)
    val StatusNotStarted = Color(0xFF94A3B8)
    val StatusNotStartedBg = Color(0xFFF1F5F9)

    // Priorities
    val PriorityLow = Color(0xFF60A5FA)
    val PriorityMedium = Color(0xFFF59E0B)
    val PriorityHigh = Color(0xFFF43F5E)
    val PriorityHighGlow = Color(0xFFFFE4E6)

    // Swatches
    val Swatches = listOf(
        Color(0xFF6E56CF),
        Color(0xFFF43F5E),
        Color(0xFF10B981),
        Color(0xFF38BDF8),
        Color(0xFFF59E0B),
        Color(0xFFEC4899),
        Color(0xFF8B5CF6)
    )

    val BackgroundBrush = Brush.linearGradient(
        colors = listOf(BgStop1, BgStop2, BgStop3),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1800f)
    )
}

@Composable
fun ZenTaskAppNavHost(vm: ZenTaskViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash Screen
        composable(
            "splash",
            exitTransition = { fadeOut(tween(500)) }
        ) { SplashScreen(navController) }

        composable(
            "onboarding/step1",
            enterTransition = { fadeIn(tween(500)) }
        ) { OnboardingStep1Screen(navController) }

        // Onboarding flow
        composable("onboarding/step1") { OnboardingStep1Screen(navController) }
        composable("onboarding/step2") { OnboardingStep2Screen(navController) }
        composable("onboarding/step3") { OnboardingStep3Screen(navController) }

        // Auth flow
        composable("auth") { AuthScreen(navController, initialRegister = false) }
        composable("auth/login") { AuthScreen(navController, initialRegister = false) }
        composable("auth/register") { AuthScreen(navController, initialRegister = true) }
        composable("auth/otp_verification") { OtpVerificationScreen(navController) }

        // Main app tabs
        composable("dashboard") { DashboardScreen(navController, vm) }
        composable("tasks") { MyTasksScreen(navController, vm) }
        composable("vital") { VitalTasksScreen(navController, vm) }
        composable("categories") { TaskCategoriesScreen(navController, vm) }
        composable("profile") { ProfileScreen(navController, vm) }

        // Secondary / Help screen
        composable("help_center") { HelpCenterScreen(navController) }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var keepSplash = true
        splashScreen.setKeepOnScreenCondition { keepSplash }
        Handler(Looper.getMainLooper()).postDelayed({ keepSplash = false }, 1500)
        setContent {
            ZenTaskAppNavHost()
        }
    }
}

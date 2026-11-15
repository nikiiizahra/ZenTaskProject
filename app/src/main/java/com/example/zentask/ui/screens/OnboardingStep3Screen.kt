
package com.example.zentask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.ui.components.ConfettiCanvasOverlay

@Composable
fun OnboardingStep3Screen(
    navController: NavController
) {
    var showConfetti by remember {
        mutableStateOf(false)
    }

    var selectedStartView by remember {
        mutableStateOf("Dashboard Overview")
    }

    // Both Skip Tour and Launch Workspace
    // must open the authentication screen.
    val goToAuth: () -> Unit = {
        navController.navigate("auth") {
            popUpTo("onboarding/step1") {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    OnboardingBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
        ) {

            // TOP BAR
            OnboardingTopBar(
                step = 3,
                chipText = "Step 3 of 3 • Final Step",
                dotColor = ZenTaskTheme.StatusCompleted,
                onSkip = {
                    goToAuth()
                }
            )

            // SCROLLABLE CONTENT
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // HERO CARD
                OnboardingGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 26.dp
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 20.dp
                            ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier.size(68.dp),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .shadow(
                                        12.dp,
                                        CircleShape,
                                        ambientColor =
                                            ZenTaskTheme.Primary,
                                        spotColor =
                                            ZenTaskTheme.Primary
                                    )
                                    .background(
                                        Color.White,
                                        CircleShape
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.RocketLaunch,
                                    contentDescription = null,
                                    tint = ZenTaskTheme.Primary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(18.dp)
                                    .background(
                                        Color(0xFFFFE4F0),
                                        CircleShape
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFEC4899),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    ZenTaskTheme.PrimaryContainer,
                                    RoundedCornerShape(50)
                                )
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = ZenTaskTheme.Primary,
                                modifier = Modifier.size(10.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            Text(
                                text = "YOU'RE ALL SET",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp,
                                color = ZenTaskTheme.Primary
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Your Mindful Workspace is Ready to Launch!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ZenTaskTheme.TextPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 27.sp
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Explore your productivity dashboard, organize tasks by category, and let smart urgency scoring highlight what matters most.",
                            fontSize = 12.sp,
                            color = ZenTaskTheme.TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // FEATURE SUMMARY
                OnboardingGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp
                ) {

                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment =
                                Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "Explore ZenTask Features",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ZenTaskTheme.TextSecondary
                            )

                            Text(
                                text = "Ready to Explore",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color =
                                    ZenTaskTheme.StatusCompleted,
                                modifier = Modifier
                                    .background(
                                        ZenTaskTheme.StatusCompletedBg,
                                        RoundedCornerShape(50)
                                    )
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 3.dp
                                    )
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        CapabilityRow(
                            title = "Sample Tasks Available",
                            tags = listOf(
                                "UI Design",
                                "Study"
                            )
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        CapabilityRow(
                            title = "Smart Vital Task Detection",
                            subtitle = "Urgency scoring based on task deadlines and priority"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "Preview your starting view:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZenTaskTheme.TextSecondary,
                    modifier = Modifier.padding(
                        horizontal = 2.dp
                    )
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // START VIEW SELECTION PREVIEW
                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    StartViewCard(
                        icon = Icons.Outlined.Dashboard,
                        title = "Dashboard Overview",
                        subtitle = "Recommended",
                        isTag = true,
                        selected =
                            selectedStartView ==
                                    "Dashboard Overview",
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedStartView =
                            "Dashboard Overview"
                    }

                    StartViewCard(
                        icon = Icons.Outlined.AddTask,
                        title = "Create My First Task",
                        subtitle = "Quick Capture",
                        isTag = false,
                        selected =
                            selectedStartView ==
                                    "Create My First Task",
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedStartView =
                            "Create My First Task"
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "This selection is a preview. Sign in first to access your workspace.",
                    fontSize = 10.sp,
                    color = ZenTaskTheme.TextSubdued,
                    modifier = Modifier.padding(
                        horizontal = 2.dp
                    )
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            // BOTTOM NAVIGATION
            OnboardingPageDots(
                selected = 3
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // Launch opens Login/Register,
            // not the Dashboard directly.
            OnboardingPrimaryButton(
                "🚀  Continue to Sign In"
            ) {
                goToAuth()
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "‹ Back to Step 2",
                fontSize = 12.sp,
                color = ZenTaskTheme.TextSecondary,
                modifier = Modifier
                    .align(
                        Alignment.CenterHorizontally
                    )
                    .clickable {
                        navController.popBackStack()
                    }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Tour complete • ZenTask v2.4",
                fontSize = 10.sp,
                color = ZenTaskTheme.TextSubdued,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
        }

        if (showConfetti) {
            ConfettiCanvasOverlay()
        }
    }
}

@Composable
private fun CapabilityRow(
    title: String,
    tags: List<String> = emptyList(),
    subtitle: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.85f),
                RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = ZenTaskTheme.StatusCompleted,
            modifier = Modifier.size(22.dp)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column {

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZenTaskTheme.TextPrimary
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            if (tags.isNotEmpty()) {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    tags.forEach { tag ->

                        Text(
                            text = tag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZenTaskTheme.Primary,
                            modifier = Modifier
                                .background(
                                    ZenTaskTheme.PrimaryContainer,
                                    RoundedCornerShape(50)
                                )
                                .padding(
                                    horizontal = 7.dp,
                                    vertical = 2.dp
                                )
                        )
                    }
                }
            }

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = ZenTaskTheme.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun StartViewCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isTag: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .shadow(
                elevation =
                    if (selected) 8.dp else 2.dp,
                shape = shape,
                ambientColor =
                    ZenTaskTheme.Primary.copy(
                        alpha = 0.2f
                    )
            )
            .background(
                color =
                    if (selected) {
                        Color.White
                    } else {
                        Color.White.copy(
                            alpha = 0.7f
                        )
                    },
                shape = shape
            )
            .border(
                width =
                    if (selected) 1.5.dp else 1.dp,
                color =
                    if (selected) {
                        ZenTaskTheme.Primary.copy(
                            alpha = 0.5f
                        )
                    } else {
                        ZenTaskTheme.GlassBorder
                    },
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        ZenTaskTheme.PrimaryContainer,
                        CircleShape
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ZenTaskTheme.Primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Icon(
                imageVector =
                    if (selected) {
                        Icons.Filled.RadioButtonChecked
                    } else {
                        Icons.Outlined.RadioButtonUnchecked
                    },
                contentDescription = null,
                tint =
                    if (selected) {
                        ZenTaskTheme.Primary
                    } else {
                        ZenTaskTheme.TextSubdued
                    },
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = ZenTaskTheme.TextPrimary
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        if (isTag) {

            Text(
                text = subtitle,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = ZenTaskTheme.Primary,
                modifier = Modifier
                    .background(
                        ZenTaskTheme.PrimaryContainer,
                        RoundedCornerShape(50)
                    )
                    .padding(
                        horizontal = 7.dp,
                        vertical = 2.dp
                    )
            )

        } else {

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = ZenTaskTheme.TextSecondary
            )
        }
    }
}

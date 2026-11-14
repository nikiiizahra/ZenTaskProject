
package com.example.zentask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zentask.ZenTaskTheme

@Composable
fun OnboardingStep1Screen(
    navController: NavController
) {
    var selectedFocus by remember {
        mutableStateOf("Deep Study")
    }

    // Skip Tour should open Login/Register,
    // not bypass authentication.
    val goToAuth: () -> Unit = {
        navController.navigate("auth") {
            popUpTo("onboarding/step1") {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZenTaskTheme.BackgroundBrush)
    ) {

        // Background glow
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 90.dp)
                .size(260.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFFFFD6E8).copy(
                                alpha = 0.6f
                            ),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

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
                step = 1,
                chipText = "Step 1 of 3 • 1-Min Tour",
                onSkip = {
                    goToAuth()
                }
            )

            // SCROLLABLE CONTENT
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {

                Spacer(
                    modifier = Modifier.height(28.dp)
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
                                vertical = 22.dp
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
                                        Icons.Filled.AutoAwesome,
                                    contentDescription =
                                        "ZenTask logo",
                                    tint =
                                        ZenTaskTheme.Primary,
                                    modifier =
                                        Modifier.size(28.dp)
                                )
                            }

                            Text(
                                text = "👑",
                                fontSize = 13.sp,
                                modifier = Modifier.align(
                                    Alignment.TopEnd
                                )
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text = "WELCOME TO ZENTASK",
                            fontSize = 10.sp,
                            fontWeight =
                                FontWeight.SemiBold,
                            letterSpacing = 0.6.sp,
                            color =
                                ZenTaskTheme.Primary,
                            modifier = Modifier
                                .background(
                                    ZenTaskTheme.PrimaryContainer,
                                    RoundedCornerShape(50)
                                )
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 4.dp
                                )
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        Text(
                            text = "Focus Without Chaos.\nMindful Planning.",
                            fontSize = 24.sp,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color =
                                ZenTaskTheme.TextPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text = "Experience a calm, distraction-free productivity space crafted to organize tasks with gentle clarity and zero cognitive clutter.",
                            fontSize = 13.sp,
                            color =
                                ZenTaskTheme.TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // CORE PHILOSOPHY
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "OUR CORE PHILOSOPHY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        color =
                            ZenTaskTheme.TextSecondary
                    )

                    Text(
                        text = "3 Mindful Pillars",
                        fontSize = 10.sp,
                        fontWeight =
                            FontWeight.SemiBold,
                        color =
                            ZenTaskTheme.Primary
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                PillarCard(
                    icon = Icons.Outlined.Spa,
                    tint =
                        ZenTaskTheme.StatusCompleted,
                    iconBg =
                        ZenTaskTheme.StatusCompletedBg,
                    title = "Pure Intention",
                    desc = "One primary focus at a time to eliminate decision fatigue."
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                PillarCard(
                    icon = Icons.Outlined.Schedule,
                    tint =
                        ZenTaskTheme.StatusInProgress,
                    iconBg =
                        ZenTaskTheme.SecondaryContainer,
                    title = "Gentle Urgency",
                    desc = "Smart automatic vital filters surface what matters without panic."
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // PRIMARY FOCUS
                OnboardingGlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 14.dp
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "Your Primary Focus Today:",
                                fontSize = 12.sp,
                                fontWeight =
                                    FontWeight.Medium,
                                color =
                                    ZenTaskTheme.TextPrimary
                            )

                            Text(
                                text = "Select one",
                                fontSize = 11.sp,
                                fontWeight =
                                    FontWeight.SemiBold,
                                color =
                                    ZenTaskTheme.Primary
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(6.dp)
                        ) {

                            FocusChip(
                                label = "Deep Study",
                                icon =
                                    Icons.Outlined.MenuBook,
                                selected =
                                    selectedFocus ==
                                            "Deep Study",
                                modifier =
                                    Modifier.weight(1f),
                                onClick = {
                                    selectedFocus =
                                        "Deep Study"
                                }
                            )

                            FocusChip(
                                label = "Work & Projects",
                                icon =
                                    Icons.Outlined.WorkOutline,
                                selected =
                                    selectedFocus ==
                                            "Work & Projects",
                                modifier =
                                    Modifier.weight(1.35f),
                                onClick = {
                                    selectedFocus =
                                        "Work & Projects"
                                }
                            )

                            FocusChip(
                                label = "Life Balance",
                                icon =
                                    Icons.Outlined.Spa,
                                selected =
                                    selectedFocus ==
                                            "Life Balance",
                                modifier =
                                    Modifier.weight(1.1f),
                                onClick = {
                                    selectedFocus =
                                        "Life Balance"
                                }
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            // FIXED BOTTOM NAVIGATION
            OnboardingPageDots(
                selected = 1
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // Continue the onboarding tour
            OnboardingPrimaryButton(
                "Next: Mindful Urgency (Step 2)  →"
            ) {
                navController.navigate(
                    "onboarding/step2"
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Skip the tour, but do not skip Login.
            Text(
                text = "Already familiar? Skip the tour",
                fontSize = 12.sp,
                color = ZenTaskTheme.TextSecondary,
                modifier = Modifier
                    .align(
                        Alignment.CenterHorizontally
                    )
                    .clickable {
                        goToAuth()
                    }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Estimated time left: 60 seconds • ZenTask v2.4",
                fontSize = 10.sp,
                color = ZenTaskTheme.TextSubdued,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
        }
    }
}

@Composable
private fun PillarCard(
    icon: ImageVector,
    tint: Color,
    iconBg: Color,
    title: String,
    desc: String
) {
    OnboardingGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        iconBg,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        ZenTaskTheme.TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = desc,
                    fontSize = 12.sp,
                    color =
                        ZenTaskTheme.TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun FocusChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(50)

    Row(
        modifier = modifier
            .clip(shape)
            .background(
                if (selected) {
                    ZenTaskTheme.Primary
                } else {
                    Color.White
                },
                shape
            )
            .clickable(
                onClick = onClick
            )
            .padding(
                horizontal = 6.dp,
                vertical = 11.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) {
                Color.White
            } else {
                ZenTaskTheme.TextSecondary
            },
            modifier = Modifier.size(13.dp)
        )

        Spacer(
            modifier = Modifier.width(4.dp)
        )

        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            color = if (selected) {
                Color.White
            } else {
                ZenTaskTheme.TextSecondary
            }
        )
    }
}

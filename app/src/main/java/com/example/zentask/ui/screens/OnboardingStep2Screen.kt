
package com.example.zentask.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zentask.ZenTaskTheme

@Composable
fun OnboardingStep2Screen(
    navController: NavController
) {
    // Skip Tour opens Login/Register.
    // The onboarding screens are removed from back stack.
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
                step = 2,
                chipText = "Step 2 of 3 • 1-Min Tour",
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
                    modifier = Modifier.height(24.dp)
                )

                // MAIN CARD
                OnboardingGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = ZenTaskTheme.Primary,
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = "QUICK TOUR • STEP 2",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp,
                                color = ZenTaskTheme.Primary
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text = "Mindful Task Management & Vital Urgency",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ZenTaskTheme.TextPrimary,
                            lineHeight = 27.sp
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Organize your day with smart urgency scoring, vital task detection, custom categories, and task collaboration demos.",
                            fontSize = 12.sp,
                            color = ZenTaskTheme.TextSecondary,
                            lineHeight = 17.sp
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        // SAMPLE URGENT TASK
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFFFFF5F6),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        ZenTaskTheme.PriorityHighGlow,
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.HourglassBottom,
                                    contentDescription = null,
                                    tint = ZenTaskTheme.PriorityHigh,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = "Finalize Client Pitch Deck",
                                    fontSize = 12.sp,
                                    fontWeight =
                                        FontWeight.SemiBold,
                                    color =
                                        ZenTaskTheme.TextPrimary
                                )

                                Spacer(
                                    modifier = Modifier.height(2.dp)
                                )

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = "14h left",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color =
                                            ZenTaskTheme.PriorityHigh
                                    )

                                    Text(
                                        text = "  •  ",
                                        fontSize = 10.sp,
                                        color =
                                            ZenTaskTheme.TextSubdued
                                    )

                                    Text(
                                        text = "High",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color =
                                            ZenTaskTheme.PriorityHigh
                                    )
                                }
                            }

                            Icon(
                                imageVector =
                                    Icons.Outlined.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = ZenTaskTheme.TextSubdued,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        // TWO MINI CARDS
                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        Color.White.copy(
                                            alpha = 0.8f
                                        ),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(10.dp),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(
                                            ZenTaskTheme.PrimaryContainer,
                                            RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Outlined.Palette,
                                        contentDescription = null,
                                        tint = ZenTaskTheme.Primary,
                                        modifier =
                                            Modifier.size(16.dp)
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Column {

                                    Text(
                                        text = "UI Design",
                                        fontSize = 11.sp,
                                        fontWeight =
                                            FontWeight.SemiBold,
                                        color =
                                            ZenTaskTheme.TextPrimary
                                    )

                                    Text(
                                        text = "12 Tasks",
                                        fontSize = 10.sp,
                                        color =
                                            ZenTaskTheme.TextSecondary
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        Color.White.copy(
                                            alpha = 0.8f
                                        ),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(10.dp),
                                verticalAlignment =
                                    Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.SpaceBetween
                            ) {

                                Column {

                                    Text(
                                        text = "Flow Score",
                                        fontSize = 11.sp,
                                        fontWeight =
                                            FontWeight.SemiBold,
                                        color =
                                            ZenTaskTheme.TextPrimary
                                    )

                                    Text(
                                        text = "Optimal",
                                        fontSize = 10.sp,
                                        fontWeight =
                                            FontWeight.Medium,
                                        color =
                                            ZenTaskTheme.StatusCompleted
                                    )
                                }

                                FlowRing(
                                    progress = 0.78f,
                                    label = "78%"
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                // SMART VITAL FILTER
                FeatureCard(
                    icon = Icons.Outlined.FilterAlt,
                    tint = ZenTaskTheme.PriorityHigh,
                    iconBg = ZenTaskTheme.PriorityHighGlow,
                    title = "Smart Vital Filter",
                    desc = "Automatically highlights urgent tasks based on deadlines, priority, and progress."
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // CATEGORY AND COLLABORATION FEATURES
                FeatureCard(
                    icon = Icons.Outlined.CloudSync,
                    tint = ZenTaskTheme.StatusInProgress,
                    iconBg = ZenTaskTheme.SecondaryContainer,
                    title = "Categories & Collaboration",
                    desc = "Organize tasks by category and explore task sharing through demo collaboration codes."
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            // BOTTOM NAVIGATION
            OnboardingPageDots(
                selected = 2
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            OnboardingPrimaryButton(
                "Next: Explore Categories  →"
            ) {
                navController.navigate(
                    "onboarding/step3"
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.Center,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "← Back to Previous Step",
                    fontSize = 12.sp,
                    color = ZenTaskTheme.TextSecondary,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )

                Spacer(
                    modifier = Modifier.width(24.dp)
                )

                // Skip tour without bypassing authentication.
                Text(
                    text = "Skip Tour",
                    fontSize = 12.sp,
                    color = ZenTaskTheme.TextSecondary,
                    modifier = Modifier.clickable {
                        goToAuth()
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Estimated time left: 40 seconds • ZenTask v2.4",
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
private fun FeatureCard(
    icon: ImageVector,
    tint: Color,
    iconBg: Color,
    title: String,
    desc: String
) {
    OnboardingGlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp
    ) {

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
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

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZenTaskTheme.TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = ZenTaskTheme.TextSecondary,
                    lineHeight = 15.sp
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Icon(
                imageVector =
                    Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = ZenTaskTheme.TextSubdued,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun FlowRing(
    progress: Float,
    label: String
) {
    Box(
        modifier = Modifier.size(38.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val width = 4.dp.toPx()

            val arcSize = Size(
                size.width - width,
                size.height - width
            )

            val topLeft = Offset(
                width / 2f,
                width / 2f
            )

            drawArc(
                color = ZenTaskTheme.StatusCompletedBg,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width)
            )

            drawArc(
                color = ZenTaskTheme.StatusCompleted,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width,
                    cap = StrokeCap.Round
                )
            )
        }

        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = ZenTaskTheme.TextPrimary
        )
    }
}

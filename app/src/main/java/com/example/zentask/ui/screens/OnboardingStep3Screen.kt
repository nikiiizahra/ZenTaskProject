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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OnboardingStep3Screen(navController: NavController) {
    var showConfetti by remember { mutableStateOf(false) }
    var selectedStartView by remember { mutableStateOf("Dashboard Overview") }
    val coroutineScope = rememberCoroutineScope()

    OnboardingBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            OnboardingTopBar(
                step = 3,
                chipText = "Step 3 of 3 • Final Step",
                dotColor = ZenTaskTheme.StatusCompleted,
                onSkip = { navController.navigate("auth") }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(16.dp))

                // Kartu hero
                OnboardingGlassCard(Modifier.fillMaxWidth(), cornerRadius = 26.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(Modifier.size(68.dp), contentAlignment = Alignment.Center) {
                            Box(
                                Modifier
                                    .size(54.dp)
                                    .shadow(12.dp, CircleShape, ambientColor = ZenTaskTheme.Primary, spotColor = ZenTaskTheme.Primary)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.RocketLaunch, null, tint = ZenTaskTheme.Primary, modifier = Modifier.size(26.dp))
                            }
                            Box(
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .size(18.dp)
                                    .background(Color(0xFFFFE4F0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.AutoAwesome, null, tint = Color(0xFFEC4899), modifier = Modifier.size(10.dp))
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(ZenTaskTheme.PrimaryContainer, RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, null, tint = ZenTaskTheme.Primary, modifier = Modifier.size(10.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "YOU'RE ALL SET",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp,
                                color = ZenTaskTheme.Primary
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Your Mindful Workspace is Ready to Launch!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ZenTaskTheme.TextPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 27.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "We've pre-configured your personal dashboard with smart vital filters, pastel color swatches, and real-time cloud backup.",
                            fontSize = 12.sp,
                            color = ZenTaskTheme.TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Activated Capabilities
                OnboardingGlassCard(Modifier.fillMaxWidth(), cornerRadius = 22.dp) {
                    Column(Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Activated Capabilities", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = ZenTaskTheme.TextSecondary)
                            Text(
                                "3 / 3 Ready",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ZenTaskTheme.StatusCompleted,
                                modifier = Modifier
                                    .background(ZenTaskTheme.StatusCompletedBg, RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        CapabilityRow("3 Sample Focus Tasks Loaded", tags = listOf("UI Design", "Study"))
                        Spacer(Modifier.height(8.dp))
                        CapabilityRow("Vital Urgency Filter Activated", subtitle = "< 48 hrs alerts enabled")
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    "Choose your starting view:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZenTaskTheme.TextSecondary,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StartViewCard(
                        icon = Icons.Outlined.Dashboard,
                        title = "Dashboard Overview",
                        subtitle = "Recommended",
                        isTag = true,
                        selected = selectedStartView == "Dashboard Overview",
                        modifier = Modifier.weight(1f)
                    ) { selectedStartView = "Dashboard Overview" }
                    StartViewCard(
                        icon = Icons.Outlined.AddTask,
                        title = "Create My First Task",
                        subtitle = "Quick Capture",
                        isTag = false,
                        selected = selectedStartView == "Create My First Task",
                        modifier = Modifier.weight(1f)
                    ) { selectedStartView = "Create My First Task" }
                }

                Spacer(Modifier.height(12.dp))
            }

            // Bagian bawah
            OnboardingPageDots(selected = 3)
            Spacer(Modifier.height(14.dp))
            OnboardingPrimaryButton("🚀  Launch Workspace & Start") {
                navController.navigate("auth") {
                    popUpTo("onboarding/step1") { inclusive = true }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "‹ Back to Step 2",
                fontSize = 12.sp,
                color = ZenTaskTheme.TextSecondary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { navController.popBackStack() }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Completed in 55 seconds • ZenTask v2.4",
                fontSize = 10.sp,
                color = ZenTaskTheme.TextSubdued,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (showConfetti) {
            ConfettiCanvasOverlay()
        }
    }
}

@Composable
private fun CapabilityRow(title: String, tags: List<String> = emptyList(), subtitle: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.CheckCircle, null, tint = ZenTaskTheme.StatusCompleted, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.TextPrimary)
            Spacer(Modifier.height(3.dp))
            if (tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    tags.forEach {
                        Text(
                            it,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZenTaskTheme.Primary,
                            modifier = Modifier
                                .background(ZenTaskTheme.PrimaryContainer, RoundedCornerShape(50))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            if (subtitle != null) {
                Text(subtitle, fontSize = 10.sp, color = ZenTaskTheme.TextSecondary)
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
            .shadow(if (selected) 8.dp else 2.dp, shape, ambientColor = ZenTaskTheme.Primary.copy(alpha = 0.2f))
            .background(if (selected) Color.White else Color.White.copy(alpha = 0.7f), shape)
            .border(
                if (selected) 1.5.dp else 1.dp,
                if (selected) ZenTaskTheme.Primary.copy(alpha = 0.5f) else ZenTaskTheme.GlassBorder,
                shape
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                Modifier.size(32.dp).background(ZenTaskTheme.PrimaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = ZenTaskTheme.Primary, modifier = Modifier.size(16.dp)) }
            Icon(
                if (selected) Icons.Filled.RadioButtonChecked else Icons.Outlined.RadioButtonUnchecked,
                null,
                tint = if (selected) ZenTaskTheme.Primary else ZenTaskTheme.TextSubdued,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.TextPrimary)
        Spacer(Modifier.height(4.dp))
        if (isTag) {
            Text(
                subtitle,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = ZenTaskTheme.Primary,
                modifier = Modifier
                    .background(ZenTaskTheme.PrimaryContainer, RoundedCornerShape(50))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            )
        } else {
            Text(subtitle, fontSize = 10.sp, color = ZenTaskTheme.TextSecondary)
        }
    }
}
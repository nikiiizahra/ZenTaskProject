package com.example.zentask.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OnboardingStep1Screen(navController: NavHostController) {
    var selectedFocus by remember { mutableStateOf("Study") }

    ZenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ZenTaskTheme.PrimaryContainer,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        "Step 1 of 3 • Philosophy",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        color = ZenTaskTheme.Primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
                TextButton(onClick = { navController.navigate("auth") }) {
                    Text("Skip Tour", color = ZenTaskTheme.TextSecondary, fontSize = 13.sp)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White, CircleShape)
                        .shadow(10.dp, CircleShape, spotColor = Color(0x336E56CF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = "Mindful",
                        tint = ZenTaskTheme.Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Focus Without Chaos.\nMindful Planning.",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTaskTheme.TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Experience mindful task flow with pastel glass aesthetics designed to eliminate cognitive fatigue.",
                    fontSize = 14.sp,
                    color = ZenTaskTheme.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PhilosophyPillarItem("Focus", "Pure Intention", "Single-point focus to prevent cognitive overload.")
                PhilosophyPillarItem("Alert", "Gentle Urgency", "Automated smart alert for tasks under 48 hours.")
                PhilosophyPillarItem("Design", "Aesthetic Harmony", "Translucent glass surfaces designed to soothe eyes.")
            }

            Column {
                Text(
                    "Select Your Primary Daily Focus:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZenTaskTheme.TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Study", "Work", "Life").forEach { chip ->
                        val isSel = selectedFocus == chip
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedFocus = chip },
                            label = { Text(chip) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZenTaskTheme.Primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(999.dp)
                        )
                    }
                }
            }

            ZenPrimaryButton(
                text = "Continue: 1-Min Tour →",
                onClick = { navController.navigate("onboarding/step2") }
            )
        }
    }
}

@Composable
fun OnboardingStep2Screen(navController: NavHostController) {
    ZenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ZenTaskTheme.PrimaryContainer,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        "Step 2 of 3 • 1-Min Tour",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        color = ZenTaskTheme.Primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
                TextButton(onClick = { navController.navigate("auth") }) {
                    Text("Skip Tour", color = ZenTaskTheme.TextSecondary, fontSize = 13.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                GlassCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "Vital",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Smart Vital Filter", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZenTaskTheme.TextPrimary)
                            Text("High priority tasks due in <48 hours automatically bubble to your vital focus dock.", fontSize = 13.sp, color = ZenTaskTheme.TextSecondary)
                        }
                    }
                }
                GlassCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = "Palette",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Pastel Color Swatches", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZenTaskTheme.TextPrimary)
                            Text("Categorize your goals with 7 aesthetic mindful pastel color palettes.", fontSize = 13.sp, color = ZenTaskTheme.TextSecondary)
                        }
                    }
                }
                GlassCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CloudSync,
                            contentDescription = "Sync",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Instant Cloud Sync & Collab", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ZenTaskTheme.TextPrimary)
                            Text("Share 6-digit invite codes to work together with peers and colleagues.", fontSize = 13.sp, color = ZenTaskTheme.TextSecondary)
                        }
                    }
                }
            }

            ZenPrimaryButton(
                text = "Next: Ready to Launch →",
                onClick = { navController.navigate("onboarding/step3") }
            )
        }
    }
}

@Composable
fun OnboardingStep3Screen(navController: NavHostController) {
    var showConfetti by remember { mutableStateOf(false) }
    var selectedStartView by remember { mutableStateOf("Dashboard Overview") }
    val coroutineScope = rememberCoroutineScope()

    ZenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        "Step 3 of 3 • Final Step",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        color = Color(0xFF047857),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
                TextButton(onClick = { navController.navigate("auth") }) {
                    Text("Skip Tour", color = ZenTaskTheme.TextSecondary, fontSize = 13.sp)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White, CircleShape)
                        .shadow(12.dp, CircleShape, spotColor = Color(0x336E56CF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RocketLaunch,
                        contentDescription = "Launch",
                        tint = ZenTaskTheme.Primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Surface(color = ZenTaskTheme.PrimaryContainer, shape = RoundedCornerShape(999.dp)) {
                    Text("YOU'RE ALL SET", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = ZenTaskTheme.Primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Your Mindful Workspace is Ready to Launch!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTaskTheme.TextPrimary,
                    textAlign = TextAlign.Center
                )
            }

            GlassCard {
                Text("Activated Capabilities", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZenTaskTheme.TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                ReadyCheckRow("3 Sample Focus Tasks Loaded", "#UI-Design • #Study")
                Spacer(modifier = Modifier.height(8.dp))
                ReadyCheckRow("Vital Urgency Filter Activated", "< 48 hrs alerts enabled")
                Spacer(modifier = Modifier.height(8.dp))
                ReadyCheckRow("Instant Sync & Encryption Active", "Connected securely")
            }

            Column {
                Text("Choose your starting view:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Dashboard Overview", "Create My First Task").forEach { viewName ->
                        val isSel = selectedStartView == viewName
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isSel) Color.White else Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .border(if (isSel) 2.dp else 1.dp, if (isSel) ZenTaskTheme.Primary else ZenTaskTheme.GlassBorder, RoundedCornerShape(16.dp))
                                .clickable { selectedStartView = viewName }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(if (viewName.contains("Dashboard")) "Dashboard" else "Task Editor", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = ZenTaskTheme.TextPrimary)
                            }
                        }
                    }
                }
            }

            ZenPrimaryButton(
                text = "Continue to Login / Register →",
                onClick = {
                    showConfetti = true
                    coroutineScope.launch {
                        delay(1200)
                        navController.navigate("auth") {
                            popUpTo("onboarding/step1") { inclusive = true }
                        }
                    }
                }
            )
        }

        if (showConfetti) {
            ConfettiCanvasOverlay()
        }
    }
}

